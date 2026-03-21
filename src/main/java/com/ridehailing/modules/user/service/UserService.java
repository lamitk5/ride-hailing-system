package com.ridehailing.modules.user.service;

import com.ridehailing.common.exception.BadRequestException;
import com.ridehailing.modules.account.entity.Account;
import com.ridehailing.modules.account.repository.AccountRepository;
import com.ridehailing.modules.user.dto.UserRegistrationRequest;
import com.ridehailing.modules.user.entity.User;
import com.ridehailing.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        if (accountRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new BadRequestException("Số điện thoại khách hàng đã được đăng ký.");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng.");
        }

        Account account = Account.builder()
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_CUSTOMER")
                .isActive(true)
                .build();
        account = accountRepository.save(account);

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .account(account)
                .build();
        return userRepository.save(user);
    }
}
