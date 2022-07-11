package com.freelancerk.domain.repository;

import com.freelancerk.domain.ClientPointLog;
import com.freelancerk.domain.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientPointLogRepository extends JpaRepository<ClientPointLog, Long> {
    Page<ClientPointLog> findByUserId(Long userId, Pageable pageable);

    List<ClientPointLog> findByUserIdAndAddedPointExpiredAtGreaterThan(Long userId, LocalDateTime criteria);

    ClientPointLog findTop1ByUserIdAndAddedPointExpiredAtGreaterThanAndRemainPointGreaterThanOrderByIdAsc(Long userId, LocalDateTime criteria, long i);

    List<ClientPointLog> findByUserIdOrderByCreatedAtDesc(Long sessionUserId);

    ClientPointLog findTop1ByPurchaseId(Long purchaseId);
}
