package com.freelancerk.domain.repository;

import com.freelancerk.domain.FreelancerProductItemType;
import com.freelancerk.domain.PickMeUpTicketLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PickMeUpTicketLogRepository extends JpaRepository<PickMeUpTicketLog, Long> {

    List<PickMeUpTicketLog> findByPickMeUpIdAndInvalidFalse(Long pickMeUpId);

    List<PickMeUpTicketLog> findByPickMeUpIdAndFreelancerProductItemTypeCodeAndInvalidFalseOrderByCreatedAtAsc(Long pickMeUpId, FreelancerProductItemType.Code ittemCode);

    List<PickMeUpTicketLog> findByPurchaseId(Long purchaseId);

    List<PickMeUpTicketLog> findByPurchaseIsNull();

    List<PickMeUpTicketLog> findByPickMeUpUserId(Long userId);
}
