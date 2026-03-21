package com.ridehailing.modules.ride.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class RideRequestDTO {
    private UUID userId;
    private UUID vehicleTypeId;
    private double pickupLat;
    private double pickupLng;
    private double dropoffLat;
    private double dropoffLng;
}
