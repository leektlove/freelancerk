package com.freelancerk.service;

import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.Purchase;
import com.freelancerk.domain.User;

import java.time.LocalDateTime;

public interface PointService {
	void getRidOfPointsFromClient(Long userId, Integer points, String reason, Purchase purchase, boolean minusAvailable);

    void getRidOfPointsFromFreelancerForPickMeUp(Long userId, Integer points, Purchase purchase);

    void getRidOfPointsFromFreelancerForContestEntry(Long userId, Integer points, Purchase purchase);

    void givePointsToClient(Long userId, Integer points, String reason, Purchase purchase);

    void givePointsToClientExpiredAt(Long userId, Integer points, String reason, LocalDateTime expiredAt);

    void givePointsToFreelancerForContestRefund(Long userId, int points, String reason, Project project);

    void givePointsToFreelancerForPickMeUp(User user, int usedPoints, int addedPoint, String reason, PickMeUp pickMeUp, Purchase purchase);

    void givePointsToFreelancerForContestEntry(User user, int point, String reason, Purchase purchase);

    void givePointsToFreelancerForEtcExpiredAt(User user, int point, String reason, LocalDateTime expiredAt);

    void givePointsToFreelancerForEtc(User user, int point, String reason);
}
