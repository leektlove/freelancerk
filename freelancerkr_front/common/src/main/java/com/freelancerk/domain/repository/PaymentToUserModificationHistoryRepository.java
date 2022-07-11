package com.freelancerk.domain.repository;

import com.freelancerk.domain.PaymentToUserModificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentToUserModificationHistoryRepository extends JpaRepository<PaymentToUserModificationHistory, Long> {
}
