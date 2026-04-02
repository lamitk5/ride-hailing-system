package com.ridehailing.modules.driver.repository;

import com.ridehailing.modules.driver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    boolean existsByIdentityCard(String identityCard);
    java.util.Optional<Driver> findByAccount(com.ridehailing.modules.account.entity.Account account);
}
