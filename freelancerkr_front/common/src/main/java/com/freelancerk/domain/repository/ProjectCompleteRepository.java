package com.freelancerk.domain.repository;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectComplete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectCompleteRepository extends JpaRepository<ProjectComplete, Long> {
    ProjectComplete findByProjectId(Long projectId);

    long countByProjectContractedFreelancerIdAndProjectStatus(Long sessionUserId, Project.Status status);

    List<ProjectComplete> findByFreelancerRequestTrueAndClientAcceptFalseAndFreelancerRequestAtLessThan(LocalDateTime dateTime);

    List<ProjectComplete> findByFreelancerRequestTrueAndClientAcceptFalseAndFreelancerRequestAtGreaterThan(LocalDateTime dateTime);
}
