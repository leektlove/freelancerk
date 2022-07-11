package com.freelancerk.controller.view;

import com.freelancerk.TimeUtil;
import com.freelancerk.controller.BaseController;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.domain.specification.KpiClientSpecifications;
import com.freelancerk.domain.specification.KpiFreelancerSpecifications;
import com.freelancerk.io.KpiOrdering;
import com.freelancerk.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@RequestMapping("kpi")
@Controller
public class KpiViewController extends BaseController {

    private UserService userService;
    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private PurchaseRepository purchaseRepository;
    private KpiClientRepository kpiClientRepository;
    private ProjectBidRepository projectBidRepository;
    private DirectDealRepository directDealRepository;
    private PaymentToUserRepository paymentToUserRepository;
    private DailyAccessLogRepository dailyAccessLogRepository;
    private KpiFreelancerRepository kpiFreelancerRepository;
    private ProjectPropositionRepository projectPropositionRepository;

    public KpiViewController(UserService userService, UserRepository userRepository, ProjectRepository projectRepository,
                             PurchaseRepository purchaseRepository, ProjectBidRepository projectBidRepository,
                             KpiClientRepository kpiClientRepository,
                             DirectDealRepository directDealRepository, PaymentToUserRepository paymentToUserRepository,
                             DailyAccessLogRepository dailyAccessLogRepository,
                             KpiFreelancerRepository kpiFreelancerRepository,
                             ProjectPropositionRepository projectPropositionRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.purchaseRepository = purchaseRepository;
        this.kpiClientRepository = kpiClientRepository;
        this.projectBidRepository = projectBidRepository;
        this.directDealRepository = directDealRepository;
        this.paymentToUserRepository = paymentToUserRepository;
        this.kpiFreelancerRepository = kpiFreelancerRepository;
        this.dailyAccessLogRepository = dailyAccessLogRepository;
        this.projectPropositionRepository = projectPropositionRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", "/clientList"})
    public String getClientListView(Model model,
                                    @RequestParam(value = "createdFrom", required = false) String createdFrom,
                                    @RequestParam(value = "createdTo", required = false) String createdTo,
                                    @RequestParam(value = "categoryId", required = false) Long categoryId,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "ordering", required = false) KpiOrdering ordering,
                                    @RequestParam(value = "direction", required = false) Sort.Direction direction,
                                    @RequestParam(value = "useEscrow", required = false) Boolean useEscrow,
                                    @RequestParam(value = "businessType", required = false) User.BusinessType businessType,
                                    @RequestParam(value = "leave", required = false) Boolean leave,
                                    @RequestParam(value = "projectCountFrom", required = false) Long projectCountFrom,
                                    @RequestParam(value = "projectCountTo", required = false) Long projectCountTo,
                                    @RequestParam(value = "transactionAmountFrom", required = false) Long transactionAmountFrom,
                                    @RequestParam(value = "transactionAmountTo", required = false) Long transactionAmountTo,
                                    @RequestParam(value = "ratingFrom", required = false) Float ratingFrom,
                                    @RequestParam(value = "ratingTo", required = false) Float ratingTo,
                                    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "30") Integer pageSize) {

        List<User> userPage = userRepository.findByRolesContaining(User.Role.ROLE_CLIENT.name());

        for (User item: userPage) {
            List<Purchase> purchaseList = purchaseRepository.findByProjectPostingClientIdAndStatus(item.getId(), Purchase.Status.COMPLETED);
            List<PaymentToUser> paymentToUsers = paymentToUserRepository.findByProjectPostingClientIdAndStatus(item.getId(), PaymentToUser.Status.PAYED);

            KpiClient kpiClient = kpiClientRepository.findByUserId(item.getId());
            if (kpiClient == null) {
                kpiClient = new KpiClient();
            }

            kpiClient.setUser(item);
            kpiClient.setNumberOfAccessCount(dailyAccessLogRepository.countByUserId(item.getId()));
            kpiClient.setNumberOfProjectCount(projectRepository.countByPostingClientIdAndProjectTypeIn(item.getId(), Arrays.asList(Project.Type.PROJECT, Project.Type.CONTEST_TO_PROJECT)));
            kpiClient.setNumberOfContestCount(projectRepository.countByPostingClientIdAndProjectTypeIn(item.getId(), Arrays.asList(Project.Type.CONTEST)));
            kpiClient.setNumberOfProjectPropositionCount(projectPropositionRepository.countByProjectPostingClientId(item.getId()));
            kpiClient.setNumberOfDirectDealCount(directDealRepository.countByUserId(item.getId()));
            kpiClient.setNumberOfOptionCount(purchaseList.stream().mapToInt(Purchase::getNumberOfOptions).sum());
            kpiClient.setTotalTransactionAmount(paymentToUsers.stream().map(PaymentToUser::getAmount).mapToLong(Long::longValue).sum());
            kpiClient.setTotalChargedOptionPrice(purchaseList.stream().mapToInt(Purchase::getChargedOptionsAmountOfMoney).sum());
            kpiClient.setPoints(userService.getPoints(item.getId(), User.Role.ROLE_CLIENT));
            kpiClientRepository.save(kpiClient);
        }

        Page<KpiClient> page = kpiClientRepository.findAll(
                KpiClientSpecifications.filterSpecificSearch(ordering, direction, keyword, useEscrow, leave,
                        TimeUtil.convertStrToLocalDateTime(createdFrom), TimeUtil.convertStrToLocalDateTime(createdTo), businessType,
                        projectCountFrom, projectCountTo, transactionAmountFrom, transactionAmountTo, ratingFrom, ratingTo),
                PageRequest.of(pageNumber, pageSize));

        model.addAttribute("page", page);

        setPaginationModelData(model, pageNumber, page);

        return "kpi/clientList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/freelancerList")
    public String getFreelancerListView(Model model,
                                        @RequestParam(value = "createdFrom", required = false) String createdFrom,
                                        @RequestParam(value = "createdTo", required = false) String createdTo,
                                        @RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "direction", required = false) Sort.Direction direction,
                                        @RequestParam(value = "ordering", required = false) KpiOrdering ordering,
                                        @RequestParam(value = "categoryId", required = false) Long categoryId,
                                        @RequestParam(value = "userKeyword", required = false) String userKeyword,
                                        @RequestParam(value = "useEscrow", required = false) Boolean useEscrow,
                                        @RequestParam(value = "businessType", required = false) User.BusinessType businessType,
                                        @RequestParam(value = "leave", required = false) Boolean leave,
                                        @RequestParam(value = "projectCountFrom", required = false) Long projectCountFrom,
                                        @RequestParam(value = "projectCountTo", required = false) Long projectCountTo,
                                        @RequestParam(value = "transactionAmountFrom", required = false) Long transactionAmountFrom,
                                        @RequestParam(value = "transactionAmountTo", required = false) Long transactionAmountTo,
                                        @RequestParam(value = "ratingFrom", required = false) Float ratingFrom,
                                        @RequestParam(value = "ratingTo", required = false) Float ratingTo,
                                        @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "30") Integer pageSize) {

        List<User> userPage = userRepository.findByRolesContaining(User.Role.ROLE_FREELANCER.name());

        for (User item: userPage) {
            List<Purchase> purchaseList = purchaseRepository.findByProjectPostingClientIdAndStatus(item.getId(), Purchase.Status.COMPLETED);
            List<PaymentToUser> paymentToUsers = paymentToUserRepository.findByUserIdAndStatus(item.getId(), PaymentToUser.Status.PAYED);

            KpiFreelancer kpiFreelancer = kpiFreelancerRepository.findByUserId(item.getId());
            if (kpiFreelancer == null) {
                kpiFreelancer = new KpiFreelancer();
            }

            kpiFreelancer.setUser(item);
            kpiFreelancer.setNumberOfAccessCount(dailyAccessLogRepository.countByUserId(item.getId()));
            kpiFreelancer.setNumberOfProjectBids(projectBidRepository.countByParticipantIdAndProjectProjectTypeIn(item.getId(), Arrays.asList(Project.Type.PROJECT, Project.Type.CONTEST_TO_PROJECT)));
            kpiFreelancer.setNumberOfContestEntries(projectBidRepository.countByParticipantIdAndProjectProjectTypeIn(item.getId(), Arrays.asList(Project.Type.CONTEST)));
            kpiFreelancer.setNumberOfProjectPropositionCount(projectPropositionRepository.countByFreelancerId(item.getId()));
            kpiFreelancer.setNumberOfOptionCount(purchaseList.stream().mapToInt(Purchase::getNumberOfOptions).sum());
            kpiFreelancer.setTotalTransactionAmount(paymentToUsers.stream().map(PaymentToUser::getAmount).mapToLong(Long::longValue).sum());
            kpiFreelancer.setTotalTransactionCount(paymentToUsers.size());
            kpiFreelancer.setTotalChargedOptionPrice(purchaseList.stream().mapToInt(Purchase::getChargedOptionsAmountOfMoney).sum());
            kpiFreelancer.setNumberOfCompletedProject(projectRepository.countByContractedFreelancerIdAndStatus(item.getId(), Project.Status.COMPLETED));
            kpiFreelancer.setPoints(userService.getPoints(item.getId(), User.Role.ROLE_FREELANCER));
            kpiFreelancerRepository.save(kpiFreelancer);
        }

        Page<KpiFreelancer> page = kpiFreelancerRepository.findAll(
                KpiFreelancerSpecifications.filterSpecificSearch(ordering, direction, keyword, categoryId,
                        TimeUtil.convertStrToLocalDateTime(createdFrom), TimeUtil.convertStrToLocalDateTime(createdTo), userKeyword,
                        useEscrow, leave, businessType, projectCountFrom, projectCountTo, transactionAmountFrom, transactionAmountTo,
                        ratingFrom, ratingTo),
                PageRequest.of(pageNumber, pageSize));

        model.addAttribute("page", page);

        setPaginationModelData(model, pageNumber, page);

        return "kpi/freelancerList";
    }
}
