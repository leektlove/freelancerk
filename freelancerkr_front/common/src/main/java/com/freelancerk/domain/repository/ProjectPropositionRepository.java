package com.freelancerk.domain.repository;

import com.freelancerk.domain.ProjectProposition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;

public interface ProjectPropositionRepository extends JpaRepository<ProjectProposition, Long>, JpaSpecificationExecutor<ProjectProposition> {
	ProjectProposition findByProjectId(Long projectId);
	
    Page<ProjectProposition> findByFreelancerId(Long freelancerId, Pageable pageable);
    int countByFreelancerId(Long freelancerId);

    Page<ProjectProposition> findByProjectTitleContainingAndFreelancerId(String keyword, Long freelancerId, Pageable pageable);

    Page<ProjectProposition> findByProjectTitleContainingAndFreelancerIdAndCreatedAtGreaterThanAndCreatedAtLessThan(String keyword, Long freelancerId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    int countByFreelancerIdAndProjectId(Long userId, Long projectId);

    void deleteByProjectId(Long projectId);

    ProjectProposition findByProjectIdAndFreelancerId(Long projectId, Long freelancerUserId);

    long countByProjectPostingClientId(Long userId);

    ProjectProposition findTop1ByProjectPostingClientIdOrderByCreatedAtDesc(Long userId);

    ProjectProposition findTop1ByFreelancerIdOrderByCreatedAtDesc(Long userId);

    int countByProjectId(Long projectId);
}
