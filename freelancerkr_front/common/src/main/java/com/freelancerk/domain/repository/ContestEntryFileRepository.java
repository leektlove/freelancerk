package com.freelancerk.domain.repository;

import com.freelancerk.domain.ContestEntryFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ContestEntryFileRepository extends JpaRepository<ContestEntryFile, Long> {
	ContestEntryFile findByContestEntryIdAndRepresentative(Long contestEntryId, boolean isRepresentative);
	List<ContestEntryFile> findByContestEntryId(Long contestEntryId);
	
    @Transactional
    void deleteByContestEntryId(Long contestEntryId);

    List<ContestEntryFile> findByContestEntryProjectIdAndContestEntryInvalidFalseAndRepresentativeTrue(Long contestId);

    List<ContestEntryFile> findByContestEntryIdOrderByRepresentativeDesc(long contestEntryId);

    ContestEntryFile findByUserIdAndContestEntryProjectIdAndRepresentativeTrue(Long sessionUserId, long projectId);
}
