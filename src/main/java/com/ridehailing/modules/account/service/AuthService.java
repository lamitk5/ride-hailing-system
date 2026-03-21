package com.ridehailing.modules.account.service;

import com.ridehailing.config.security.JwtTokenProvider;
import com.ridehailing.modules.account.dto.LoginRequestDTO;
import com.ridehailing.modules.account.dto.LoginResponseDTO;
import com.ridehailing.modules.account.entity.Account;
import com.ridehailing.modules.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

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

        return LoginResponseDTO.builder()
                .token(token)
                .role(account.getRole())
                .build();
    }
}
