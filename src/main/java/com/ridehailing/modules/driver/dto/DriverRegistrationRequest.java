package com.ridehailing.modules.driver.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DriverRegistrationRequest {
    private String phoneNumber;
    private String password;
    private String fullName;
    private String identityCard;
    
    // Thông tin xe (Vehicle)
    private String licensePlate;
    private String brandModel;
    private String vehicleType;
}
