package com.ridehailing.modules.ride.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class RideResponseDTO {
    private UUID rideId;
    private String status;
    private UUID driverId;
    private BigDecimal estimatedDistance;
    private BigDecimal actualFare;
    private Instant createdAt;
}
