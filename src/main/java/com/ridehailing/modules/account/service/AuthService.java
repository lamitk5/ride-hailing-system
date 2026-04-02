package com.ridehailing.modules.account.service;

import com.ridehailing.config.security.JwtTokenProvider;
import com.ridehailing.modules.account.dto.LoginRequestDTO;
import com.ridehailing.modules.account.dto.LoginResponseDTO;
import com.ridehailing.modules.account.entity.Account;
import com.ridehailing.modules.account.repository.AccountRepository;
import com.ridehailing.modules.driver.repository.DriverRepository;
import com.ridehailing.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;

    public LoginResponseDTO login(LoginRequestDTO request) {
        Account account = accountRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại."));

        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new RuntimeException("Sai mật khẩu.");
        }

        if (!account.getIsActive()) {
            throw new RuntimeException("Tài khoản đã bị khóa.");
        }

        String token = tokenProvider.generateToken(account.getPhoneNumber(), account.getRole());

        String userId = "";
        String fullName = "";
        
        if ("ROLE_DRIVER".equals(account.getRole())) {
            var driver = driverRepository.findByAccount(account).orElse(null);
            if (driver != null) {
                userId = driver.getId().toString();
                fullName = driver.getFullName();
            }
        } else {
            var user = userRepository.findByAccount(account).orElse(null);
            if (user != null) {
                userId = user.getId().toString();
                fullName = user.getFullName();
            }
        }

        return LoginResponseDTO.builder()
                .token(token)
                .role(account.getRole())
                .userId(userId)
                .fullName(fullName)
                .phoneNumber(account.getPhoneNumber())
                .build();
    }
}
