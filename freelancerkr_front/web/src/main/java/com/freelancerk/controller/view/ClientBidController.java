package com.freelancerk.controller.view;


import com.freelancerk.TimeUtil;
import com.freelancerk.controller.RootController;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.domain.specification.ProjectBidSpecifications;
import com.freelancerk.domain.specification.ProjectPropositionSpecifications;
import com.freelancerk.domain.specification.ProjectSpecifications;
import com.freelancerk.io.BidSortBy;
import com.freelancerk.model.SortBy;
import com.freelancerk.service.ProjectBidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client/bid")
public class ClientBidController extends RootController {

    @Value("${server.url}") String serverUrl;
    private ProjectRepository projectRepository;
    private ProjectBidService projectBidService;
    private PurchaseRepository purchaseRepository;
    private ProjectBidRepository projectBidRepository;
    private PaymentToUserRepository paymentToUserRepository;
    private ProjectCommentRepository projectCommentRepository;
    private ProjectCategoryRepository projectCategoryRepository;
    private ProjectPropositionRepository projectPropositionRepository;
    private ContestEntryTicketRepository contestEntryTicketRepository;
    private FreelancerPayProductRepository freelancerPayProductRepository;
    private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;

    @Autowired
    public ClientBidController(ProjectRepository projectRepository, ProjectBidRepository projectBidRepository,
                               PurchaseRepository purchaseRepository, ProjectCommentRepository projectCommentRepository,
                               PaymentToUserRepository paymentToUserRepository,
                               ProjectPropositionRepository projectPropositionRepository,
                               ProjectCategoryRepository projectCategoryRepository,
                               ProjectBidService projectBidService, ContestEntryTicketRepository contestEntryTicketRepository,
                               FreelancerPayProductRepository freelancerPayProductRepository,
                               FreelancerProductItemTypeRepository freelancerProductItemTypeRepository) {
        this.projectRepository = projectRepository;
        this.projectBidService = projectBidService;
        this.purchaseRepository = purchaseRepository;
        this.projectBidRepository = projectBidRepository;
        this.paymentToUserRepository = paymentToUserRepository;
        this.projectCommentRepository = projectCommentRepository;
        this.projectCategoryRepository = projectCategoryRepository;
        this.projectPropositionRepository = projectPropositionRepository;
        this.contestEntryTicketRepository = contestEntryTicketRepository;
        this.freelancerPayProductRepository = freelancerPayProductRepository;
        this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/processingList")
    public String getProcessingList(Model model,
                                    @RequestParam(value = "pageNumber", defaultValue = "0", required = false) final int pageNumber,
                                    @RequestParam(value = "pageSize", defaultValue = "10", required = false) final int pageSize,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "status", required = false) Project.Status status,
                                    @RequestParam(value = "type", required = false) Project.Type type) {
        FreelancerProductItemType pickMeUpCreativeItem = freelancerProductItemTypeRepository.findByCode(FreelancerProductItemType.Code.CREATIVE);
        FreelancerProductItemType pickMeUpFeaturedItem = freelancerProductItemTypeRepository.findByCode(FreelancerProductItemType.Code.FEATURED);
        FreelancerProductItemType pickMeUpHighQualityItem = freelancerProductItemTypeRepository.findByCode(FreelancerProductItemType.Code.HIGH_QUALITY);
        FreelancerProductItemType pickMeUpDirectDealItem = freelancerProductItemTypeRepository.findByCode(FreelancerProductItemType.Code.DIRECT_DEAL);

        Page<Project> projectPage = projectRepository.findAll(ProjectSpecifications.filter(getSessionUserId(), keyword, Project.Status.POSTED, null, type),
                PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createdAt")));

        for (Project project: projectPage) {

            List<FreelancerPayProduct> payProducts = freelancerPayProductRepository.findByContestEntryProjectIdAndPurchaseStatusOrderByIdAsc(project.getId(), Purchase.Status.COMPLETED);
            project.setPriorityProjectBids(payProducts.stream().filter(product->product.getFreelancerProductItemType().isPriority()).map(FreelancerPayProduct::getContestEntry).collect(Collectors.toList()));
//            project.setProjectBids(payProducts.stream().filter(product->!product.getFreelancerProductItemType().isPriority()).map(FreelancerPayProduct::getContestEntry).collect(Collectors.toList()));
            project.setProjectBids(projectBidRepository.findByProjectIdAndInvalidFalse(project.getId()));
            project.setMessageCountByFreelancer(projectCommentRepository.countByProjectIdAndUserIdNot(project.getId(), project.getPostingClient().getId()));

            if (Project.Type.CONTEST.equals(project.getProjectType())) {
                for (ProjectBid bid: project.getProjectBids()) {
                    List<FreelancerProductItemType> pickMeUpPayProducts
                            = contestEntryTicketRepository.findByProjectBidIdAndEndAtAfterOrderByEndAtAsc(bid.getId(), LocalDateTime.now())
                            .stream()
                            .map(ContestEntryTicket::getFreelancerProductItemType)
                            .collect(Collectors.toList());

                    bid.setCreative(pickMeUpPayProducts.contains(pickMeUpCreativeItem));
                    bid.setHighQuality(pickMeUpPayProducts.contains(pickMeUpHighQualityItem));
                    bid.setDirectDealAvailable(pickMeUpPayProducts.contains(pickMeUpDirectDealItem));
                    bid.setFeatured(pickMeUpPayProducts.contains(pickMeUpFeaturedItem));
                }
            }
        }

        setPaginationModelData(model, pageNumber, projectPage);
        setBidStatusCounts(model);
        return "client/bid/processing/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/processingDue")
    public String getProcessingDueView() {

        return "client/bid/processing/due";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/processingCancel")
    public String getProcessingCancelView(Model model,
                                          @RequestParam(value = "keyword", required = false) String keyword,
                                          @RequestParam(value = "status", required = false) Project.Status status,
                                          @RequestParam(value = "type", required = false) Project.Type type) {

        model.addAttribute("items", projectRepository.findAll(ProjectSpecifications.filter(getSessionUserId(), keyword, Project.Status.CANCELLED, null, type)));

        return "client/bid/processing/cancel";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/processingContest")
    public String getProcessingContestView() {

        return "client/bid/processing/contest";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/processingProject")
    public String getProcessingProjectView() {

        return "client/bid/processing/project";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/cancelList")
    public String getCancelListView(Model model, @RequestParam(value = "bidNo", required = false) Long bidNo,
                                    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        if (bidNo != null && bidNo > 0) {
            return "client/bid/cancel/view";
        }
        Page<Project> page = projectRepository.findAll(
                ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.CANCELLED, null, null), PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "cancelAt")));
        for (Project project: page) {
            project.setProjectBids(projectBidRepository.findByProjectIdAndInvalidFalse(project.getId()));
            project.setAvgCareerYear(String.format("%.1f", projectBidService.getAvgOfProjectBidCareerYear(project.getId())));
            project.setAllPrimaryContestEntryFiles(projectBidService.getAllContestEntryRepresentativeFileList(project.getId()));
            List<Purchase> purchases = purchaseRepository.findByProjectIdAndStatusInOrderByCreatedAtDesc(project.getId(), Arrays.asList(Purchase.Status.COMPLETED, Purchase.Status.REFUNDED));
            project.setTotalChangedOptionMoney(purchases.stream().mapToInt(Purchase::getChargedOptionsAmountOfMoney).sum());
            project.setTotalSupplyAmount(purchases.stream().mapToInt(Purchase::getSupplyAmountOfMoney).sum());
            project.setTotalVatAmount(purchases.stream().mapToInt(Purchase::getVatAmountOfMoney).sum());
            project.setTotalDiscountAmount(purchases.stream().mapToInt(Purchase::getTotalDiscountOptionPrice).sum());
            project.setTotalPurchaseAmount(purchases.stream().mapToInt(Purchase::getTotalAmountOfMoney).sum());
            if (!purchases.isEmpty()) {
                project.setFirstPurchasedAt(purchases.get(0).getCreatedAt());
            }
        }

        setPaginationModelData(model, pageNumber, page);
        setBidStatusCounts(model);
        return "client/bid/cancel/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/autoSave")
    public String getAutoSaveView(Model model,
                                  @RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "type", required = false) Project.Type type,
                                  @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {

        Page<Project> page = projectRepository.findAll(ProjectSpecifications.filter(getSessionUserId(), keyword, Project.Status.TEMP, null, type),
                PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createdAt")));
        
        setBidStatusCounts(model);
        setPaginationModelData(model, pageNumber, page);
        return "client/bid/autoSave";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/suggestList")
    public String getSuggestList(Model model,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "startAt", required = false) String startAt,
                                 @RequestParam(value = "endAt", required = false) String endAt,
                                 @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                 @RequestParam(value = "pageSize", required = false, defaultValue = "80") Integer pageSize) {

        List<Long> pickMeUpIds = new ArrayList<>();
        Page<ProjectProposition> page = projectPropositionRepository.findAll(
                ProjectPropositionSpecifications.filterForClient(
                        getSessionUserId(),
                        keyword,
                        TimeUtil.convertStrToLocalDateTime(startAt),
                        TimeUtil.convertStrToLocalDateTime(endAt))
                , PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createdAt")));

        for (ProjectProposition item: page) {
            if (item.getPickMeUp() != null) {
                pickMeUpIds.add(item.getPickMeUp().getId());
            }
        }
        model.addAttribute("items", page);
        model.addAttribute("pickMeUpIds", pickMeUpIds);
        setPaginationModelData(model, pageNumber, page);

        setBidStatusCounts(model);
        return "client/bid/suggestList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/bidderList")
    public String getBidderList(Model model, @RequestParam("projectId") Long projectId,
                                @RequestParam(value = "sortBy", required = false) BidSortBy sortBy) {

        Project project = projectRepository.getOne(projectId);
        if (!getSessionUserId().equals(project.getPostingClient().getId())) {
            return String.format("redirect:%s/main", serverUrl);
        }

        List<ProjectBid> projectBids = new ArrayList<>();

        if (sortBy == null || BidSortBy.CREATED_AT_DESC.equals(sortBy)) {
            projectBids = projectBidRepository.findByProjectIdOrderByCreatedAtAsc(projectId);
        } else if (BidSortBy.AMOUNT_ASC.equals(sortBy)) {
            projectBids = projectBidRepository.findByProjectIdOrderByAmountOfMoneyAsc(projectId);
        } else if (BidSortBy.AMOUNT_DESC.equals(sortBy)) {
            projectBids = projectBidRepository.findByProjectIdOrderByAmountOfMoneyDesc(projectId);
        }

        for (ProjectBid projectBid: projectBids) {
            projectBid.setParticipantAccumulatedProjectMoney(
                    paymentToUserRepository.findByUserIdAndStatus(projectBid.getParticipant().getId(), PaymentToUser.Status.PAYED).stream().map(PaymentToUser::getAmount).mapToLong(Long::longValue).sum()
            );
            projectBid.setParticipantCompletedProjectCount(projectBidRepository.count(ProjectBidSpecifications.filter(
                    projectBid.getParticipant().getId(), null, null, ProjectBid.BidStatus.PICKED, Project.Status.COMPLETED, null, SortBy.CREATED_AT)));
            Set<Category> projectCategories = projectCategoryRepository.findByProjectId(projectBid.getProject().getId())
                    .stream().map(ProjectCategory::getCategory).collect(Collectors.toSet());
            Set<Category> freelancerCategories = projectBid.getParticipant().getCategories();

            List<Category> matchedCategories = new ArrayList<>();

            for (Category category: projectCategories) {
                if (freelancerCategories.contains(category)) {
                    matchedCategories.add(category);
                }
            }

            projectBid.setMatchedCategories(matchedCategories);
        }

        model.addAttribute("items", projectBids);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("project", project);
        model.addAttribute("projectId", projectId);
        setBidStatusCounts(model);
        return "client/bid/processing/bidderList";
    }

    private void setBidStatusCounts(Model model) {
        model.addAttribute(
                "processingCount",
                projectRepository.count(ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.POSTED, null,null)));
        model.addAttribute(
                "cancelledCount",
                projectRepository.count(ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.CANCELLED, null, null)));
        model.addAttribute(
                "tempSavedCount",
                projectRepository.count(ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.TEMP, null, null))
        );
        model.addAttribute(
                "propositionsCount",
                projectPropositionRepository.count(ProjectPropositionSpecifications.filterForClient(getSessionUserId(), null, null, null))
        );
    }
}
