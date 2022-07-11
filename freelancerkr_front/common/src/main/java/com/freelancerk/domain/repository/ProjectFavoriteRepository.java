package com.freelancerk.domain.repository;

import com.freelancerk.domain.ProjectFavorite;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProjectFavoriteRepository extends JpaRepository<ProjectFavorite, Long>, JpaSpecificationExecutor<ProjectFavorite> {
	ProjectFavorite findByProjectIdAndUserId(Long projectId, Long userId);
    Page<ProjectFavorite> findByUserId(Long userId, Pageable pageable);
    List<ProjectFavorite> findByUserId(Long userId);
    int countByUserId(Long userId);
    
    Page<ProjectFavorite> findByUserIdAndProjectTitleContaining(Long userId, String searchWord, Pageable pageable);
    int countByUserIdAndProjectTitleContaining(Long userId, String searchWord);
    
    Page<ProjectFavorite> findByUserIdAndProjectTitleContainingAndCreatedAtAfterAndCreatedAtBefore(Long userId, String searchWord, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    int countByUserIdAndProjectTitleContainingAndCreatedAtAfterAndCreatedAtBefore(Long userId, String searchWord, LocalDateTime startDate, LocalDateTime endDate);

    ProjectFavorite findTop1ByUserIdOrderByCreatedAtDesc(Long userId);
}
