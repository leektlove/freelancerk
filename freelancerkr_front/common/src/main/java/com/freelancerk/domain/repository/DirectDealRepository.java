package com.freelancerk.domain.repository;

import com.freelancerk.domain.DirectDeal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DirectDealRepository extends JpaRepository<DirectDeal, Long>, JpaSpecificationExecutor<DirectDeal> {
    Page<DirectDeal> findByUserId(Long userId, Pageable pageable);
	List<DirectDeal> findByPickMeUpId(Long pickMeUpId);

    int countByPickMeUpIdAndUserId(Long pickMeUpId, Long userId);

    long countByUserId(Long id);

    DirectDeal findTop1ByUserIdOrderByCreatedAtDesc(Long userId);

    long countByPickMeUpUserId(Long userId);
}
