package com.freelancerk.controller.view;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.domain.specification.EscrowLogSpecifications;
import com.freelancerk.domain.specification.EscrowRefundSpecifications;
import com.freelancerk.domain.specification.ProjectItemTicketSpecifications;
import com.freelancerk.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client/payment")
public class ClientPaymentViewController extends RootController {

    private ProjectService projectService;
    private PurchaseRepository purchaseRepository;
    private EscrowLogRepository escrowLogRepository;
    private ClientPointLogRepository clientPointLogRepository;
    private ProjectItemTicketRepository projectItemTicketRepository;
    private EscrowRefundRequestRepository escrowRefundRequestRepository;
    private ProjectItemTicketLogRepository projectItemTicketLogRepository;
    private ProjectProductItemTypeRepository projectProductItemTypeRepository;

    @Autowired
    public ClientPaymentViewController(ProjectService projectService,
                                       PurchaseRepository purchaseRepository,
                                       EscrowLogRepository escrowLogRepository,
                                       EscrowRefundRequestRepository escrowRefundRequestRepository,
                                       ClientPointLogRepository clientPointLogRepository,
                                       ProjectProductItemTypeRepository projectProductItemTypeRepository,
                                       ProjectItemTicketRepository projectItemTicketRepository,
                                       ProjectItemTicketLogRepository projectItemTicketLogRepository) {
        this.projectService = projectService;
        this.purchaseRepository = purchaseRepository;
        this.escrowLogRepository = escrowLogRepository;
        this.clientPointLogRepository = clientPointLogRepository;
        this.projectItemTicketRepository = projectItemTicketRepository;
        this.escrowRefundRequestRepository = escrowRefundRequestRepository;
        this.projectProductItemTypeRepository = projectProductItemTypeRepository;
        this.projectItemTicketLogRepository = projectItemTicketLogRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public String getClientPaymentList(Model model,
                                       @RequestParam(value = "pageNumber", defaultValue = "0", required = false) final int pageNumber,
                                       @RequestParam(value = "pageSize", defaultValue = "12", required = false) final int pageSize) {

        List<ProjectProductItemType> urgentItems = projectProductItemTypeRepository.findByUrgentTrueAndCategory(ProjectProductItemType.Category.EXTERNAL);

        Page<ProjectItemTicket> page = projectItemTicketRepository
                .findAll(ProjectItemTicketSpecifications.filter(null, null, false, getSessionUserId(), true),
                        PageRequest.of(pageNumber, pageSize));

        for (ProjectItemTicket itemTicket : page) {
            List<ProjectItemTicket> projectItemTickets = projectItemTicketRepository.findByProjectId(itemTicket.getProject().getId());
            List<Long> projectProductItemIds = projectItemTickets
                    .stream().map(ProjectItemTicket::getProjectProductItemType).map(ProjectProductItemType::getId).collect(Collectors.toList());
            Map<Long, String> optionIdSpanMap = new HashMap<>();
            for (ProjectItemTicket ticket: projectItemTickets) {
                optionIdSpanMap.put(ticket.getProjectProductItemType().getId(), ticket.getValidationDateSpans());
            }
            itemTicket.getProject().setTicketLogs(projectItemTicketLogRepository.findByProjectId(itemTicket.getProject().getId()));
            itemTicket.getProject().setUrgency(projectProductItemIds.stream().anyMatch((o) -> urgentItems.stream().map(ProjectProductItemType::getId).collect(Collectors.toList()).contains(o)));
            itemTicket.getProject().setProjectOptionItemIds(projectProductItemIds);
            itemTicket.getProject().setProjectOptionTicketValidationSpansMap(optionIdSpanMap);
            List<Purchase> allPurchases = purchaseRepository.findByProjectIdAndStatus(itemTicket.getProject().getId(), Purchase.Status.COMPLETED);
            itemTicket.getProject().setTotalPurchaseAmount(allPurchases.stream().mapToInt(Purchase::getTotalAmountOfMoney).sum());
            itemTicket.getProject().setTotalDiscountAmount(allPurchases.stream().mapToInt(Purchase::getTotalDiscountOptionPrice).sum());
            itemTicket.getProject().setTotalUsePoint(allPurchases.stream().mapToInt(Purchase::getUsedPoints).sum());
            itemTicket.getProject().setTotalChangedOptionMoney(allPurchases.stream().mapToInt(Purchase::getChargedOptionsAmountOfMoney).sum());
        }

        List<ClientPointLog> projectPointLogs = clientPointLogRepository.findByUserIdOrderByCreatedAtDesc(getSessionUserId());

        for (ClientPointLog clientPointLog: projectPointLogs) {
//            if (clientPointLog.getPurchase() != null) {
//                clientPointLog.setPurchaseOptionDescription(
//                        projectItemTicketLogRepository.findByPurchaseId(clientPointLog.getPurchase().getId()).stream()
//                                .map(ProjectItemTicketLog::getProjectProductItemType)
//                                .map(ProjectProductItemType::getName).collect(Collectors.joining(", "))
//                );
//            }
        }


        model.addAttribute("pointLogs", projectPointLogs);
        model.addAttribute("projectOptions", projectProductItemTypeRepository.findByPackFalseAndValidTrueAndProjectType(Project.Type.PROJECT));
        model.addAttribute("contestOptions", projectProductItemTypeRepository.findByPackFalseAndValidTrueAndProjectType(Project.Type.CONTEST));

        setPaginationModelData(model, pageNumber, page);

        return "client/payment/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/escrowList")
    public String getClientPaymentEscrowView(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        long totalCount = escrowLogRepository.count(EscrowLogSpecifications.searchProjectClientTransactions(
                Arrays.asList(EscrowLog.Type.DEPOSIT), null, null,
                getSessionUserId()));
        List<EscrowLog> page
                = escrowLogRepository.findAll(EscrowLogSpecifications.searchProjectClientTransactions(
                        Arrays.asList(EscrowLog.Type.DEPOSIT, EscrowLog.Type.WITHDRAWAL), null, null,
                getSessionUserId()),
                new Sort(Sort.Direction.DESC, "id"));

        model.addAttribute("totalCount", totalCount);
        model.addAttribute("page", page);
        model.addAttribute("totalRefundedCount",
                escrowRefundRequestRepository.count(EscrowRefundSpecifications.searchProjectClientTransactions(
                        Collections.singletonList(EscrowRefundRequest.Type.PROCESSED),
                        getSessionUserId()))
        );
        model.addAttribute("totalRefundRequestCount",
                escrowRefundRequestRepository.countByUserId(getSessionUserId()));
        model.addAttribute("totalDeposits",
                escrowLogRepository.findByDepositUserIdAndType(getSessionUserId(), EscrowLog.Type.DEPOSIT).stream().mapToInt(EscrowLog::getAmount).sum());
        model.addAttribute("totalWithdrawal",
                escrowLogRepository.findByWithdrawalUserIdAndType(getSessionUserId(), EscrowLog.Type.WITHDRAWAL).stream().mapToInt(EscrowLog::getAmount).sum());
        model.addAttribute("remainEscrow", projectService.getRemainEscrow(getSessionUserId()));

        return "client/payment/escrow/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/escrowRefundList")
    public String getClientPaymentEscrowList(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication).getId();

        long totalCount = escrowRefundRequestRepository.countByUserId(getSessionUserId());
        List<EscrowRefundRequest> list = escrowRefundRequestRepository.findByUserId(getSessionUserId());

        model.addAttribute("totalCount", totalCount);
        model.addAttribute("list", list);
        model.addAttribute("totalEscrowCount",
                escrowLogRepository.count(EscrowLogSpecifications.searchProjectClientTransactions(
                        Arrays.asList(EscrowLog.Type.DEPOSIT, EscrowLog.Type.WITHDRAWAL),
                        null, null, userId))
        );
        model.addAttribute("totalRefundRequestCount",
                escrowRefundRequestRepository.countByUserId(getSessionUserId()));

        model.addAttribute("totalRefundedPrice",
                escrowRefundRequestRepository.findByUserIdAndType(getSessionUserId(), EscrowRefundRequest.Type.PROCESSED).stream().mapToInt(EscrowRefundRequest::getAmount).sum());

        return "client/payment/escrow/view";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/escrowRefund")
    public String getClientPaymentEscrowRefund() {

        return "client/payment/escrow/refund";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/paymentChoice")
    public String getClientpaymentChoice(Model model) {

        model.addAttribute("mid", UUID.randomUUID().toString());

        return "client/payment/paymentChoice";
    }
    @RequestMapping(method = RequestMethod.GET, value = "/paymentDone")
    public String getClientpaymentDone() {

        return "client/payment/paymentDone";
    }
}
