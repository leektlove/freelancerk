package com.freelancerk.scheduler;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ProjectBidCompleteProcessScheduler {

    private ProjectRepository projectRepository;

//    @Autowired
//    public ProjectBidCompleteProcessScheduler(ProjectRepository projectRepository) {
//        this.projectRepository = projectRepository;
//    }
//
//    @Transactional
//    @Scheduled(cron = "0 0 12 * * ?")
//    public void changeProjectStatusAfterBidComplete() {
//        List<Project>
//    }
}
