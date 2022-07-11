package com.freelancerk.legacy.repository;

import com.freelancerk.legacy.domain.FrProjectTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FrProjectTagRepository extends JpaRepository<FrProjectTag, Long> {
    List<FrProjectTag> findByFpNo(int fpNo);
}
