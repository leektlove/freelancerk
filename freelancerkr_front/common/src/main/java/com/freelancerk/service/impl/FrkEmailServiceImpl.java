package com.freelancerk.service.impl;

import com.freelancerk.domain.EmailLog;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.EmailLogRepository;
import com.freelancerk.gateway.EmailService;
import com.freelancerk.service.FrkEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Locale;

@Service
public class FrkEmailServiceImpl implements FrkEmailService {

    private EmailService emailService;
    private TemplateEngine templateEngine;
    private EmailLogRepository emailLogRepository;

    @Autowired
    public FrkEmailServiceImpl(EmailService emailService, TemplateEngine templateEngine, EmailLogRepository emailLogRepository) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
        this.emailLogRepository = emailLogRepository;
    }

    @Override
    public void sendEmailResetLink(String username, String linkUrl, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("userName", username);
        ctx.setVariable("linkUrl", linkUrl);

        String content = this.templateEngine.process("email/email-for-resetting-password", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 비밀번호 재설정", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 비밀번호 재설정");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendProjectBidAlarm(User freelancer, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("freelancer", freelancer);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/project-apply", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 프로젝트 지원자 알림", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 프로젝트 지원자 알림");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendContestBidAlarm(User freelancer, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("freelancer", freelancer);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/contest-apply", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 컨테스트 지원자 알림", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 컨테스트 지원자 알림");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendProjectBidSuccessToClient(User freelancer, User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("freelancer", freelancer);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/project-started", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 프로젝트 낙찰 완료", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 프로젝트 낙찰 완료");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendContestBidSuccessToClient(String freelancerExposeNames, User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("freelancerExposeNames", freelancerExposeNames);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/contest-started", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 컨테스트 선정 완료", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 컨테스트 선정 완료");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendProjectBidSuccessToFreelancer(User freelancer, User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("freelancer", freelancer);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/project-bid-success-to-freelancer", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 프로젝트 낙찰 성공", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 프로젝트 낙찰 성공");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendContestBidSuccessToFreelancer(User freelancer, String freelancerExposeNames, User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("freelancer", freelancer);
        ctx.setVariable("freelancerExposeNames", freelancerExposeNames);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/contest-bid-success-to-freelancer", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 컨테스트 당선", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 컨테스트 당선");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendContestCancelByClientAlarmToClient(User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/contest-cancel-client", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 컨테스트 포스트 취소 및 환불 안내", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 컨테스트 포스트 취소 및 환불 안내");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendContestCancelByClientAlarmToFreelancer(User freelancer, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("freelancer", freelancer);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/contest-cancel-freelancer", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 컨테스트 포스트 취소 및 배당 안내", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 컨테스트 포스트 취소 및 배당 안내");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendProjectCancelByClientAlarmToClient(User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/project-cancelled", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 프로젝트 포스트 취소", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 프로젝트 포스트 취소");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendProjectCancelBySchedulerForNoBidAlarmToClient(User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/project-cancelled-no-bid-or-picked", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 기한만료로 인한 프로젝트 마감", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 기한만료로 인한 프로젝트 마감");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendProjectCancelBySchedulerForNoPickAlarmToClient(User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/project-cancelled-no-bid-or-picked", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 기한만료로 인한 프로젝트 마감", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 기한만료로 인한 프로젝트 마감");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendProjectNeedPickBySchedulerForExistsBidAlarmToClient(User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/project-cancelled-no-bid-or-picked", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 기한만료로 인한 프로젝트 마감", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 기한만료로 인한 프로젝트 마감");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendProjectNeedPickCancelInTwoDaysBySchedulerForExistsBidAlarmToClient(User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/project-posted-end-in-two-days", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 프로젝트 포스트 마감 예정 안내", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 프로젝트 포스트 마감 예정 안내");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendContestCancelBySchedulerForNoBidAlarmToClient(User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/contest-cancelled-no-bid-for-posting-end", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 컨테스트 마감 및 환불 안내", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 컨테스트 마감 및 환불 안내");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendContestPostingEndBySchedulerForNeedPickToClient(User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/contest-posting-end-for-need-to-pick", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 컨테스트 포스트 마감 예정 안내", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 컨테스트 포스트 마감 예정 안내");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendContestNeedPickCancelInTwoDaysBySchedulerForExistsBidAlarmToClient(User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/contest-posted-end-in-two-days", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 2일 후 컨테스트 포스트 자동 마감 안내", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 2일 후 컨테스트 포스트 자동 마감 안내");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendContestCancelBySchedulerForNoPickToClient(User client, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("client", client);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/contest-posting-end-and-exists-bid", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 컨테스트 포스트 마감 및 환불 안내", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 컨테스트 포스트 마감 및 환불 안내");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendContestCancelBySchedulerForNoPickToFreelancer(User freelancer, Project project, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("freelancer", freelancer);
        ctx.setVariable("project", project);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/contest-cancel-freelancer", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 컨테스트 포스트 마감 및 배당 안내", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 컨테스트 포스트 마감 및 배당 안내");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendPaymentRequestAlarmToClient(Project project, User freelancer, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("freelancer", freelancer);
        ctx.setVariable("userEmail", receiveEmail);
        ctx.setVariable("project", project);

        String content = this.templateEngine.process("email/payment-request", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 프리랜서의 지급청구 도착", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 클라이언트 승인 완료");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendPaymentAcceptedAlarmToFreelancer(Project project, User freelancer, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("freelancer", freelancer);
        ctx.setVariable("userEmail", receiveEmail);
        ctx.setVariable("project", project);

        String content = this.templateEngine.process("email/payment-accepted", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 클라이언트 승인 완료", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 프리랜서의 지급청구 도착");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendRatedAlarmToClient(String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/client-rated", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 프리랜서의 클라이언트 평가 완료", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 프리랜서의 클라이언트 평가 완료");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendPickMeUpExposeEndToFreelancer(String startAt, User freelancer, PickMeUp pickMeUp, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("userEmail", receiveEmail);
        ctx.setVariable("startAt", startAt);
        ctx.setVariable("freelancer", freelancer);
        ctx.setVariable("pickMeUp", pickMeUp);

        String content = this.templateEngine.process("email/pick-me-up-expose-end", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 포트폴리오 공개 종료", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 포트폴리오 공개 종료");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendPickMeUpExposeEndInTwoDaysToFreelancer(User freelancer, String endAt, PickMeUp pickMeUp, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("userEmail", receiveEmail);
        ctx.setVariable("freelancer", freelancer);
        ctx.setVariable("endAt", endAt);
        ctx.setVariable("pickMeUp", pickMeUp);
        ctx.setVariable("userEmail", receiveEmail);

        String content = this.templateEngine.process("email/pick-me-up-expose-end-in-two-days", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 2일 후 포트폴리오 공개 종료 예정", content);

        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 2일 후 포트폴리오 공개 종료 예정");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);
    }

    @Override
    public void sendPickMeUpOptionEndInTwoDaysToFreelancer(User freelancer, String endAt, PickMeUp pickMeUp, String optionNames, String receiveEmail) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("userEmail", receiveEmail);
        ctx.setVariable("freelancer", freelancer);
        ctx.setVariable("endAt", endAt);
        ctx.setVariable("pickMeUp", pickMeUp);
        ctx.setVariable("optionNames", optionNames);

        String content = this.templateEngine.process("email/pick-me-up-option-end-in-two-days", ctx);
        emailService.sendEmail(receiveEmail, "[프리랜서코리아] 2일 후 픽미업 옵션 종료 예정", content);
        
        EmailLog emailLog = new EmailLog();
        emailLog.setContent(content);
        emailLog.setTitle("[프리랜서코리아] 2일 후 픽미업 옵션 종료 예정");
        emailLog.setReceiveEmails(receiveEmail);
        emailLogRepository.save(emailLog);

    }
}
