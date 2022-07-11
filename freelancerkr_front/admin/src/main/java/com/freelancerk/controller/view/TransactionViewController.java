package com.freelancerk.controller.view;

import com.freelancerk.TimeUtil;
import com.freelancerk.controller.BaseController;
import com.freelancerk.domain.EscrowLog;
import com.freelancerk.domain.EscrowRefundRequest;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.repository.EscrowLogRepository;
import com.freelancerk.domain.repository.EscrowRefundRequestRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.specification.EscrowLogSpecifications;
import com.freelancerk.domain.specification.EscrowRefundSpecifications;
import com.freelancerk.domain.specification.ProjectSpecifications;
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

import java.util.List;

@RequestMapping("transaction")
@Controller
public class TransactionViewController extends BaseController {

    private ProjectRepository projectRepository;
    private EscrowLogRepository escrowLogRepository;
    private EscrowRefundRequestRepository escrowRefundRequestRepository;

    @Autowired
    public TransactionViewController(ProjectRepository projectRepository,
                                     EscrowLogRepository escrowLogRepository,
                                     EscrowRefundRequestRepository escrowRefundRequestRepository) {
        this.projectRepository = projectRepository;
        this.escrowLogRepository = escrowLogRepository;
        this.escrowRefundRequestRepository = escrowRefundRequestRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", "/depositList"})
    public String getDepositListView(Model model,
                                     @RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "depositAtFrom", required = false) String depositAtFrom,
                                     @RequestParam(value = "depositAtTo", required = false) String depositAtTo,
                                     @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                     @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize) {

        Page<Project> page = projectRepository.findAll(
                ProjectSpecifications.filter(null, null, Project.Status.IN_PROGRESS, null, Project.Type.PROJECT),
                PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

        for (Project item: page) {
            List<EscrowLog> escrowLogList = escrowLogRepository.findByProjectIdAndType(item.getId(), EscrowLog.Type.DEPOSIT);
            item.setTotalEscrowAmount(escrowLogList.stream().map(EscrowLog::getAmountWhVat).mapToInt(i->i).sum());
            item.setTotalEscrowSupplyAmount(escrowLogList.stream().map(EscrowLog::getAmount).mapToInt(i->i).sum());
            item.setTotalEscrowVatAmount(item.getTotalEscrowAmount() - item.getTotalEscrowSupplyAmount());
        }

        model.addAttribute("page", page);

        setPaginationModelData(model, pageNumber, page);

        return "transaction/depositList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/projects/deposit/form")
    public String getDepositFormView(Model model) {

        return "transaction/depositAdd";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/projects/{id}/deposit/form")
    public String updateDepositStatusView(@PathVariable("id") long projectId, Model model) {
        Project item = projectRepository.getOne(projectId);
        item.setEscrowLogs(escrowLogRepository.findByProjectIdAndType(projectId, EscrowLog.Type.DEPOSIT));
        model.addAttribute("item", item);
        model.addAttribute("totalDepositMoneyByUser",
                escrowLogRepository.findByDepositUserIdAndType(item.getPostingClient().getId(), EscrowLog.Type.DEPOSIT).stream().map(EscrowLog::getAmount).mapToInt(i->i).sum()
                        - escrowLogRepository.findByWithdrawalUserIdAndType(item.getPostingClient().getId(), EscrowLog.Type.WITHDRAWAL).stream().map(EscrowLog::getAmount).mapToInt(i->i).sum());

        return "transaction/depositAdd";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/refundList")
    public String getRefundListView(Model model,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "type", required = false) EscrowRefundRequest.Type[] types,
                                    @RequestParam(value = "createdAtFrom", required = false) String createdAtFrom,
                                    @RequestParam(value = "createdAtTo", required = false) String createdAtTo,
                                    @RequestParam(value = "refundedAtFrom", required = false) String refundedAtFrom,
                                    @RequestParam(value = "refundedAtTo", required = false) String refundedAtTo,
                                    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                    @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize) {

        Page<EscrowRefundRequest> page = escrowRefundRequestRepository.findAll(
                EscrowRefundSpecifications.filterForAdmin(
                        keyword, types, TimeUtil.convertStrToLocalDateTime(createdAtFrom), TimeUtil.convertStrToLocalDateTime(createdAtTo),
                        TimeUtil.convertStrToLocalDateTime(refundedAtFrom), TimeUtil.convertStrToLocalDateTime(refundedAtTo)),
                PageRequest.of(pageNumber, pageSize));

        model.addAttribute("page", page);
        setPaginationModelData(model, pageNumber, page);

        return "transaction/refundList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/escrow-logs")
    public String getStatisticsDepositView(@RequestParam("type") EscrowLog.Type type,
                                           @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize,
                                           Model model) {
        Page<EscrowLog> page = escrowLogRepository.findAll(EscrowLogSpecifications.filter(type),
                PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

        model.addAttribute("page", page);

        setPaginationModelData(model, pageNumber, page);

        return "transaction/statisticsDeposit";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/statisticsWithdrawal")
    public String getStatisticsWithdrawalView() {

        return "transaction/statisticsWithdrawal";
    }
}
