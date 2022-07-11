package com.freelancerk.domain.repository;

import com.freelancerk.domain.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    List<EmailLog> findByIdGreaterThan(long l);
}
