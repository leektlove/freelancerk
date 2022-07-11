package com.freelancerk.controller.view;

import com.freelancerk.TimeUtil;
import com.freelancerk.controller.BaseController;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.domain.specification.ProjectSpecifications;
import com.freelancerk.domain.specification.UserSpecifications;
import com.freelancerk.io.ProjectOrdering;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/project")
public class AdminProjectViewController extends BaseController {

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private PurchaseRepository purchaseRepository;
    private CategoryRepository categoryRepository;
    private EscrowLogRepository escrowLogRepository;
    private ProjectBidRepository projectBidRepository;
    private ProjectRateRepository projectRateRepository;
    private PaymentToUserRepository paymentToUserRepository;
    private ProjectCommentRepository projectCommentRepository;
    private ProjectCategoryRepository projectCategoryRepository;
    private ProjectItemTicketRepository projectItemTicketRepository;
    private ProjectPropositionRepository projectPropositionRepository;
    private EscrowRefundRequestRepository escrowRefundRequestRepository;
    private ProjectItemTicketLogRepository projectItemTicketLogRepository;

    @Autowired
    public AdminProjectViewController(UserRepository userRepository,
                                      ProjectRepository projectRepository,
                                      PurchaseRepository purchaseRepository,
                                      CategoryRepository categoryRepository,
                                      EscrowLogRepository escrowLogRepository,
                                      ProjectBidRepository projectBidRepository,
                                      ProjectRateRepository projectRateRepository,
                                      PaymentToUserRepository paymentToUserRepository,
                                      ProjectCommentRepository projectCommentRepository,
                                      ProjectCategoryRepository projectCategoryRepository,
                                      ProjectItemTicketRepository projectItemTicketRepository,
                                      ProjectPropositionRepository projectPropositionRepository,
                                      EscrowRefundRequestRepository escrowRefundRequestRepository,
                                      ProjectItemTicketLogRepository projectItemTicketLogRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.purchaseRepository = purchaseRepository;
        this.categoryRepository = categoryRepository;
        this.escrowLogRepository = escrowLogRepository;
        this.projectBidRepository = projectBidRepository;
        this.projectRateRepository = projectRateRepository;
        this.paymentToUserRepository = paymentToUserRepository;
        this.projectCommentRepository = projectCommentRepository;
        this.projectCategoryRepository = projectCategoryRepository;
        this.projectItemTicketRepository = projectItemTicketRepository;
        this.projectPropositionRepository = projectPropositionRepository;
        this.escrowRefundRequestRepository = escrowRefundRequestRepository;
        this.projectItemTicketLogRepository = projectItemTicketLogRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", "/list"})
    public String getListView(Model model,
                              @RequestParam(value = "ordering", required = false) ProjectOrdering projectOrdering,
                              @RequestParam(value = "direction", required = false)  Sort.Direction direction,
                              @RequestParam(value = "createdFrom", required = false) String createdAtFrom,
                              @RequestParam(value = "createdTo", required = false) String createdAtTo,
                              @RequestParam(value = "categoryId", required = false) Long categoryId,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "projectKeyword", required = false) String projectKeyword,
                              @RequestParam(value = "status", required = false) Project.Status status,
                              @RequestParam(value = "bidCountFrom", required = false) Integer bidCountFrom,
                              @RequestParam(value = "bidCountTo", required = false) Integer bidCountTo,
                              @RequestParam(value = "successBidFrom", required = false) Integer successBidFrom,
                              @RequestParam(value = "successBidTo", required = false) Integer successBidTo,
                              @RequestParam(value = "expectedPeriod", required = false) Project.ExpectedPeriod expectedPeriod,
                              @RequestParam(value = "budgetFrom", required = false) Integer budgetFrom,
                              @RequestParam(value = "budgetTo", required = false) Integer budgetTo,
                              @RequestParam(value = "payCriteria", required = false) Project.PayCriteria payCriteria,
                              @RequestParam(value = "workPlace", required = false) Project.WorkPlace workPlace,
                              @RequestParam(value = "postingStartFrom", required = false) String postringStartFrom,
                              @RequestParam(value = "postingEndTo", required = false) String postingEndTo,
                              @RequestParam(value = "useEscrow", required = false) Boolean useEscrow,
                              @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                              @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize) {

        Page<Project> page = projectRepository.findAll(
                ProjectSpecifications.filterForAdmin(projectOrdering, direction, keyword, categoryId, TimeUtil.convertStrToLocalDateTime(createdAtFrom),
                        TimeUtil.convertStrToLocalDateTime(createdAtTo), projectKeyword, status, bidCountFrom, bidCountTo, successBidFrom, successBidTo,
                        expectedPeriod, budgetFrom, budgetTo, payCriteria, workPlace, TimeUtil.convertStrToLocalDateTime(postringStartFrom), TimeUtil.convertStrToLocalDateTime(postingEndTo), useEscrow),
                PageRequest.of(pageNumber, pageSize));

        for (Project item: page) {
            List<ProjectBid> projectBids = projectBidRepository.findByProjectIdAndBidStatusOrderByPickedRank(item.getId(), ProjectBid.BidStatus.PICKED);
            if (projectBids.size() > 0) {
                item.setPickedProjectBid(projectBids.get(0));
            }
            item.setProjectBids(projectBidRepository.findByProjectIdAndInvalidFalse(item.getId()));
            item.setProjectPropositionCount(projectPropositionRepository.countByProjectId(item.getId()));
            long escrowDepositMoney = escrowLogRepository.findByDepositUserIdAndType(item.getPostingClient().getId(), EscrowLog.Type.DEPOSIT).stream().mapToInt(EscrowLog::getAmount).sum()
                    - escrowLogRepository.findByDepositUserIdAndType(item.getPostingClient().getId(), EscrowLog.Type.WITHDRAWAL).stream().mapToInt(EscrowLog::getAmount).sum();
            long refundedEscrowMoney = escrowRefundRequestRepository.findByUserIdAndType(item.getPostingClient().getId(), EscrowRefundRequest.Type.PROCESSED).stream().mapToInt(EscrowRefundRequest::getAmount).sum();
            item.setCurrentEscrowAmount(escrowDepositMoney - refundedEscrowMoney);
            item.setRePurchase(purchaseRepository.findByProjectIdAndStatus(item.getId(), Purchase.Status.COMPLETED).size() > 0);
        }

        model.addAttribute("page", page);
        model.addAttribute("category1st", categoryRepository.findByParentCategoryIsNullOrderBySeqAsc());

        setPaginationModelData(model, pageNumber, page);

        return "project/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/details/basic")
    public String getProjectDetailView(@PathVariable("id") Long id, Model model) {
        Project project = projectRepository.getOne(id);
        List<ProjectCategory> projectCategories = projectCategoryRepository.findByProjectId(id);
        Map<Category, List<Category>> categoryMap = new HashMap<>();

        for (ProjectCategory projectCategory: projectCategories) {
            categoryMap.computeIfAbsent(projectCategory.getCategory().getParentCategory(), k -> new ArrayList<>());
            List<Category> projectCategoryList = categoryMap.get(projectCategory.getCategory().getParentCategory());
            projectCategoryList.add(projectCategory.getCategory());
        }

        model.addAttribute("ticketLogs", projectItemTicketLogRepository.findByProjectId(id));
        model.addAttribute("purchaseCount", purchaseRepository.countByProjectIdAndStatusAndType(id, Purchase.Status.COMPLETED, Purchase.Type.PROJECT));
        model.addAttribute("categoryMap", categoryMap);
        model.addAttribute("item", project);

        return "project/projectDetailBasic";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/details/bid")
    public String getProjectBidDetailView(@PathVariable("id") Long id, Model model) {
        model.addAttribute("item", projectRepository.getOne(id));
        model.addAttribute("bids", projectBidRepository.findByProjectIdAndInvalidFalse(id));
        model.addAttribute("rateByClient", projectRateRepository.findByProjectIdAndRaterType(id, ProjectRate.RaterType.CLIENT));
        model.addAttribute("rateByFreelancer", projectRateRepository.findByProjectIdAndRaterType(id, ProjectRate.RaterType.FREELANCER));
        model.addAttribute("paymentToUsers", paymentToUserRepository.findByProjectIdOrderByCreatedAtDesc(id));
        return "project/projectDetailBid";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/details/work")
    public String getProjectWorkDetailView(@PathVariable("id") Long id, Model model) {
        model.addAttribute("item", projectRepository.getOne(id));

        return "project/projectDetailWork";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/details/message")
    public String getProjectMessageDetailView(@PathVariable("id") Long id, Model model) {
        model.addAttribute("item", projectRepository.getOne(id));
        model.addAttribute("comments", projectCommentRepository.findByProjectIdOrderByCreatedAtAsc(id));

        return "project/projectDetailMessage";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/add")
    public String getAddView(@RequestParam(value = "clientNameKeyword", required = false) String clientNameKeyword,
                             @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                             @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize,
                             Model model) {

        model.addAttribute("users", userRepository.findAll(UserSpecifications.filterForClient(clientNameKeyword, false), PageRequest.of(pageNumber, pageSize)));
        return "project/projectAdd";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/mails")
    public String getMailListView() {

        return "project/mailList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/mails/{id}")
    public String getMailDetailView() {

        return "project/mailDetail";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/smses")
    public String getSmsListView() {

        return "project/smsList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/smses/{id}")
    public String getSmsDetailView() {

        return "project/smsDetail";
    }
}
