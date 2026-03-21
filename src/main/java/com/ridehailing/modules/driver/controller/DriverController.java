package com.ridehailing.modules.driver.controller;

import com.ridehailing.modules.driver.dto.DriverRegistrationRequest;
import com.ridehailing.modules.driver.entity.Driver;
import com.ridehailing.modules.driver.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/register")
    public ResponseEntity<Driver> registerDriver(@RequestBody DriverRegistrationRequest request) {
        Driver driver = driverService.registerDriver(request);
        return ResponseEntity.ok(driver);
    }
}
