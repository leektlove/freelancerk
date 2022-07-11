package com.freelancerk.domain.repository;

import com.freelancerk.domain.EscrowRefundRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EscrowRefundRequestRepository extends JpaRepository<EscrowRefundRequest, Long>, JpaSpecificationExecutor<EscrowRefundRequest> {
    Page<EscrowRefundRequest> findByUserId(Long userId, Pageable pageable);

    List<EscrowRefundRequest> findByUserId(Long userId);

    long countByUserId(Long userId);

    List<EscrowRefundRequest> findByUserIdAndType(Long userId, EscrowRefundRequest.Type type);
}
