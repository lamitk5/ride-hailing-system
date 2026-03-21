package com.ridehailing.modules.ride.controller;

import com.ridehailing.modules.ride.dto.RideRequestDTO;
import com.ridehailing.modules.ride.dto.RideResponseDTO;
import com.ridehailing.modules.ride.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping("/request")
    public ResponseEntity<RideResponseDTO> requestRide(@RequestBody RideRequestDTO requestDTO) {
        RideResponseDTO response = rideService.requestRide(requestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rideId}/accept")
    public ResponseEntity<RideResponseDTO> acceptRide(
            @PathVariable UUID rideId,
            @RequestParam UUID driverId) {
        // Driver ID should usually come from SecurityContext/JWT.
        // For testing/simulation, we take it from parameter.
        RideResponseDTO response = rideService.acceptRide(rideId, driverId);
        return ResponseEntity.ok(response);
    }
}
