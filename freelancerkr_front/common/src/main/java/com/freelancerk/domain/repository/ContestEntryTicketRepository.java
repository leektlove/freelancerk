package com.freelancerk.domain.repository;

import com.freelancerk.domain.ContestEntryTicket;
import com.freelancerk.domain.ProjectBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface ContestEntryTicketRepository extends JpaRepository<ContestEntryTicket, Long>, JpaSpecificationExecutor<ContestEntryTicket> {
    ContestEntryTicket findTop1ByFreelancerProductItemTypeIdAndProjectBidId(long freelancerProductItemTypeId, Long bidId);

    List<ContestEntryTicket> findByProjectBidIdAndEndAtAfterOrderByEndAtAsc(Long projectBidId, LocalDateTime criteria);

    List<ContestEntryTicket> findByProjectBidId(Long bidId);
}
