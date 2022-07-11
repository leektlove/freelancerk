package com.freelancerk.domain.repository;

import com.freelancerk.domain.ProjectComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long>, JpaSpecificationExecutor<ProjectComment> {
	List<ProjectComment> findByProjectIdOrderByCreatedAtAsc(Long projectId);

	List<ProjectComment> findByProjectIdAndTargetUserIdOrderByCreatedAtAsc(Long projectId, Long freelancerId);

	long countByProjectIdAndUserId(Long projectId, Long writerUserId);

	long countByProjectIdAndUserIdNot(Long projectId, Long writerUserId);

    List<ProjectComment> findByProjectIdAndTargetUserIsNull(Long projectId);

    long countByProjectIdAndUserIdNotAndCreatedAtAfter(Long projectId, Long writerUserId, LocalDateTime criteria);

    long countByProjectIdAndTargetUserIsNull(Long projectId);

	long countByProjectIdAndTargetUserId(Long projectId, Long targetUserId);

	long countByProjectIdAndTargetUserIdAndUserId(Long projectId, Long targetUserId, Long userId);

    long countByProjectIdAndUserIdAndCreatedAtAfter(Long projectId, Long userId, LocalDateTime criteria);
}
