package com.freelancerk.service.impl;

import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.EmailLogRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.gateway.EmailService;
import com.freelancerk.service.ContestSectorService;
import com.freelancerk.service.KeywordOrSectorAlarmService;
import com.freelancerk.service.MessageService;
import com.freelancerk.service.UserSectorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KeywordOrSectorAlarmServiceImpl implements KeywordOrSectorAlarmService {

    private Environment env;
    private EmailService emailService;
    private MessageService messageService;
    private UserRepository userRepository;
    private TemplateEngine templateEngine;
    private ProjectRepository projectRepository;
    private UserSectorService userSectorService;
    private EmailLogRepository emailLogRepository;
    private ContestSectorService contestSectorService;

    @Autowired
    public KeywordOrSectorAlarmServiceImpl(Environment env, EmailService emailService,
                                           MessageService messageService, UserRepository userRepository,
                                           TemplateEngine templateEngine, UserSectorService userSectorService,
                                           ProjectRepository projectRepository,
                                           EmailLogRepository emailLogRepository,
                                           ContestSectorService contestSectorService) {
        this.env = env;
        this.emailService = emailService;
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.templateEngine = templateEngine;
        this.projectRepository = projectRepository;
        this.userSectorService = userSectorService;
        this.emailLogRepository = emailLogRepository;
        this.contestSectorService = contestSectorService;
    }

    @Async
    @Override
    public void sendMail(Project project) {
        if (env.getActiveProfiles().length == 0 || !"real".equalsIgnoreCase(env.getActiveProfiles()[0])) {
            return;
        }

        try {
            Set<User> users = null;
            if (Project.Type.PROJECT.equals(project.getProjectType())) {
                users = new HashSet<>(userRepository.findAllById(userSectorService.getUsersByKeywordMatchingMoreThan2(project.getProjectCategories().stream().map(ProjectCategory::getCategory).collect(Collectors.toSet()))));
                log.info("<<< {} project matching keyword user size: {}", project.getId(), users.size());

                try {
                    project.setKeywordDuplicatedMailTargets(project.getKeywordDuplicatedMailTargets() + users.size());
                    project.setSendKeywordDuplicatedMail(true);
                    projectRepository.save(project);
                } catch (Exception e) {
                    project.setKeywordDuplicatedMailTargets(project.getKeywordDuplicatedMailTargets() + users.size());
                    project.setSendKeywordDuplicatedMail(true);
                    projectRepository.save(project);
                }

                for (User user: users) {
                    try {
                        if (user.isReceiveEmail()) {
                            String content = makeProjectPostedEmailContent(project, user.getEmail());
                            emailService.sendEmail(user.getEmail(), "[프리랜서코리아] 키워드 중복(2개 이상) 프로젝트 포스팅 안내", content);
                        }

//                        EmailLog emailLog = new EmailLog();
//                        emailLog.setContent(content);
//                        emailLog.setTitle("[프리랜서코리아] 키워드 중복(3개 이상) 프로젝트 포스팅 안내");
//                        emailLog.setReceiveEmails(user.getEmail());
//                        emailLogRepository.save(emailLog);
                    } catch (Exception e) {
                        log.error("<<< {} 이메일 발송 실패. ", user.getEmail(), e);
                    }
                }

            } else if (Project.Type.CONTEST.equals(project.getProjectType())) {
                users = new HashSet<>(userSectorService.getUsersByTopKeyword(contestSectorService.convertContestSectorToUserCategory(project.getContestSectors().iterator().next())));

                try {
                    project.setKeywordDuplicatedMailTargets(project.getKeywordDuplicatedMailTargets() + users.size());
                    project.setSendKeywordDuplicatedMail(true);
                    projectRepository.save(project);
                } catch (Exception e) {
                    project.setKeywordDuplicatedMailTargets(project.getKeywordDuplicatedMailTargets() + users.size());
                    project.setSendKeywordDuplicatedMail(true);
                    projectRepository.save(project);
                }

                for (User user: users) {
                    try {
                        if (user.isReceiveEmail()) {
                            String content = makeContestPostedEmailContent(project, user.getEmail());
                            emailService.sendEmail(user.getEmail(), "[프리랜서코리아] 동일 섹터 컨테스트 포스팅 안내", content);
                        }

                        if (StringUtils.isNotEmpty(user.getCellphone()) && user.isCellphoneCertified()) {
                            Map<String, Object> messageVariablesFor = new HashMap<>();
                            messageVariablesFor.put("freelancerName", user.getExposeName());
                            messageVariablesFor.put("projectName", project.getTitle());
                            messageService.sendMessage(user, AligoKakaoMessageTemplate.Code.TA_3176, messageVariablesFor);
                        }

//                        EmailLog emailLog = new EmailLog();
//                        emailLog.setContent(content);
//                        emailLog.setTitle("[프리랜서코리아] 동일 섹터 컨테스트 포스팅 안내");
//                        emailLog.setReceiveEmails(user.getEmail());
//                        emailLogRepository.save(emailLog);

                    } catch (Exception e) {
                        log.error("<<< {} 이메일 발송 실패. ", user.getEmail(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("<<< {} 프로젝트 포스팅 이메일 발송 중 에러 발생 ", project.getId(), e);
        }
    }

    private String makeContestPostedEmailContent(Project project, String email) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("item", project);
        ctx.setVariable("userEmail", email);

        return this.templateEngine.process("email/sector-duplicated-contest-posted", ctx);
    }

    public String makeProjectPostedEmailContent(Project project, String email) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("item", project);
        ctx.setVariable("userEmail", email);

        return this.templateEngine.process("email/keyword-duplicated-project-posted", ctx);
    }
}
