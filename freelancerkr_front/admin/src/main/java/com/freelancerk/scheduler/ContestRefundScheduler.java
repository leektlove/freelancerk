package com.freelancerk.scheduler;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class ContestRefundScheduler {

    private ProjectRepository projectRepository;

    @Autowired
    public ContestRefundScheduler(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 12 * * ?")
    public void giveRewardAfterContestPick() {

    }
}
