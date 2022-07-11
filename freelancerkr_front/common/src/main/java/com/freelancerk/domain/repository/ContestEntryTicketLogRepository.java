package com.freelancerk.domain.repository;

import com.freelancerk.domain.ContestEntryTicketLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestEntryTicketLogRepository extends JpaRepository<ContestEntryTicketLog, Long> {
    List<ContestEntryTicketLog> findByProjectBidIdAndInvalidFalse(Long projectBidId);

    List<ContestEntryTicketLog> findByPurchaseId(Long purchaseId);

    List<ContestEntryTicketLog> findByProjectBidParticipantId(Long id);
}
