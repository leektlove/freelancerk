package com.freelancerk.domain.repository;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRateRepository extends JpaRepository<ProjectRate, Long> {
    Integer countByRatedUserIdAndRaterType(Long userId, ProjectRate.RaterType raterType);

    @Query(nativeQuery = true,
            value = "select SUM(type1rate + type2rate + type3rate + type4rate + type5rate) from project_rate where rated_user_id = :userId and rater_type = :raterType")
    Integer getRateSumByUserIdAndRaterType(@Param("userId") Long userId, @Param("raterType") ProjectRate.RaterType raterType);

    List<ProjectRate> findByRatedUserIdAndRaterTypeAndProjectStatus(Long ratedUserId, ProjectRate.RaterType raterType, Project.Status projectStatus);

    ProjectRate findByProjectIdAndRaterType(Long projectId, ProjectRate.RaterType raterType);

    ProjectRate findByProjectIdAndRaterUserIdAndRatedUserId(Long projectId, Long id, Long id1);

    int countByProjectIdAndRaterType(Long projectId, ProjectRate.RaterType raterType);
}
