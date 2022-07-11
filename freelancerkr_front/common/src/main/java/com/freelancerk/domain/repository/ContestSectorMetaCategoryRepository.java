package com.freelancerk.domain.repository;

import com.freelancerk.domain.ContestSectorMetaCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestSectorMetaCategoryRepository extends JpaRepository<ContestSectorMetaCategory, Long> {
    List<ContestSectorMetaCategory> findByContestSectorMetaTypeId(Long id);
}
