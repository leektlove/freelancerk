package com.freelancerk.domain.repository;

import com.freelancerk.domain.ProjectPointLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectPointLogRepository extends JpaRepository<ProjectPointLog, Long> {
    List<ProjectPointLog> findByUserIdAndAddedPointGreaterThanAndUsePointGreaterThanOrderByCreatedAtDesc(Long userId, int zeroPoint, int zeroPoint2);

    List<ProjectPointLog> findByUserIdAndAddedPointExpiredAtGreaterThan(Long userId, LocalDateTime criteria);

    List<ProjectPointLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    int countByProjectId(Long id);
}
