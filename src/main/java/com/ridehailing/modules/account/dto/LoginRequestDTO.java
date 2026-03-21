package com.ridehailing.modules.account.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String phoneNumber;
    private String password;
}
