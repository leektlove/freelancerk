package com.freelancerk.controller;

import com.freelancerk.domain.EscrowLog;
import com.freelancerk.domain.EscrowRefundRequest;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.EscrowLogRepository;
import com.freelancerk.domain.repository.EscrowRefundRequestRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.type.PaymentMethod;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Api(tags = "에스크로", description = "내역 조회/환불 신청 등")
@RestController
public class EscrowController extends RootController {

    private UserRepository userRepository;
    private EscrowLogRepository escrowLogRepository;
    private EscrowRefundRequestRepository escrowRefundRepository;

    @Autowired
    public EscrowController(UserRepository userRepository, EscrowLogRepository escrowLogRepository,
                            EscrowRefundRequestRepository escrowRefundRepository) {
        this.userRepository = userRepository;
        this.escrowLogRepository = escrowLogRepository;
        this.escrowRefundRepository = escrowRefundRepository;
    }

    @ApiOperation("환불신청")
    @RequestMapping(method = RequestMethod.POST, value = "/escrows/refund-requests")
    public CommonResponse requestRefundEscrow(@RequestParam("amount") int amount, @RequestParam("reason") String reason) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        User user = userRepository.getOne(userId);

        EscrowRefundRequest escrowRefund = new EscrowRefundRequest();
        escrowRefund.setUser(user);
        escrowRefund.setAmount(amount);
        escrowRefund.setReason(reason);
        escrowRefund.setType(EscrowRefundRequest.Type.REQUEST);
        escrowRefund.setCreatedAt(LocalDateTime.now());
        escrowRefundRepository.save(escrowRefund);

        return CommonResponse.ok();
    }

    @PostMapping("/escrows")
    public CommonResponse insert(Integer chargeAmount, Integer vatAmount, Integer paymentAmount, String mid, PaymentMethod paymentMethod) {
        EscrowLog escrowLog = new EscrowLog();
        escrowLog.setAmountWhVat(paymentAmount);
        escrowLog.setAmount(chargeAmount);
        escrowLog.setType(EscrowLog.Type.DEPOSIT);
        escrowLog.setDepositUser(userRepository.getOne(getSessionUserId()));
        escrowLog.setMid(mid);
        escrowLog.setPaymentMethod(paymentMethod);
        escrowLog.setDepositAt(LocalDateTime.now());

        escrowLogRepository.save(escrowLog);

        return CommonResponse.ok();
    }
}
