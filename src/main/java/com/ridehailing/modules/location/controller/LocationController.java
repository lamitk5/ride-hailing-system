package com.ridehailing.modules.location.controller;

import com.ridehailing.modules.location.service.DriverLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final DriverLocationService driverLocationService;

    // REST endpoint: Customer queries nearby drivers
    @GetMapping("/nearby")
    public ResponseEntity<List<String>> getNearbyDrivers(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5.0") double radius) {
        
        List<String> drivers = driverLocationService.findNearbyDrivers(lon, lat, radius);
        return ResponseEntity.ok(drivers);
    }
}
