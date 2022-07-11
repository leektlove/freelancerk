package com.freelancerk.domain.repository;

import com.freelancerk.domain.FreelancerProductItemType;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.PickMeUpTicket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface PickMeUpTicketRepository extends JpaRepository<PickMeUpTicket, Long>, JpaSpecificationExecutor<PickMeUpTicket> {
    int countByUserIdAndFreelancerProductItemTypeTypeAndStartAtBeforeAndEndAtAfter(Long userId, FreelancerProductItemType.Type type, LocalDateTime dateTime, LocalDateTime dateTime1);

    List<PickMeUpTicket> findByFreelancerProductItemTypeId(Long freelancerProductItemTypeId, Pageable pageable);

    List<PickMeUpTicket> findByPickMeUpId(Long pickMeUpId);

    List<PickMeUpTicket> findByUserIdAndStartAtBeforeAndEndAtAfterAndFreelancerProductItemTypeTypeNotAndFreelancerProductItemTypeUsedInContestEntryTrue(
            Long userId, LocalDateTime dateTime, LocalDateTime dateTime2, FreelancerProductItemType.Type notType);

    List<PickMeUpTicket> findByPickMeUpIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(Long id, LocalDateTime now, LocalDateTime now1);

    PickMeUpTicket findTop1ByPickMeUpIdAndFreelancerProductItemTypeIdAndUserIdOrderByEndAtDesc(Long pickMeUpId, Long productItemTypeId, Long id);

    List<PickMeUpTicket> findByPickMeUpIdAndEndAtAfterOrderByEndAtAsc(Long pickMeUpId, LocalDateTime criteria);

    List<PickMeUpTicket> findByFreelancerProductItemTypeCodeAndEndAtLessThanAndEndAtGreaterThan(FreelancerProductItemType.Code code, LocalDateTime dateTime, LocalDateTime dateTime1);

    List<PickMeUpTicket> findByPickMeUpIdAndInvalidFalse(Long pickMeUpId);
}
