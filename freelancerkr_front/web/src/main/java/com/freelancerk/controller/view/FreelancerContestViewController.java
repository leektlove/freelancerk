package com.freelancerk.controller.view;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectItemTicketRepository;
import com.freelancerk.domain.repository.ProjectPropositionRepository;
import com.freelancerk.service.ProjectBidService;
import com.freelancerk.service.ProjectService;
import com.freelancerk.service.UserService;
import com.freelancerk.type.ContestDetailViewTypeForFreelancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FreelancerContestViewController extends RootController {

    private ProjectPropositionRepository projectPropositionRepository;
    private ProjectItemTicketRepository projectItemTicketRepository;
    private ProjectBidRepository projectBidRepository;
    private ProjectBidService projectBidService;
    private ProjectService projectService;
    private UserService userService;

    @Autowired
    public FreelancerContestViewController(ProjectPropositionRepository projectPropositionRepository,
                                           ProjectItemTicketRepository projectItemTicketRepository,
                                           ProjectBidService projectBidService, ProjectBidRepository projectBidRepository,
                                           ProjectService projectService, UserService userService) {
        this.projectPropositionRepository = projectPropositionRepository;
        this.projectItemTicketRepository = projectItemTicketRepository;
        this.projectBidRepository = projectBidRepository;
        this.projectBidService = projectBidService;
        this.projectService = projectService;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/freelancer/contests/{id}/details")
    public String getFreelancerContestsDetailsView(Model model, @PathVariable("id") Long contestId) {

        Project project = projectService.getById(contestId);
        ProjectBid bid = null;
        List<ProjectComment> commentList = null;
        if (isLoggedIn()) {
            bid = projectBidService.getMyContestEntry(getSessionUserId(), contestId);
            commentList = projectService.getProjectCommentForFreelancer(contestId,	project.getPostingClient().getId(), userService.getCurrentUser().getId());
        }


        List<ProjectItemTicket> validTickets = projectItemTicketRepository.findByProjectIdAndInvalidFalseAndExpiredAtAfterOrderByExpiredAtAsc(project.getId(), LocalDateTime.now());
        project.setUseNdaIp(
                validTickets.stream().map(ProjectItemTicket::getProjectProductItemType).map(ProjectProductItemType::getCode).collect(Collectors.toList())
                        .contains(ProjectProductItemType.Code.NDA_IP)
        );
        projectService.increaseHits(contestId);

        model.addAttribute("client", project.getPostingClient());
        model.addAttribute("commentList", commentList);
        model.addAttribute("contest", project);
        model.addAttribute("projectBid", bid);
        if (isLoggedIn()) {
            model.addAttribute("projectProposition", projectPropositionRepository.findByProjectIdAndFreelancerId(contestId, getSessionUserId()));
        }

        model.addAttribute("bidCompleted", bid != null && Arrays.asList(ProjectBid.BidStatus.PICKED, ProjectBid.BidStatus.FAILED).contains(bid.getBidStatus()));
        if (Project.Status.POSTED.equals(project.getStatus()) && bid == null) {

            model.addAttribute("viewType", ContestDetailViewTypeForFreelancer.BEFORE_APPLY);
        } else if (Project.Status.POSTED.equals(project.getStatus()) && bid != null) {
            List<ContestEntryFile> entryFileList = projectBidService.getAllContestEntryFile(bid.getId());
            List<ProjectContractFile> contractFiles = projectService.getProjectContractFiles(contestId);

            model.addAttribute("viewType", ContestDetailViewTypeForFreelancer.AFTER_APPLY);
            model.addAttribute("bidId", bid.getId());
            model.addAttribute("availFileCount", (3 - entryFileList.size()));	// 총 3개만 컨테스트 파일을 등록할 수 있고, 모자란 갯수만큼 파일을 추가 등록할 수 있다.
            model.addAttribute("entryFileList", entryFileList);
            model.addAttribute("contractFiles", contractFiles);

        } else if (Project.Status.IN_PROGRESS.equals(project.getStatus()) && bid != null) {
            model.addAttribute("viewType", ContestDetailViewTypeForFreelancer.AFTER_COMPLETED);
            model.addAttribute("pickedEntries", projectBidRepository.findByProjectIdAndBidStatusOrderByPickedRank(project.getId(), ProjectBid.BidStatus.PICKED));

        } else if (Project.Status.COMPLETED.equals(project.getStatus()) && bid != null) {
            model.addAttribute("viewType", ContestDetailViewTypeForFreelancer.AFTER_COMPLETED);
            model.addAttribute("pickedEntries", projectBidRepository.findByProjectIdAndBidStatusOrderByPickedRank(project.getId(), ProjectBid.BidStatus.PICKED));
        }

        return "freelancer/details/contestDetail";
    }
}
