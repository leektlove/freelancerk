package com.freelancerk.controller.view;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.EscrowLog;
import com.freelancerk.domain.PaymentToUser;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.ProjectComment;
import com.freelancerk.domain.ProjectContractFile;
import com.freelancerk.domain.ProjectItemTicket;
import com.freelancerk.domain.ProjectProductItemType;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.EscrowLogRepository;
import com.freelancerk.domain.repository.PaymentToUserRepository;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectItemTicketRepository;
import com.freelancerk.domain.repository.ProjectPropositionRepository;
import com.freelancerk.service.ProjectService;
import com.freelancerk.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class FreelancerProjectDetailController extends RootController  {

    private UserService userService;
    private ProjectService projectService;
    private EscrowLogRepository escrowLogRepository;
    private ProjectBidRepository projectBidRepository;
    private PaymentToUserRepository paymentToUserRepository;
    private ProjectItemTicketRepository projectItemTicketRepository;
    private ProjectPropositionRepository projectPropositionRepository;

    @Autowired
    public FreelancerProjectDetailController(PaymentToUserRepository paymentToUserRepository,
                                             ProjectService projectService, EscrowLogRepository escrowLogRepository,
                                             UserService userService, ProjectBidRepository projectBidRepository,
                                             ProjectItemTicketRepository projectItemTicketRepository,
                                             ProjectPropositionRepository projectPropositionRepository) {
        this.userService = userService;
        this.projectService = projectService;
        this.escrowLogRepository = escrowLogRepository;
        this.projectBidRepository = projectBidRepository;
        this.paymentToUserRepository = paymentToUserRepository;
        this.projectItemTicketRepository = projectItemTicketRepository;
        this.projectPropositionRepository = projectPropositionRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/freelancer/projects/{id}/details")
    public String getFreelancerProjectDetails(@PathVariable("id") Long projectId,
                                              @RequestParam(value = "status", required = false) Project.Status status,
                                              @RequestParam(value = "referer", required = false) String referer, Model model) {
        Project project = projectService.getById(projectId);
        User client = project.getPostingClient();
        List<ProjectComment> commentList = isLoggedIn()?projectService.getProjectCommentForFreelancer(projectId, client.getId(), getSessionUserId()):new ArrayList<>();
        long escrowDepositMoney = isLoggedIn()?escrowLogRepository.findByDepositUserIdAndType(getSessionUserId(), EscrowLog.Type.DEPOSIT).stream().mapToInt(EscrowLog::getAmount).sum()
                - escrowLogRepository.findByWithdrawalUserIdAndType(getSessionUserId(), EscrowLog.Type.WITHDRAWAL).stream().mapToInt(EscrowLog::getAmount).sum():0;

        List<ProjectItemTicket> validTickets = projectItemTicketRepository.findByProjectIdAndInvalidFalseAndExpiredAtAfterOrderByExpiredAtAsc(project.getId(), LocalDateTime.now());
        project.setUseNdaIp(
                validTickets.stream().map(ProjectItemTicket::getProjectProductItemType).map(ProjectProductItemType::getCode).collect(Collectors.toList())
                        .contains(ProjectProductItemType.Code.NDA_IP)
        );

        ProjectBid bid = isLoggedIn()?projectBidRepository.findTop1ByParticipantIdAndProjectIdAndBidStatusNotOrderByCreatedAtDesc(getSessionUserId(), projectId, ProjectBid.BidStatus.CANCEL):null;
        model.addAttribute("referer", referer);
        model.addAttribute("notPostedStatusAlarm", Project.Status.POSTED.equals(status) && !Project.Status.POSTED.equals(project.getStatus()));
        model.addAttribute("client", client);
        model.addAttribute("escrowDepositMoney", escrowDepositMoney);
        model.addAttribute("project", project);
        model.addAttribute("commentList", commentList);
        model.addAttribute("projectBid", bid);
        model.addAttribute("projectProposition",
                isLoggedIn()? projectPropositionRepository.findByProjectIdAndFreelancerId(projectId, getSessionUserId()): null);

        model.addAttribute("bidCompleted", bid != null && Arrays.asList(ProjectBid.BidStatus.PICKED, ProjectBid.BidStatus.FAILED).contains(bid.getBidStatus()));
        model.addAttribute("commentHidden", "successList".equalsIgnoreCase(referer) || (bid != null && Arrays.asList(ProjectBid.BidStatus.CANCEL, ProjectBid.BidStatus.FAILED).contains(bid.getBidStatus())));

        int accumulatedIncomeAmountTotal = 0;
        if (Project.Status.IN_PROGRESS.equals(project.getStatus())) {
            User freelancer = project.getContractedFreelancer();
            List<PaymentToUser> paymentToUsers = paymentToUserRepository.findByProjectIdOrderByPayedAtDesc(projectId);
            int accumulatedIncomeAmount = 0;
            for (PaymentToUser paymentToUser: paymentToUsers) {
                accumulatedIncomeAmount += paymentToUser.getAmount();
            }
            accumulatedIncomeAmountTotal = accumulatedIncomeAmount;

            for (PaymentToUser paymentToUser: paymentToUsers) {
                paymentToUser.setAccumulatedIncomeAmount(accumulatedIncomeAmount);
                accumulatedIncomeAmount -= paymentToUser.getAmount();
            }

            List<ProjectContractFile> freeContractFiles = projectService.getProjectContractFilesByFreelancer(projectId);
            List<ProjectContractFile> clientContractFiles = projectService.getProjectContractFilesByClient(projectId);

            model.addAttribute("freelancer", freelancer);
            model.addAttribute("paymentToUsers", paymentToUsers);
            model.addAttribute("freeContractFiles", freeContractFiles);
            model.addAttribute("clientContractFiles", clientContractFiles);
        } else if (Project.Status.COMPLETED.equals(project.getStatus())) {
            List<PaymentToUser> paymentToUsers = paymentToUserRepository.findByProjectIdOrderByPayedAtDesc(projectId);
            int accumulatedIncomeAmount = 0;
            for (PaymentToUser paymentToUser: paymentToUsers) {
                accumulatedIncomeAmount += paymentToUser.getAmount();
            }
            accumulatedIncomeAmountTotal = accumulatedIncomeAmount;

            for (PaymentToUser paymentToUser: paymentToUsers) {
                paymentToUser.setAccumulatedIncomeAmount(accumulatedIncomeAmount);
                accumulatedIncomeAmount -= paymentToUser.getAmount();
            }

            model.addAttribute("paymentToUsers", paymentToUsers);
        }

        model.addAttribute("loginAsClient", isLoggedIn() && isLoggedIsAsClient());
        model.addAttribute("accumulatedIncomeAmountTotal", accumulatedIncomeAmountTotal);
        model.addAttribute("escrowAmount", projectService.getRemainEscrow(project.getPostingClient().getId()));

        projectService.increaseHits(projectId);

        return "freelancer/details/projectDetail";
    }
}
