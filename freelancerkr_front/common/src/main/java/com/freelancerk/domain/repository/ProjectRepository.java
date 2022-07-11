package com.freelancerk.domain.repository;

import com.freelancerk.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

	Page<Project> findByPostingClientIdAndStatus(Long postingClientId, Project.Status status, Pageable pageable);

	Page<Project> findByContractedFreelancerIdAndStatus(Long contractedFreelancerId, Project.Status status,	Pageable pageable);
	int countByContractedFreelancerIdAndStatus(Long contractedFreelancerId, Project.Status status);
	List<Project> findByTitle(String title);	
	
	/**
	 * 프리랜서 완료된 프로젝트
	 */
	Page<Project> findByContractedFreelancerIdAndStatusAndEndAtAfterAndEndAtBefore(Long contractedFreelancerId, Project.Status status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	int countByContractedFreelancerIdAndStatusAndEndAtAfterAndEndAtBefore(Long contractedFreelancerId, Project.Status status, LocalDateTime startDate, LocalDateTime endDate);

	Page<Project> findByPostingClientIdAndStartAtLessThanAndStatus(Long sessionUserId, LocalDateTime now, Project.Status status, Pageable pageable);

	Page<Project> findByPostingClientIdAndEndAtLessThanAndStatus(Long sessionUserId, LocalDateTime now, Project.Status status, Pageable pageable);

    List<Project> findByStatusAndProjectType(Project.Status inProgress, Project.Type type);

    Page<Project> findByProjectType(Project.Type type, Pageable pageable);

    Page<Project> findByProjectTypeIn(List<Project.Type> types, Pageable pageable);

    Page<Project> findByProjectTypeAndStatusIn(Project.Type type, List<Project.Status> statuses, Pageable pageable);

    List<Project> findByStatusAndPostingEndAtBefore(Project.Status status, LocalDateTime now);

    long countByPostingClientIdAndProjectTypeIn(Long userId, List<Project.Type> types);

    Project findTop1ByProjectTypeInAndStatusAndPostingClientIdOrderByCreatedAt(List<Project.Type> types, Project.Status status, Long userId);

    int countByPostingClientIdAndStatusIn(Long clientId, List<Project.Status> statuses);

    List<Project> findByStatusAndStartAtBefore(Project.Status status, LocalDateTime criteria);

	Project findByUuid(String uuid);

    List<Project> findByStatusAndContractedFreelancerIsNull(Project.Status status);

	List<Project> findByStatusAndStartAtBeforeAndProjectType(Project.Status status, LocalDateTime dateTime, Project.Type type);


	@Modifying
	@Transactional
	@Query(value = "update Project p set p.invalid = true where p.postingClient.id = :userId")
	void invalidateByUserId(@Param("userId") Long userId);

    int countByStatusNotIn(List<Project.Status> statuses);

    Project findTop1ByPostingClientIdAndStatusAndInvalidFalseOrderByPostingStartAtDesc(Long userId, Project.Status status);

    Project findTop1ByPostingClientIdAndStatusAndInvalidFalseOrderByStartAtDesc(Long userId, Project.Status status);
}
