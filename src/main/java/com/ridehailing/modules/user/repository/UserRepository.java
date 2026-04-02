package com.ridehailing.modules.user.repository;
import com.ridehailing.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    java.util.Optional<User> findByAccount(com.ridehailing.modules.account.entity.Account account);
}
