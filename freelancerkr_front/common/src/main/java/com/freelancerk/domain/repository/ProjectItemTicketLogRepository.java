package com.freelancerk.domain.repository;

import com.freelancerk.domain.ProjectItemTicketLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface ProjectItemTicketLogRepository extends JpaRepository<ProjectItemTicketLog, Long> {
    List<ProjectItemTicketLog> findByPurchaseId(Long purchaseId);

    int countByProjectId(Long projectId);

    List<ProjectItemTicketLog> findByProjectId(Long projectId);

    List<ProjectItemTicketLog> findByProjectPostingClientId(Long id);
}
