package com.freelancerk.controller.view;

import com.freelancerk.controller.RootController;
import com.freelancerk.controller.io.ProjectSort;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.domain.specification.ProjectCommentSpecifications;
import com.freelancerk.domain.specification.ProjectSpecifications;
import com.freelancerk.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client/project")
public class ClientProjectViewController extends RootController {

    private UserRepository userRepository;
    private ProjectService projectService;
    private ProjectRepository projectRepository;
    private ProjectBidRepository projectBidRepository;
    private PaymentToUserRepository paymentRepository;
    private ProjectRateRepository projectRateRepository;
    private PaymentToUserRepository paymentToUserRepository;
    private ProjectCommentRepository projectCommentRepository;
    private ProjectCompleteRepository projectCompleteRepository;
    private ProjectItemTicketRepository projectItemTicketRepository;
    private ContestEntryRewardRepository contestEntryRewardRepository;
    private ProjectContractFileRepository projectContractFileRepository;

    @Autowired
    public ClientProjectViewController(UserRepository userRepository, ProjectRepository projectRepository, ProjectService projectService,
                                       ProjectBidRepository projectBidRepository, PaymentToUserRepository paymentRepository,
                                       ProjectRateRepository projectRateRepository, PaymentToUserRepository paymentToUserRepository,
                                       ProjectCommentRepository projectCommentRepository, ProjectCompleteRepository projectCompleteRepository,
                                       ProjectItemTicketRepository projectItemTicketRepository,
                                       ContestEntryRewardRepository contestEntryRewardRepository,
                                       ProjectContractFileRepository projectContractFileRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.projectBidRepository = projectBidRepository;
        this.paymentRepository = paymentRepository;
        this.projectRateRepository = projectRateRepository;
        this.paymentToUserRepository = paymentToUserRepository;
        this.projectCommentRepository = projectCommentRepository;
        this.projectCompleteRepository = projectCompleteRepository;
        this.projectItemTicketRepository = projectItemTicketRepository;
        this.contestEntryRewardRepository = contestEntryRewardRepository;
        this.projectContractFileRepository = projectContractFileRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/processingList")
    public String getClientProcessingListView(Model model,
                                              @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                              @RequestParam(value = "sort", required = false) ProjectSort projectSort,
                                              @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction) {
        long doneProjectCount = projectRepository.count(ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.COMPLETED, null, null));

        Page<Project> page = projectRepository.findAll(
                ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.IN_PROGRESS, null, null), PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "startAt")));

        for (Project project: page) {
            if (Project.Type.PROJECT.equals(project.getProjectType())) {
                project.setPaymentToUsers(paymentToUserRepository.findByProjectIdOrderByCreatedAtDesc(project.getId()));
                if (!project.getPaymentToUsers().isEmpty()) {
                    project.setLastPaymentToUser(project.getPaymentToUsers().get(0));
                }
                long totalIncomeAmount = paymentToUserRepository.findByProjectIdAndStatus(project.getId(), PaymentToUser.Status.PAYED).stream().map(PaymentToUser::getAmount).mapToLong(Long::longValue).sum();
                project.setTotalIncomeAmount(totalIncomeAmount);
                project.setProjectComplete(projectCompleteRepository.findByProjectId(project.getId()));
            } else if (Project.Type.CONTEST.equals(project.getProjectType())) {
                project.setPickedContestEntries(projectBidRepository.findByProjectIdAndBidStatusOrderByPickedRank(project.getId(), ProjectBid.BidStatus.PICKED));
                for (ProjectBid contestEntry : project.getPickedContestEntries()) {
                    contestEntry.setContestEntryReward(contestEntryRewardRepository.findByProjectBidId(contestEntry.getId()));
                    contestEntry.setLastPayment(paymentRepository.findTop1ByContestEntryRewardProjectBidIdOrderByCreatedAtDesc(contestEntry.getId()));
                }
            }
            project.setRemainEscrowAmount(projectService.getRemainEscrow(project.getPostingClient().getId()));
            if (project.getContractedFreelancer() != null) {
                project.setMessageCountByFreelancer(projectCommentRepository.countByProjectIdAndUserIdAndCreatedAtAfter(project.getId(), project.getContractedFreelancer().getId(), project.getStartAt()));
            }
        }

        setPaginationModelData(model, pageNumber, page);

        model.addAttribute("items", page.getContent());
        model.addAttribute("processingCount", page.getTotalElements());
        model.addAttribute("doneCount", doneProjectCount);
        return "client/project/processing";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/doneList")
    public String getClientDoneListView(Model model,
                                        @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                        @RequestParam(value = "startDate", required = false) String startDate,
                                        @RequestParam(value = "endDate", required = false) String endDate,
                                        @RequestParam(value = "sort", required = false) ProjectSort projectSort,
                                        @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction) {

        long processingProjectCount = projectRepository.count(ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.IN_PROGRESS, null, null));

        Page<Project> completedProjects = projectRepository.findAll(
                ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.COMPLETED, null, null),
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "postingEndAt")));


        long completedAmount = paymentToUserRepository.findByProjectPostingClientIdAndStatus(getSessionUserId(), PaymentToUser.Status.PAYED)
                .stream().map(PaymentToUser::getAmount).mapToLong(Long::longValue).sum();
        List<Project> completedContest = projectRepository.findAll(ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.COMPLETED, null, Project.Type.CONTEST));
        for (Project contest: completedContest) {
            completedAmount += contest.getTotalPrize();
        }
        for (Project project: completedProjects) {
            if (Project.Type.PROJECT.equals(project.getProjectType())) {
                List<ProjectBid> projectBids = projectBidRepository.findByProjectIdAndBidStatusOrderByPickedRank(project.getId(), ProjectBid.BidStatus.PICKED);
                if (projectBids.size() > 0) {
                    project.setPickedProjectBid(projectBids.get(0));
                }

                long totalIncomeAmount = paymentToUserRepository.findByProjectIdAndStatus(project.getId(), PaymentToUser.Status.PAYED).stream().map(PaymentToUser::getAmount).mapToLong(Long::longValue).sum();
                project.setTotalIncomeAmount(totalIncomeAmount);
                project.setPaymentToUsers(paymentToUserRepository.findByProjectIdOrderByCreatedAtDesc(project.getId()));
                if (!project.getPaymentToUsers().isEmpty()) {
                    project.setLastPaymentToUser(project.getPaymentToUsers().get(0));
                }
            } else if (Project.Type.CONTEST.equals(project.getProjectType())) {
                project.setSuccessBidPrice(0);
                project.setPickedContestEntries(projectBidRepository.findByProjectIdAndBidStatusOrderByPickedRank(project.getId(), ProjectBid.BidStatus.PICKED));
            }
        }

        List<ProjectRate> projectRates = projectRateRepository.findByRatedUserIdAndRaterTypeAndProjectStatus(
                getSessionUserId(), ProjectRate.RaterType.FREELANCER, Project.Status.COMPLETED);

        model.addAttribute("type1Rate", projectRates.size() == 0?0:100* projectRates.stream().map(ProjectRate::getType1Rate).mapToInt(Integer::intValue).sum() / (5*projectRates.size()));
        model.addAttribute("type2Rate", projectRates.size() == 0?0:100* projectRates.stream().map(ProjectRate::getType2Rate).mapToInt(Integer::intValue).sum() / (5*projectRates.size()));
        model.addAttribute("type3Rate", projectRates.size() == 0?0:100* projectRates.stream().map(ProjectRate::getType3Rate).mapToInt(Integer::intValue).sum() / (5*projectRates.size()));
        model.addAttribute("type4Rate", projectRates.size() == 0?0:100* projectRates.stream().map(ProjectRate::getType4Rate).mapToInt(Integer::intValue).sum() / (5*projectRates.size()));
        model.addAttribute("type5Rate", projectRates.size() == 0?0:100* projectRates.stream().map(ProjectRate::getType5Rate).mapToInt(Integer::intValue).sum() / (5*projectRates.size()));
        model.addAttribute("items", completedProjects);
        model.addAttribute("processingCount", processingProjectCount);
        model.addAttribute("completedAmount", completedAmount);
        model.addAttribute("user", userRepository.getOne(getSessionUserId()));
        return "client/project/done/view";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/doneStartWithSelectedSpecialist")
    public String getClientDoneStartWithSelectedSpecialistView(Model model) {

        model.addAttribute("user", userRepository.getOne(getSessionUserId()));
        return "client/project/done/startWithSelectedSpecialist";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/details")
    public String getClientProjectDetailView(@PathVariable("id") Long id, Model model) {
        Project project = projectRepository.getOne(id);
        List<ProjectComment> commentList = new ArrayList<>();

        if (isLoggedIn() && !project.getPostingClient().getId().equals(getSessionUserId())) {
            projectService.increaseHits(id);
        }
        model.addAttribute("project", project);
        int accumulatedIncomeAmountTotal = 0;
        if (Project.Status.POSTED.equals(project.getStatus())) {
            commentList = projectCommentRepository.findByProjectIdOrderByCreatedAtAsc(id);
        } else if (Arrays.asList(Project.Status.IN_PROGRESS, Project.Status.COMPLETED).contains(project.getStatus())) {
            commentList = projectCommentRepository.findAll(
                    ProjectCommentSpecifications.filterForInProgressForClient(id, project.getPostingClient().getId(), project.getContractedFreelancer().getId()), Sort.by(Sort.Direction.ASC, "createdAt"));
            commentList = commentList.stream().filter(i -> i.getCreatedAt().isAfter(i.getProject().getStartAt())).collect(Collectors.toList());
            List<PaymentToUser> paymentToUsers = paymentToUserRepository.findByProjectIdOrderByCreatedAtDesc(project.getId());
            model.addAttribute("freeContractFiles", projectService.getProjectContractFilesByFreelancer(id));
            model.addAttribute("contractFiles", projectContractFileRepository.findByProjectIdAndUserRoleAndInvalidFalse(project.getId(), User.Role.ROLE_CLIENT));
            model.addAttribute("paymentToUsers", paymentToUsers);
            int accumulatedIncomeAmount = 0;
            for (PaymentToUser paymentToUser: paymentToUsers) {
                accumulatedIncomeAmount += paymentToUser.getAmount();
            }
            accumulatedIncomeAmountTotal = accumulatedIncomeAmount;

            for (PaymentToUser paymentToUser: paymentToUsers) {
                paymentToUser.setAccumulatedIncomeAmount(accumulatedIncomeAmount);
                accumulatedIncomeAmount -= paymentToUser.getAmount();
            }

            project.setRemainEscrowAmount(projectService.getRemainEscrow(project.getPostingClient().getId()));
        }

        model.addAttribute("loginAsClient", isLoggedIn() && isLoggedIsAsClient());
        model.addAttribute("commentList", commentList);

        model.addAttribute("accumulatedIncomeAmountTotal", accumulatedIncomeAmountTotal);
        model.addAttribute("escrowAmount", projectService.getRemainEscrow(id));

        return "client/details/projectDetail";
    }
}
