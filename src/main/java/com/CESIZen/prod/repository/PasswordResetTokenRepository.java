package com.CESIZen.prod.repository;

import com.CESIZen.prod.entity.PasswordResetToken;
import com.CESIZen.prod.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUserId(Long userId);

    void deleteAllByUser(User user);
}
