package com.freelancerk.scheduler;

import com.freelancerk.domain.CancelRefundPointLog;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.*;
import com.freelancerk.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
//@Component
public class ContestCompleteScheduler {

    private PointService pointService;
    private ProjectRepository projectRepository;
    private ProjectBidRepository projectBidRepository;
    private CancelRefundPointLogRepository cancelRefundPointLogRepository;

    @Autowired
    public ContestCompleteScheduler(PointService pointService, ProjectRepository projectRepository,
                                    ProjectBidRepository projectBidRepository,
                                    CancelRefundPointLogRepository cancelRefundPointLogRepository) {
        this.pointService = pointService;
        this.projectRepository = projectRepository;
        this.projectBidRepository = projectBidRepository;
        this.cancelRefundPointLogRepository = cancelRefundPointLogRepository;
    }

    @Transactional
//    @Scheduled(cron = "0 0 12 * * ?")
    public void giveRewardAfterContestPick() {
//        List<Project> inProgressContestList = projectRepository.findByStatusAndProjectType(Project.Status.IN_PROGRESS, Project.Type.CONTEST);
//
//        for (Project contest: inProgressContestList) {
//            try {
//                if (contest.getStartAt().plusDays(7).isAfter(LocalDateTime.now())) {
//                    contest.setStatus(Project.Status.CANCELLED);
//                    contest.setCancelAt(LocalDateTime.now());
//
//                    List<ProjectBid> pickedBid = projectBidRepository.findByProjectIdAndBidStatusOrderByPickedRank(contest.getId(), ProjectBid.BidStatus.APPLY);
//                    for (ProjectBid projectBid: pickedBid) {
//                        CancelRefundPointLog cancelRefundPointLog = new CancelRefundPointLog();
//                        cancelRefundPointLog.setProjectBid(projectBid);
//                        cancelRefundPointLog.setProject(contest);
//                        cancelRefundPointLog.setUser(projectBid.getParticipant());
//                        cancelRefundPointLog.setUserRole(User.Role.ROLE_FREELANCER);
//                        cancelRefundPointLog.setPoints(contest.getExpectedCancelDividend());
//                        cancelRefundPointLogRepository.save(cancelRefundPointLog);
//
//                        pointService.givePointsToFreelancerForContestRefund(
//                                projectBid.getParticipant().getId(),
//                                contest.getExpectedCancelDividend(),
//                                "컨테스트 자동 취소로 인한 배당금 환불",
//                                contest);
//                    }
//
//                    CancelRefundPointLog cancelRefundPointLogForClient = new CancelRefundPointLog();
//                    cancelRefundPointLogForClient.setPoints(contest.getRefundablePriceWhVat());
//                    cancelRefundPointLogForClient.setUser(contest.getPostingClient());
//                    cancelRefundPointLogForClient.setProject(contest);
//                    cancelRefundPointLogForClient.setUserRole(User.Role.ROLE_CLIENT);
//                    cancelRefundPointLogRepository.save(cancelRefundPointLogForClient);
//
//                    pointService.givePointsToClient(
//                            contest.getPostingClient().getId(),
//                            contest.getRefundablePriceWhVat(),
//                            "컨테스트 자동 취소로 인한 환불",
//                            null);
//                }
//            } catch (Exception e) {
//                log.error("<<< 컨테스트({}) 상태 취소(배당금 지급) 스케쥴러 실행 중 오류.", contest.getId(), e);
//                e.printStackTrace();
//            }
//        }
    }
}
