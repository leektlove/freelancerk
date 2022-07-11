package com.freelancerk.scheduler;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectComplete;
import com.freelancerk.domain.ProjectRate;
import com.freelancerk.domain.repository.ProjectCompleteRepository;
import com.freelancerk.domain.repository.ProjectRateRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ProjectCompleteScheduler {

    private MessageService messageService;
    private ProjectRepository projectRepository;
    private ProjectRateRepository projectRateRepository;
    private ProjectCompleteRepository projectCompleteRepository;

    @Autowired
    public ProjectCompleteScheduler(MessageService messageService, ProjectRepository projectRepository,
                                    ProjectRateRepository projectRateRepository, ProjectCompleteRepository projectCompleteRepository) {
        this.messageService = messageService;
        this.projectRepository = projectRepository;
        this.projectRateRepository = projectRateRepository;
        this.projectCompleteRepository = projectCompleteRepository;
    }

    @Transactional
    @Scheduled(cron="0 0/15 * * * ?")
    public void tryCompleteForClientNotRating() {

        List<ProjectComplete> projectCompletesCandidate = projectCompleteRepository.findByFreelancerRequestTrueAndClientAcceptFalseAndFreelancerRequestAtGreaterThan(LocalDateTime.now().plusDays(12));
        for (ProjectComplete projectComplete: projectCompletesCandidate) {
            if (projectComplete.isSendEndInTwoDaysMail()) continue;

            Map<String, Object> messageVariablesFor = new HashMap<>();
            messageService.sendMessage(projectComplete.getProject().getContractedFreelancer(), AligoKakaoMessageTemplate.Code.TA_3195, messageVariablesFor);

            projectComplete.setSendEndInTwoDaysMail(true);
            projectCompleteRepository.save(projectComplete);
        }

        List<ProjectComplete> projectCompletes = projectCompleteRepository.findByFreelancerRequestTrueAndClientAcceptFalseAndFreelancerRequestAtLessThan(LocalDateTime.now().minusWeeks(2));
        for (ProjectComplete item: projectCompletes) {
            try {
                item.setClientAcceptAt(LocalDateTime.now());
                item.setClientAccept(true);
                item.setMemo("프리랜서 평가 후 2주 안에 클라이언트 미평가로 자동 프로젝트 완료 처리");
                projectCompleteRepository.save(item);

                ProjectRate projectRate = projectRateRepository.findByProjectIdAndRaterType(item.getProject().getId(), ProjectRate.RaterType.CLIENT);
                if (projectRate == null) {
                    projectRate = new ProjectRate();
                    projectRate.setProject(item.getProject());
                    projectRate.setRaterType(ProjectRate.RaterType.CLIENT);
                    projectRate.setRaterUser(item.getProject().getPostingClient());
                    projectRate.setRatedUser(item.getProject().getContractedFreelancer());
                    projectRate.setType1Rate(4);
                    projectRate.setType2Rate(4);
                    projectRate.setType3Rate(4);
                    projectRate.setType4Rate(4);
                    projectRate.setType5Rate(4);
                    projectRateRepository.save(projectRate);
                }

                Project project = item.getProject();
                if (!Project.Status.COMPLETED.equals(project.getStatus())) {
                    project.setCompletedAt(LocalDateTime.now());
                    project.setStatus(Project.Status.COMPLETED);
                    projectRepository.save(project);
                }

                log.info("<<< {}번 프로젝트 완료 처리 됨", item.getId());
            } catch (Exception e) {
                log.error("<<< {}번 완료 처리 중 에러 발생", item.getId(), e);
            }
        }
    }
}
