package com.ridehailing.modules.vehicle.repository;
import com.ridehailing.modules.vehicle.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType, UUID> {
    java.util.Optional<VehicleType> findByName(String name);
}
