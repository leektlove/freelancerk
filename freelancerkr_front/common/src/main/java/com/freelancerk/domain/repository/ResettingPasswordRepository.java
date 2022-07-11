package com.freelancerk.domain.repository;

import com.freelancerk.domain.ResettingPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ResettingPasswordRepository extends JpaRepository<ResettingPassword, Long> {
    ResettingPassword findByTokenAndInvalidFalseAndUsedFalseAndExpiredAtAfter(String token, LocalDateTime criteria);
}
