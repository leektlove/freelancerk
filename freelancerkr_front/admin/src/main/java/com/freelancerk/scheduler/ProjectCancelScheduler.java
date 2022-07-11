package com.freelancerk.scheduler;

import com.freelancerk.TimeUtil;
import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.service.FrkEmailService;
import com.freelancerk.service.MessageService;
import com.freelancerk.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProjectCancelScheduler {

    private ProjectBidRepository projectBidRepository;
    private ProjectRepository projectRepository;
    private FrkEmailService frkEmailService;
    private ProjectService projectService;
    private MessageService messageService;

    @Autowired
    public ProjectCancelScheduler(ProjectBidRepository projectBidRepository,
                                  ProjectRepository projectRepository, FrkEmailService frkEmailService,
                                  ProjectService projectService, MessageService messageService) {
        this.projectBidRepository = projectBidRepository;
        this.projectRepository = projectRepository;
        this.frkEmailService = frkEmailService;
        this.projectService = projectService;
        this.messageService = messageService;
    }

    @Scheduled(cron="0 0/15 * * * ?")
    public void tryCancel() {

        List<Project> postedContestList = projectRepository.findByStatusAndPostingEndAtBefore(Project.Status.POSTED, LocalDateTime.now());
        List<Project> candidates = new ArrayList<>();
        for (Project project: postedContestList) {
            if (project.getPostingEndAt().isBefore(LocalDateTime.now().minusDays(7))) {
                candidates.add(project);
            } else if (project.getNumberOfApplyCount() == 0) {
                candidates.add(project);
            } else {
                User client = project.getPostingClient();
                if (Project.Type.PROJECT.equals(project.getProjectType())) {
                    if (StringUtils.isNotEmpty(client.getEmail()) && client.isReceiveEmail() && !project.isSendNeedPickMail()
                            && LocalDateTime.now().plusDays(1).isAfter(project.getPostingEndAt())) {
                        frkEmailService.sendProjectNeedPickBySchedulerForExistsBidAlarmToClient(client, project, client.getEmail());

                        Map<String, Object> messageVariables = new HashMap<>();
                        messageVariables.put("postingStartAt", TimeUtil.convertLocalDateTimeToStr(project.getPostingStartAt()));
                        messageVariables.put("postingEndAt", TimeUtil.convertLocalDateTimeToStr(project.getPostingEndAt().plusDays(7)));
                        messageVariables.put("clientName", project.getPostingClient().getExposeName());
                        messageVariables.put("projectName", project.getTitle());
                        messageVariables.put("numberOfApplicants", project.getNumberOfApplyCount());
                        messageService.sendMessage(project.getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3184, messageVariables);

                        project.setSendNeedPickMail(true);
                        projectRepository.save(project);
                    }
                } else if (Project.Type.CONTEST.equals(project.getProjectType())) {
                    if (StringUtils.isNotEmpty(client.getEmail()) && client.isReceiveEmail() && !project.isSendNeedPickMail()
                            && LocalDateTime.now().plusDays(1).isAfter(project.getPostingEndAt())) {
                        frkEmailService.sendContestPostingEndBySchedulerForNeedPickToClient(client, project, client.getEmail());

                        Map<String, Object> messageVariables = new HashMap<>();
                        messageVariables.put("postingStartAt", TimeUtil.convertLocalDateTimeToStr(project.getPostingStartAt()));
                        messageVariables.put("postingEndAt", TimeUtil.convertLocalDateTimeToStr(project.getPostingEndAt().plusDays(7)));
                        messageVariables.put("clientName", project.getPostingClient().getExposeName());
                        messageVariables.put("projectName", project.getTitle());
                        messageVariables.put("numberOfApplicants", project.getNumberOfApplyCount());
                        messageService.sendMessage(project.getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3893, messageVariables);

                        project.setSendNeedPickMail(true);
                        projectRepository.save(project);
                    }
                }

                if (LocalDateTime.now().plusDays(2).isAfter(project.getPostingEndAt().plusDays(7))) {
                    if (Project.Type.PROJECT.equals(project.getProjectType())) {
                        if (StringUtils.isNotEmpty(client.getEmail()) && client.isReceiveEmail() && !project.isSendEndInTwoDaysMail()) {
                            frkEmailService.sendProjectNeedPickCancelInTwoDaysBySchedulerForExistsBidAlarmToClient(client, project, client.getEmail());
                        }

                        if (StringUtils.isNotEmpty(client.getCellphone()) && !project.isSendEndInTwoDaysMail()) {

                            Map<String, Object> messageVariables = new HashMap<>();
                            messageVariables.put("postingStartAt", TimeUtil.convertLocalDateTimeToStr(project.getPostingStartAt()));
                            messageVariables.put("postingEndAt", project.getPostingEndAt());
                            messageVariables.put("clientName", project.getPostingClient().getExposeName());
                            messageVariables.put("projectName", project.getTitle());
                            messageVariables.put("numberOfApplicants", project.getNumberOfApplyCount());
                            messageService.sendMessage(project.getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3185, messageVariables);
                        }

                        project.setSendEndInTwoDaysMail(true);
                        projectRepository.save(project);
                    } else if (Project.Type.CONTEST.equals(project.getProjectType())) {
                        if (StringUtils.isNotEmpty(client.getEmail()) && client.isReceiveEmail() && !project.isSendEndInTwoDaysMail()) {
                            frkEmailService.sendContestNeedPickCancelInTwoDaysBySchedulerForExistsBidAlarmToClient(client, project, client.getEmail());
                        }

                        if (StringUtils.isNotEmpty(client.getCellphone()) && !project.isSendEndInTwoDaysMail()) {
                            Map<String, Object> messageVariables = new HashMap<>();
                            messageVariables.put("postingStartAt", TimeUtil.convertLocalDateTimeToStr(project.getPostingStartAt()));
                            messageVariables.put("clientName", project.getPostingClient().getExposeName());
                            messageVariables.put("projectName", project.getTitle());
                            messageVariables.put("postingEndAt", project.getPostingEndAt().plusDays(7));
                            messageVariables.put("numberOfApplicants", project.getNumberOfApplyCount());
                            messageService.sendMessage(project.getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3187, messageVariables);
                        }

                        project.setSendEndInTwoDaysMail(true);
                        projectRepository.save(project);
                    }
                }
            }
        }
        for (Project item: candidates) {
            try {
                projectService.cancel(item.getId());
                User client = item.getPostingClient();
                if (StringUtils.isNotEmpty(client.getEmail()) && client.isReceiveEmail()) {
                    if (Project.Type.CONTEST.equals(item.getProjectType())) {
                        if (item.getNumberOfApplyCount() == 0) {
                            frkEmailService.sendContestCancelBySchedulerForNoBidAlarmToClient(client, item, client.getEmail());
                        } else {
                            frkEmailService.sendContestCancelBySchedulerForNoPickToClient(client, item, client.getEmail());

                            Map<String, Object> messageVariables = new HashMap<>();
                            messageVariables.put("postingStartAt", TimeUtil.convertLocalDateTimeToStr(item.getPostingStartAt()));
                            messageVariables.put("clientName", item.getPostingClient().getExposeName());
                            messageVariables.put("projectName", item.getTitle());
                            messageVariables.put("depositMoney", 100000);
                            messageVariables.put("totalPrize", item.getRefundablePrize());
                            messageService.sendMessage(item.getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3188, messageVariables);

                            List<User> freelancerUsers = projectBidRepository.findByProjectIdAndInvalidFalse(item.getId()).stream().map(ProjectBid::getParticipant).filter(User::isReceiveEmail).collect(Collectors.toList());
                            for (User freelancerUser: freelancerUsers) {
                                frkEmailService.sendContestCancelBySchedulerForNoPickToFreelancer(freelancerUser, item, freelancerUser.getEmail());

                                Map<String, Object> messageVariablesFor = new HashMap<>();
                                messageVariablesFor.put("freelancerName", freelancerUser.getExposeName());
                                messageVariablesFor.put("projectName", item.getTitle());
                                messageVariablesFor.put("cancelDividend", item.getExpectedCancelDividend());
                                messageService.sendMessage(freelancerUser, AligoKakaoMessageTemplate.Code.TA_3189, messageVariablesFor);
                            }
                        }
                    } else if (Project.Type.PROJECT.equals(item.getProjectType())) {
                        if (item.getNumberOfApplyCount() == 0) {
                            frkEmailService.sendProjectCancelBySchedulerForNoBidAlarmToClient(client, item, client.getEmail());
                        } else {
                            frkEmailService.sendProjectCancelBySchedulerForNoPickAlarmToClient(client, item, client.getEmail());
                        }
                    }
                }

                log.info("<<< {}번 프로젝트 취소 됨", item.getId());
            } catch (Exception e) {
                log.error("<<< {}번 취소 처리 중 에러 발생", item.getId(), e);
            }
        }

        for (Project item: projectRepository.findByStatusAndStartAtBeforeAndProjectType(Project.Status.IN_PROGRESS, LocalDateTime.now().minusDays(7), Project.Type.CONTEST)) {
            try {
                item.setStatus(Project.Status.COMPLETED);
                projectRepository.save(item);
                Map<String, Object> messageVariablesFor = new HashMap<>();
                messageVariablesFor.put("postingStartAt", TimeUtil.convertLocalDateTimeToStr(item.getPostingStartAt()));
                messageVariablesFor.put("clientName", item.getPostingClient().getName());
                messageVariablesFor.put("projectName", item.getTitle());

                messageService.sendMessage(item.getPostingClient(), AligoKakaoMessageTemplate.Code.TB_1398, messageVariablesFor);
            } catch (Exception e) {
                log.error("<<< error at complete contest {}", item.getId(), e);
            }
        }
    }
}
