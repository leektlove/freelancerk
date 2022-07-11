package com.freelancerk.domain.repository;

import com.freelancerk.domain.ProjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProjectCategoryRepository extends JpaRepository<ProjectCategory, Long> {
    List<ProjectCategory> findByProjectId(Long projecId);

    @Transactional
    @Modifying
    void deleteByProjectId(Long id);
}
