package com.freelancerk.domain.repository;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectBidRepository extends JpaRepository<ProjectBid, Long>, JpaSpecificationExecutor<ProjectBid> {
	Page<ProjectBid> findByParticipantIdAndBidStatus(Long participantId, ProjectBid.BidStatus status, Pageable pageable);
	int countByParticipantIdAndBidStatus(Long participantId, ProjectBid.BidStatus status);
	int countByProjectIdAndInvalidFalse(Long projectId);

	@Query(value = "SELECT ROUND(AVG(amount_of_money), 2) FROM project_bid WHERE project_id = :projectId", nativeQuery = true)
	int avgProjectBidMoney(@Param("projectId") Long projectId);

	@Query(value = "SELECT COALESCE (AVG(u.career_year), 0) FROM project_bid AS p JOIN user AS u ON p.participant_id = u.id WHERE p.project_id = :projectId", nativeQuery = true)
	float avgParticipantsCareerYear(@Param("projectId") Long projectId);

	
	int countByProjectIdAndParticipantId(Long projectId, Long participantId);

	// ContestEntry
	int countByParticipantIdAndApplyAtAfter(Long userId, LocalDateTime dateTime);

	Page<ProjectBid> findByParticipantIdAndProjectStatus(Long userId, Project.Status status, Pageable pageable);
	int countByParticipantIdAndProjectStatus(Long userId, Project.Status status);

	ProjectBid findTop1ByParticipantIdAndProjectIdOrderByCreatedAtDesc(Long participantId, Long contestId);

	ProjectBid findTop1ByParticipantIdAndBidStatusOrderByCreatedAtDesc(Long participantId, ProjectBid.BidStatus bidStatus);

	Page<ProjectBid> findByProjectId(Long contestId, Pageable pageable);

	List<ProjectBid> findByProjectIdAndBidStatusOrderByPickedRank(Long projectId, ProjectBid.BidStatus bidStatus);

    int countByProjectIdAndBidStatus(Long projectId, ProjectBid.BidStatus bidStatus);

    List<ProjectBid> findByProjectIdAndIdNot(long projectId, long projectBidId);

	List<ProjectBid> findByProjectIdAndIdNotIn(Long projectId, Long[] projectBidIds);

	long countByParticipantIdAndProjectProjectTypeIn(Long userId, List<Project.Type> types);

    ProjectBid findTop1ByParticipantIdAndBidStatusAndProjectStatusOrderByApplyAtDesc(Long userId, ProjectBid.BidStatus bidStatus, Project.Status status);

	ProjectBid findTop1ByParticipantIdAndBidStatusOrderByApplyAtDesc(Long userId, ProjectBid.BidStatus bidStatus);

    List<ProjectBid> findByProjectIdAndBidStatusIn(Long projectId, List<ProjectBid.BidStatus> statuses);

	List<ProjectBid> findByProjectIdAndInvalidFalse(Long projectId);

    List<ProjectBid> findByProjectIdOrderByCreatedAtAsc(Long projectId);

	List<ProjectBid> findByProjectIdOrderByAmountOfMoneyDesc(Long projectId);

	List<ProjectBid> findByProjectIdOrderByAmountOfMoneyAsc(Long projectId);

	int countByProjectIdAndParticipantIdAndBidStatusNot(Long projectId, Long userId, ProjectBid.BidStatus bidStatus);

	ProjectBid findTop1ByParticipantIdAndProjectIdAndBidStatusNotOrderByCreatedAtDesc(Long userId, Long projectId, ProjectBid.BidStatus bidStatus);
}