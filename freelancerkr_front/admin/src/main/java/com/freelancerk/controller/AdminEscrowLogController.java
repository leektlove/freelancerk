package com.freelancerk.controller;

import com.freelancerk.TimeUtil;
import com.freelancerk.domain.EscrowLog;
import com.freelancerk.domain.repository.EscrowLogRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.type.PaymentMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AdminEscrowLogController {

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private EscrowLogRepository escrowLogRepository;

    @Autowired
    public AdminEscrowLogController(UserRepository userRepository, ProjectRepository projectRepository, EscrowLogRepository escrowLogRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.escrowLogRepository = escrowLogRepository;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/escrow-logs/{id}")
    public CommonResponse<Void> deleteEscrow(@PathVariable("id") Long id) {
        escrowLogRepository.deleteById(id);

        return CommonResponse.ok();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/escrow-logs")
    public void updatePaymentToUsers(
            @RequestParam(value = "id", required = false) Long id, @RequestParam("date") String date,
            @RequestParam("depositAmount") int depositAmount,
            @RequestParam("hour") int hour, @RequestParam("minute") int minute, @RequestParam("second") int second,
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(value = "clientUserId", required = false) Long clientUserId,
            @RequestParam(value = "memoFromAdmin", required = false) String memoFromAdmin,
            HttpServletResponse response) throws IOException {

        EscrowLog escrowLog = null;
        if (id == null) {
            escrowLog = new EscrowLog();
        } else {
            escrowLog = escrowLogRepository.getOne(id);
        }
        escrowLog.setPaymentMethod(PaymentMethod.TRANS);
        escrowLog.setAmountWhVat(depositAmount);
        escrowLog.setAmount((int) Math.round(depositAmount / 1.1));
        escrowLog.setType(EscrowLog.Type.DEPOSIT);
        if (clientUserId != null) {
            escrowLog.setDepositUser(userRepository.getOne(clientUserId));
        }
        if (projectId != null) {
            escrowLog.setProject(projectRepository.getOne(projectId));
        }
        if (StringUtils.isNotEmpty(memoFromAdmin)) {
            escrowLog.setMemoFromAdmin(memoFromAdmin);
        }

        escrowLog.setDepositAt(TimeUtil.convertStrWithTimeToLocalDateTime(String.format("%s %02d:%02d:%02d", date, hour, minute, second)));
        escrowLogRepository.save(escrowLog);

        response.sendRedirect("/transaction/depositList");
    }
}
