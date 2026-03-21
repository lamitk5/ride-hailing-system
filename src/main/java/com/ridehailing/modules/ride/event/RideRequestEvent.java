package com.ridehailing.modules.ride.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestEvent {
    private UUID rideId;
    private UUID customerId;
    private double pickupLat;
    private double pickupLng;
    private BigDecimal estimatedFare;
}
