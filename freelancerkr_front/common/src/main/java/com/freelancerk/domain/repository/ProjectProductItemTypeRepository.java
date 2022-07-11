package com.freelancerk.domain.repository;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectProductItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectProductItemTypeRepository extends JpaRepository<ProjectProductItemType, Long> {
    List<ProjectProductItemType> findByIdIn(Long[] optionIds);

    ProjectProductItemType findByCodeAndProjectType(ProjectProductItemType.Code code, Project.Type projectType);

    List<ProjectProductItemType> findByPackFalseAndValidTrueAndProjectType(Project.Type type);

    List<ProjectProductItemType> findByPriorityTrueAndCategory(ProjectProductItemType.Category category);

    List<ProjectProductItemType> findByUrgentTrueAndCategory(ProjectProductItemType.Category category);

    List<ProjectProductItemType> findByPriorityTrue();

    List<ProjectProductItemType> findByCategoryAndValidTrue(ProjectProductItemType.Category category);

    List<ProjectProductItemType> findByCode(ProjectProductItemType.Code external);

    List<ProjectProductItemType> findByUrgentTrue();
}
