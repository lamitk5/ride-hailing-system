package com.ridehailing.modules.ride.service;

import com.ridehailing.modules.driver.entity.Driver;
import com.ridehailing.modules.ride.dto.RideRequestDTO;
import com.ridehailing.modules.ride.dto.RideResponseDTO;
import com.ridehailing.modules.ride.entity.Ride;
import com.ridehailing.modules.ride.event.RideRequestEvent;
import com.ridehailing.modules.ride.repository.RideRepository;
import com.ridehailing.modules.user.entity.User;
import com.ridehailing.modules.vehicle.entity.VehicleType;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.EntityManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideService {

    private final RideRepository rideRepository;
    private final EntityManager entityManager;
    private final RidePublisherService ridePublisherService;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public RideResponseDTO requestRide(RideRequestDTO request) {
        log.info("User {} is requesting a ride.", request.getUserId());
        // 1. Convert Coordinates to JTS Point (Longitude first, then Latitude)
        Point pickup = geometryFactory.createPoint(new Coordinate(request.getPickupLng(), request.getPickupLat()));
        Point dropoff = geometryFactory.createPoint(new Coordinate(request.getDropoffLng(), request.getDropoffLat()));

        // 2. Load References lazily without extra DB query to set FK
        User user = entityManager.getReference(User.class, request.getUserId());
        VehicleType vehicleType = entityManager.getReference(VehicleType.class, request.getVehicleTypeId());

        // 3. Estimate distance using Haversine formula
        BigDecimal estimatedDistance = calculateDistance(request.getPickupLat(), request.getPickupLng(),
                request.getDropoffLat(), request.getDropoffLng());
        BigDecimal actualFare = calculateFare(estimatedDistance, vehicleType); // Dynamic formula

        // 4. Create Ride with Snapshot of Price
        Ride ride = Ride.builder()
                .user(user)
                .vehicleType(vehicleType)
                .pickupLocation(pickup)
                .dropoffLocation(dropoff)
                .status("REQUESTED")
                .estimatedDistance(estimatedDistance)
                .actualFare(actualFare)
                .build();

        ride = rideRepository.save(ride);

        // 5. Broadcast to nearby drivers (Redis / Kafka integration will be here)
        // 5. Broadcast to nearby drivers (Dùng Redis Pub/Sub)
        log.info("New ride created ID: {}. Broadcasting to drivers...", ride.getId());

        RideRequestEvent event = new RideRequestEvent(
                ride.getId(),
                user.getId(),
                request.getPickupLat(),
                request.getPickupLng(),
                actualFare);
        ridePublisherService.broadcastNewRide(event);

        return mapToResponse(ride);

    }

    @Transactional
    public RideResponseDTO acceptRide(UUID rideId, UUID driverId) {
        log.info("Driver {} is trying to accept ride {}", driverId, rideId);

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!"REQUESTED".equals(ride.getStatus())) {
            throw new RuntimeException("Ride is no longer available or already accepted by another driver.");
        }

        Driver driver = entityManager.getReference(Driver.class, driverId);
        ride.setDriver(driver);
        ride.setStatus("ACCEPTED");

        // Optimistic Locking will automatically handle concurrency here due to @Version
        // in the Entity.
        // If 2 drivers run this at the exact same time, JPA will throw
        // ObjectOptimisticLockingFailureException
        // for the second commit.
        ride = rideRepository.save(ride);

        log.info("Driver {} successfully accepted ride {}", driverId, rideId);
        return mapToResponse(ride);
    }

    private BigDecimal calculateDistance(double startLat, double startLng, double endLat, double endLng) {
        int earthRadiusKm = 6371;
        double dLat = Math.toRadians(endLat - startLat);
        double dLon = Math.toRadians(endLng - startLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return BigDecimal.valueOf(earthRadiusKm * c);
    }

    private BigDecimal calculateFare(BigDecimal distance, VehicleType vehicleType) {
        return vehicleType.getBaseFare().add(distance.multiply(vehicleType.getPricePerKm()));
    }

    private RideResponseDTO mapToResponse(Ride ride) {
        return RideResponseDTO.builder()
                .rideId(ride.getId())
                .status(ride.getStatus())
                .driverId(ride.getDriver() != null ? ride.getDriver().getId() : null)
                .estimatedDistance(ride.getEstimatedDistance())
                .actualFare(ride.getActualFare())
                .createdAt(ride.getCreatedAt())
                .build();
    }
}
