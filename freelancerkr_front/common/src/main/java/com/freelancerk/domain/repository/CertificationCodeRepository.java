package com.freelancerk.domain.repository;

import com.freelancerk.domain.CertificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CertificationCodeRepository extends JpaRepository<CertificationCode, Long> {
    CertificationCode findTop1ByCellphoneAndCodeAndExpiredAtAfter(String cellphone, String code, LocalDateTime dateTime);
}
