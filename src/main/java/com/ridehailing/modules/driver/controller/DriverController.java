package com.ridehailing.modules.driver.controller;

import com.ridehailing.config.security.JwtTokenProvider;
import com.ridehailing.modules.account.dto.LoginResponseDTO;
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
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> registerDriver(@RequestBody DriverRegistrationRequest request) {
        Driver driver = driverService.registerDriver(request);
        String token = tokenProvider.generateToken(driver.getAccount().getPhoneNumber(), driver.getAccount().getRole());
        return ResponseEntity.ok(LoginResponseDTO.builder()
                .token(token)
                .userId(driver.getId().toString())
                .role(driver.getAccount().getRole())
                .fullName(driver.getFullName())
                .phoneNumber(driver.getAccount().getPhoneNumber())
                .build());
    }
}
