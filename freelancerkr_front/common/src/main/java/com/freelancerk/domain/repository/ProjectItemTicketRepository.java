package com.freelancerk.domain.repository;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectItemTicket;
import com.freelancerk.domain.ProjectProductItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectItemTicketRepository extends JpaRepository<ProjectItemTicket, Long>, JpaSpecificationExecutor<ProjectItemTicket> {
    List<ProjectItemTicket> findByProjectId(Long projectId);

    List<ProjectItemTicket> findByProjectIdAndInvalidFalseAndExpiredAtAfterOrderByExpiredAtAsc(Long projectId, LocalDateTime localDateTime);

    ProjectItemTicket findTop1ByProjectProductItemTypeIdAndProjectId(long itemTypeId, Long projectId);

    List<ProjectItemTicket> findByProjectIdAndProjectProductItemTypeCategoryAndProjectProductItemTypeProjectType(
            Long projectId, ProjectProductItemType.Category category, Project.Type projectType);

    ProjectItemTicket findByProjectIdAndProjectProductItemTypeCodeAndProjectProductItemTypeProjectType(Long projectId, ProjectProductItemType.Code code, Project.Type projectType);

    int countByProjectId(Long projectId);

    List<ProjectItemTicket> findByProjectProductItemTypeIdNotInAndProjectId(List<Long> optionIds, Long id);
}
