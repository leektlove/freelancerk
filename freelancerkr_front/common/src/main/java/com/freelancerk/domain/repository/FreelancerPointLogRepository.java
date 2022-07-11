package com.freelancerk.domain.repository;

import com.freelancerk.domain.FreelancerPointLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FreelancerPointLogRepository extends JpaRepository<FreelancerPointLog, Long> {
    List<FreelancerPointLog> findByUserIdAndAddedPointExpiredAtGreaterThan(Long userId, LocalDateTime criteria);

    FreelancerPointLog findTop1ByUserIdAndAddedPointExpiredAtGreaterThanAndRemainPointGreaterThanOrderByIdAsc(Long userId, LocalDateTime now, long i);

    List<FreelancerPointLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
