package com.freelancerk.domain.repository;

import com.freelancerk.domain.ContestEntryReward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestEntryRewardRepository extends JpaRepository<ContestEntryReward, Long> {
    int countByProjectBidProjectId(Long projectId);

    ContestEntryReward findByProjectBidId(Long id);
}
