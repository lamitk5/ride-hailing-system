package com.ridehailing.modules.ride.entity;

import com.ridehailing.common.entity.BaseEntity;
import com.ridehailing.modules.driver.entity.Driver;
import com.ridehailing.modules.user.entity.User;
import com.ridehailing.modules.vehicle.entity.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "rides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver; // NULLABLE initally

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "pickup_location", columnDefinition = "Geometry(Point,4326)", nullable = false)
    private Point pickupLocation;

    @Column(name = "dropoff_location", columnDefinition = "Geometry(Point,4326)", nullable = false)
    private Point dropoffLocation;

    @Column(name = "status", length = 30, nullable = false)
    private String status; // REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(name = "estimated_distance")
    private BigDecimal estimatedDistance;

    @Column(name = "actual_fare")
    private BigDecimal actualFare;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "completed_at")
    private Instant completedAt;
}
