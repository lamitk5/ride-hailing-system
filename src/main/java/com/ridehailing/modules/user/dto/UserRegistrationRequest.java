package com.ridehailing.modules.user.dto;
import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String phoneNumber;
    private String password;
    private String fullName;
    private String email;
}
