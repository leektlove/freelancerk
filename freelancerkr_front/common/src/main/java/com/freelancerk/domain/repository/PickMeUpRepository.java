package com.freelancerk.domain.repository;

import com.freelancerk.domain.PickMeUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PickMeUpRepository extends JpaRepository<PickMeUp, Long>, JpaSpecificationExecutor<PickMeUp> {
	int countByUserIdAndInvalidFalseAndTempFalse(Long userId);

    List<PickMeUp> findByInvalidFalseOrderByCreatedAtDesc();

    @Modifying
    @Transactional
    @Query(value = "update PickMeUp p set p.invalid = true where p.user.id = :userId")
    void invalidateByUserId(@Param("userId") Long userId);

    PickMeUp findByProjectIdAndTempFalse(Long projectId);

    PickMeUp findByProjectIdAndUserIdAndTempFalse(Long projectId, Long userId);

    List<PickMeUp> findByUserIdIn(List<Long> userIds);
}
