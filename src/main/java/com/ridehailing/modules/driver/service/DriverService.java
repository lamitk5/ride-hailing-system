package com.ridehailing.modules.driver.service;

import com.ridehailing.common.exception.BadRequestException;
import com.ridehailing.modules.account.entity.Account;
import com.ridehailing.modules.account.repository.AccountRepository;
import com.ridehailing.modules.driver.dto.DriverRegistrationRequest;
import com.ridehailing.modules.driver.entity.Driver;
import com.ridehailing.modules.driver.repository.DriverRepository;
import com.ridehailing.modules.vehicle.entity.Vehicle;
import com.ridehailing.modules.vehicle.entity.VehicleType;
import com.ridehailing.modules.vehicle.repository.VehicleRepository;
import com.ridehailing.modules.vehicle.repository.VehicleTypeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final EntityManager entityManager;

    @Transactional
    public Driver registerDriver(DriverRegistrationRequest request) {
        if (accountRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new BadRequestException("Số điện thoại tài xế đã được đăng ký.");
        }
        if (driverRepository.existsByIdentityCard(request.getIdentityCard())) {
            throw new BadRequestException("CCCD đã được sử dụng.");
        }
        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new BadRequestException("Biển số xe đã được đăng ký.");
        }

        Account account = Account.builder()
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_DRIVER")
                .isActive(true)
                .build();
        account = accountRepository.save(account);

        Driver driver = Driver.builder()
                .fullName(request.getFullName())
                .identityCard(request.getIdentityCard())
                .status("OFFLINE")
                .account(account)
                .build();
        driver = driverRepository.save(driver);

        VehicleType type = vehicleTypeRepository.findByName(request.getVehicleType())
                .orElseGet(() -> {
                    VehicleType newType = VehicleType.builder()
                            .name(request.getVehicleType())
                            .baseFare(java.math.BigDecimal.valueOf(15000))
                            .pricePerKm(java.math.BigDecimal.valueOf(5000))
                            .isActive(true)
                            .build();
                    return vehicleTypeRepository.save(newType);
                });

        Vehicle vehicle = Vehicle.builder()
                .licensePlate(request.getLicensePlate())
                .brandModel(request.getBrandModel())
                .driver(driver)
                .vehicleType(type)
                .build();
        vehicleRepository.save(vehicle);

        return driver;
    }
}
