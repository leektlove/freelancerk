package com.freelancerk.service;

import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.User;

import java.util.List;

public interface FrkEmailService {

    void sendEmailResetLink(String username, String linkUrl, String receiveEmail);

    void sendProjectBidAlarm(User freelancer, String receiveEmail);

    void sendContestBidAlarm(User freelancer, String receiveEmail);

    void sendProjectBidSuccessToClient(User freelancer, User client, Project project, String receiveEmail);

    void sendContestBidSuccessToClient(String freelancerExposeNames, User client, Project project, String receiveEmail);

    void sendProjectBidSuccessToFreelancer(User freelancer, User client, Project project, String receiveEmail);

    void sendContestBidSuccessToFreelancer(User freelancer, String freelancerExposeNames, User client, Project project, String receiveEmail);

    void sendContestCancelByClientAlarmToClient(User client, Project project, String receiveEmail);

    void sendContestCancelByClientAlarmToFreelancer(User freelancer, Project project, String receiveEmail);

    void sendProjectCancelByClientAlarmToClient(User client, Project project, String receiveEmail);

    void sendProjectCancelBySchedulerForNoBidAlarmToClient(User client, Project project, String receiveEmail);

    void sendProjectCancelBySchedulerForNoPickAlarmToClient(User client, Project project, String receiveEmail);

    void sendProjectNeedPickBySchedulerForExistsBidAlarmToClient(User client, Project project, String receiveEmail);

    void sendProjectNeedPickCancelInTwoDaysBySchedulerForExistsBidAlarmToClient(User client, Project project, String receiveEmail);

    void sendContestCancelBySchedulerForNoBidAlarmToClient(User client, Project project, String receiveEmail);

    void sendContestPostingEndBySchedulerForNeedPickToClient(User client, Project project, String receiveEmail);

    void sendContestNeedPickCancelInTwoDaysBySchedulerForExistsBidAlarmToClient(User client, Project project, String receiveEmail);

    void sendContestCancelBySchedulerForNoPickToClient(User client, Project project, String receiveEmail);

    void sendContestCancelBySchedulerForNoPickToFreelancer(User client, Project project, String receiveEmail);

    void sendPaymentRequestAlarmToClient(Project project, User freelancer, String receiveEmail);

    void sendPaymentAcceptedAlarmToFreelancer(Project project, User freelancer, String receiveEmail);

    void sendRatedAlarmToClient(String receiveEmail);

    void sendPickMeUpExposeEndToFreelancer(String startAt, User freelancer, PickMeUp pickMeUp, String receiveEmail);

    void sendPickMeUpExposeEndInTwoDaysToFreelancer(User freelancer, String endAt, PickMeUp pickMeUp, String receiveEmail);

    void sendPickMeUpOptionEndInTwoDaysToFreelancer(User freelancer, String endAt, PickMeUp pickMeUp, String optionNames, String receiveEmail);
}
