package com.freelancerk.domain.repository;

import com.freelancerk.domain.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsLogRepository extends JpaRepository<SmsLog, Long> {
}
