package com.freelancerk.domain.repository;

import com.freelancerk.domain.ContestSector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestSectorRepository extends JpaRepository<ContestSector, Long> {
    List<ContestSector> findByContestId(Long id);

    int countByContestIdAndContestSectorTypeId(Long projectId, Long projectSectorTypeId);
}
