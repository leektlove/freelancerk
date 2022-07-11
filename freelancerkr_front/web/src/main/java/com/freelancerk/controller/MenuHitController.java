package com.freelancerk.controller;

import com.freelancerk.controller.io.MenuIndicator;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.io.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class MenuHitController extends RootController {

    private MenuHitRepository menuHitRepository;
    private PurchaseRepository purchaseRepository;
    private EscrowLogRepository escrowLogRepository;
    private DirectDealRepository directDealRepository;
    private ProjectPropositionRepository projectPropositionRepository;
    private ProjectRepository projectRepository;
    private PickMeUpRepository pickMeUpRepository;
    private ProjectFavoriteRepository projectFavoriteRepository;
    private ProjectBidRepository projectBidRepository;
    private NoticeRepository noticeRepository;
    private ReferenceRepository referenceRepository;

    @Autowired
    public MenuHitController(MenuHitRepository menuHitRepository, PurchaseRepository purchaseRepository,
                             EscrowLogRepository escrowLogRepository, DirectDealRepository directDealRepository,
                             ProjectPropositionRepository projectPropositionRepository, ProjectRepository projectRepository,
                             PickMeUpRepository pickMeUpRepository, ProjectFavoriteRepository projectFavoriteRepository,
                             ProjectBidRepository projectBidRepository,
                             NoticeRepository noticeRepository, ReferenceRepository referenceRepository) {
        this.menuHitRepository = menuHitRepository;
        this.purchaseRepository = purchaseRepository;
        this.escrowLogRepository = escrowLogRepository;
        this.directDealRepository = directDealRepository;
        this.projectPropositionRepository = projectPropositionRepository;
        this.projectRepository = projectRepository;
        this.pickMeUpRepository = pickMeUpRepository;
        this.projectFavoriteRepository = projectFavoriteRepository;
        this.projectBidRepository = projectBidRepository;
        this.noticeRepository = noticeRepository;
        this.referenceRepository = referenceRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/menu-hits")
    public CommonResponse<List<MenuIndicator>> getMenuHits() {
        List<MenuIndicator> indicators = new ArrayList<>();
        return new CommonResponse.Builder<List<MenuIndicator>>().data(indicators).build();

//        List<MenuHit> menuHitList = menuHitRepository.findByUserIdOrderByCreatedAt(getSessionUserId());
//
//        for (MenuHit menuHit: menuHitList) {
//            MenuIndicator indicator = new MenuIndicator();
//            indicator.setMenuCode(menuHit.getMenuCode());
//            indicator.setParentMenuCode(menuHit.getMenuCode().getParentMenuCode());
//            if (isLoggedIsAsClient()) {
//                if (MenuHit.MenuCode.CLIENT_PAYMENT.equals(menuHit.getMenuCode())) {
//                    Purchase item = purchaseRepository.findTop1ByUserIdOrderByCreatedAtDesc(getSessionUserId());
//                    indicator.setOn(item  != null && item.getCreatedAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.CLIENT_ESCROW.equals(menuHit.getMenuCode())) {
//                    EscrowLog item = escrowLogRepository.findTop1ByDepositUserIdOrderByCreatedAtDesc(getSessionUserId());
//                    indicator.setOn(item  != null && item.getCreatedAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.CLIENT_DIRECT_MARKET.equals(menuHit.getMenuCode())) {
//                    DirectDeal item = directDealRepository.findTop1ByUserIdOrderByCreatedAtDesc(getSessionUserId());
//                    indicator.setOn(item  != null && item.getCreatedAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.CLIENT_PROPOSITION.equals(menuHit.getMenuCode())) {
//                    ProjectProposition item = projectPropositionRepository.findTop1ByProjectPostingClientIdOrderByCreatedAtDesc(getSessionUserId());
//                    indicator.setOn(item  != null && item.getCreatedAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.CLIENT_POSTING.equals(menuHit.getMenuCode())) {
//                    Project item = projectRepository.findTop1ByProjectTypeInAndStatusAndPostingClientIdOrderByCreatedAt(
//                            Arrays.asList(Project.Type.PROJECT, Project.Type.CONTEST_TO_PROJECT),
//                            Project.Status.POSTED, getSessionUserId());
//                    indicator.setOn(item  != null && item.getCreatedAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.CLIENT_CANCELLED.equals(menuHit.getMenuCode())) {
//                    Project item = projectRepository.findTop1ByProjectTypeInAndStatusAndPostingClientIdOrderByCreatedAt(
//                            Arrays.asList(Project.Type.PROJECT, Project.Type.CONTEST_TO_PROJECT),
//                            Project.Status.CANCELLED, getSessionUserId());
//                    indicator.setOn(item  != null && item.getCancelAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.CLIENT_TEMP_SAVED.equals(menuHit.getMenuCode())) {
//                    Project item = projectRepository.findTop1ByProjectTypeInAndStatusAndPostingClientIdOrderByCreatedAt(
//                            Arrays.asList(Project.Type.PROJECT, Project.Type.CONTEST_TO_PROJECT),
//                            Project.Status.TEMP, getSessionUserId());
//                    indicator.setOn(item  != null && item.getCreatedAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.CLIENT_IN_PROGRESS.equals(menuHit.getMenuCode())) {
//                    Project item = projectRepository.findTop1ByProjectTypeInAndStatusAndPostingClientIdOrderByCreatedAt(
//                            Arrays.asList(Project.Type.PROJECT, Project.Type.CONTEST_TO_PROJECT),
//                            Project.Status.IN_PROGRESS, getSessionUserId());
//                    indicator.setOn(item  != null && item.getStartAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.CLIENT_COMPLETED.equals(menuHit.getMenuCode())) {
//                    Project item = projectRepository.findTop1ByProjectTypeInAndStatusAndPostingClientIdOrderByCreatedAt(
//                            Arrays.asList(Project.Type.PROJECT, Project.Type.CONTEST_TO_PROJECT),
//                            Project.Status.COMPLETED, getSessionUserId());
//                    indicator.setOn(item  != null && item.getCompletedAt().isAfter(menuHit.getCreatedAt()));
//                }
//            } else {
//                if (MenuHit.MenuCode.FREELANCER_PAYMENT.equals(menuHit.getMenuCode())) {
//                    Purchase item = purchaseRepository.findTop1ByUserIdOrderByCreatedAtDesc(getSessionUserId());
//                    indicator.setOn(item  != null && item.getCreatedAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.FREELANCER_PORTFOLIO.equals(menuHit.getMenuCode())) {
//                    PickMeUp item = pickMeUpRepository.findTop1ByUserIdAndInvalidFalseOrderByCreatedAtDesc(getSessionUserId());
//                    indicator.setOn(item  != null && item.getCreatedAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.FREELANCER_PROPOSED.equals(menuHit.getMenuCode())) {
//                    ProjectProposition item = projectPropositionRepository.findTop1ByFreelancerIdOrderByCreatedAtDesc(getSessionUserId());
//                    indicator.setOn(item  != null && item.getCreatedAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.FREELANCER_FAVORITED.equals(menuHit.getMenuCode())) {
//                    ProjectFavorite item = projectFavoriteRepository.findTop1ByUserIdOrderByCreatedAtDesc(getSessionUserId());
//                    indicator.setOn(item  != null && item.getCreatedAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.FREELANCER_POSTING.equals(menuHit.getMenuCode())) {
//                    ProjectBid item = projectBidRepository.findTop1ByParticipantIdAndBidStatusAndProjectStatusOrderByApplyAtDesc(getSessionUserId(), ProjectBid.BidStatus.APPLY, Project.Status.POSTED);
//                    indicator.setOn(item  != null && item.getApplyAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.FREELANCER_SUCCESS.equals(menuHit.getMenuCode())) {
//                    ProjectBid item = projectBidRepository.findTop1ByParticipantIdAndBidStatusOrderByApplyAtDesc(getSessionUserId(), ProjectBid.BidStatus.PICKED);
//                    indicator.setOn(item  != null && item.getSuccessBidAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.FREELANCER_FAILED.equals(menuHit.getMenuCode())) {
//                    ProjectBid item = projectBidRepository.findTop1ByParticipantIdAndBidStatusOrderByApplyAtDesc(getSessionUserId(), ProjectBid.BidStatus.FAILED);
//                    indicator.setOn(item  != null && item.getFailedAt() != null && item.getFailedAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.FREELANCER_IN_PROGRESS.equals(menuHit.getMenuCode())) {
//                    ProjectBid item = projectBidRepository.findTop1ByParticipantIdAndBidStatusAndProjectStatusOrderByApplyAtDesc(getSessionUserId(), ProjectBid.BidStatus.PICKED, Project.Status.IN_PROGRESS);
//                    indicator.setOn(item  != null && item.getSuccessBidAt().isAfter(menuHit.getCreatedAt()));
//                } else if (MenuHit.MenuCode.FREELANCER_COMPLETED.equals(menuHit.getMenuCode())) {
//                    ProjectBid item = projectBidRepository.findTop1ByParticipantIdAndBidStatusAndProjectStatusOrderByApplyAtDesc(getSessionUserId(), ProjectBid.BidStatus.PICKED, Project.Status.COMPLETED);
//                    indicator.setOn(item  != null &&  item.getProject().getCompletedAt().isAfter(menuHit.getCreatedAt()));
//                }
//            }
//
//            if (MenuHit.MenuCode.NOTICE.equals(menuHit.getMenuCode())) {
//                Notice item = noticeRepository.findTop1ByOrderByCreatedAtDesc();
//                indicator.setOn(item  != null && item.getCreatedAt().isAfter(menuHit.getCreatedAt()));
//            } else if (MenuHit.MenuCode.REFERENCE.equals(menuHit.getMenuCode())) {
//                Reference item = referenceRepository.findTop1ByOrderByCreatedAtDesc();
//                indicator.setOn(item  != null && item.getCreatedAt().isAfter(menuHit.getCreatedAt()));
//            }
//
//            indicators.add(indicator);
//        }
//
//        return new CommonResponse.Builder<List<MenuIndicator>>().data(indicators).build();
    }
}
