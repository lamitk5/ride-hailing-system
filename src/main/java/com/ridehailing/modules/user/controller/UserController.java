package com.ridehailing.modules.user.controller;

import com.ridehailing.config.security.JwtTokenProvider;
import com.ridehailing.modules.account.dto.LoginResponseDTO;
import com.ridehailing.modules.user.dto.UserRegistrationRequest;
import com.ridehailing.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> registerUser(@RequestBody UserRegistrationRequest request) {
        com.ridehailing.modules.user.entity.User user = userService.registerUser(request);
        String token = tokenProvider.generateToken(user.getAccount().getPhoneNumber(), user.getAccount().getRole());
        return ResponseEntity.ok(LoginResponseDTO.builder()
                .token(token)
                .userId(user.getId().toString())
                .role(user.getAccount().getRole())
                .fullName(user.getFullName())
                .phoneNumber(user.getAccount().getPhoneNumber())
                .build());
    }
}
