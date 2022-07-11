package com.freelancerk.domain.repository;

import com.freelancerk.domain.EscrowLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EscrowLogRepository extends JpaRepository<EscrowLog, Long>, JpaSpecificationExecutor<EscrowLog> {
    List<EscrowLog> findByDepositUserIdAndType(Long userId, EscrowLog.Type type);

    EscrowLog findTop1ByDepositUserIdOrderByCreatedAtDesc(Long userId);

    List<EscrowLog> findByWithdrawalUserIdAndType(Long userId, EscrowLog.Type type);

    List<EscrowLog> findByProjectIdAndType(Long projectId, EscrowLog.Type type);
}
