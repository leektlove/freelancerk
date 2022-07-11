package com.freelancerk.controller;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.EscrowLog;
import com.freelancerk.domain.PaymentToUser;
import com.freelancerk.domain.repository.EscrowLogRepository;
import com.freelancerk.domain.repository.PaymentToUserRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class PaymentToUserController {

    private UserRepository userRepository;
    private MessageService messageService;
    private EscrowLogRepository escrowLogRepository;
    private PaymentToUserRepository paymentToUserRepository;

    @Autowired
    public PaymentToUserController(UserRepository userRepository, MessageService messageService,
                                   EscrowLogRepository escrowLogRepository, PaymentToUserRepository paymentToUserRepository) {
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.escrowLogRepository = escrowLogRepository;
        this.paymentToUserRepository = paymentToUserRepository;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/payment-to-users/{id}")
    public void updatePaymentToUser(@PathVariable("id") long id,
                                              @RequestParam(value = "amount", required = false) Integer amount,
                                              @RequestParam(value = "description", required = false) String description,
                                              @RequestParam(value = "acceptDescription", required = false) String acceptDescription,
                                              @RequestParam(value = "paymentDescription", required = false) String paymentDescription,
                                              @RequestParam(value = "denyReason", required = false) String denyReason,
                                              HttpServletResponse response) throws IOException {
        PaymentToUser paymentToUser = paymentToUserRepository.getOne(id);
        long accumulatedAmount = paymentToUserRepository.findByUserIdAndStatus(paymentToUser.getUser().getId(), PaymentToUser.Status.PAYED)
                .stream().map(PaymentToUser::getAmount).mapToLong(Long::longValue).sum();
        paymentToUser.setAccumulatedIncomeAmount(accumulatedAmount);
        if (amount != null) {
            paymentToUser.setAmount(amount);
        }
        if (StringUtils.isNotEmpty(description)) {
            paymentToUser.setDescription(description);
        }
        if (StringUtils.isNotEmpty(acceptDescription)) {
            paymentToUser.setAcceptDescription(acceptDescription);
        }
        if (StringUtils.isNotEmpty(paymentDescription)) {
            paymentToUser.setPaymentDescription(paymentDescription);
        }
        if (StringUtils.isNotEmpty(denyReason)) {
            paymentToUser.setDenyReason(denyReason);
        }
        if (PaymentToUser.Status.ACCEPTED.equals(paymentToUser.getStatus())) {
            paymentToUser.setStatus(PaymentToUser.Status.PAYED);
            paymentToUser.setPayedAt(LocalDateTime.now());

            EscrowLog escrowLog = new EscrowLog();
            escrowLog.setType(EscrowLog.Type.WITHDRAWAL);
            escrowLog.setWithdrawalUser(paymentToUser.getProject().getPostingClient());
            escrowLog.setProject(paymentToUser.getProject());
            escrowLog.setAmount((int) paymentToUser.getAmount());
            escrowLog.setAmountWhVat((int) (paymentToUser.getAmount() + paymentToUser.getAmount() * 0.1));
            escrowLogRepository.save(escrowLog);

            messageService.sendMessage(paymentToUser.getUser(), AligoKakaoMessageTemplate.Code.TB_1312, null);
            messageService.sendMessage(paymentToUser.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TB_1311, null);
        }
        paymentToUserRepository.save(paymentToUser);

        response.sendRedirect("http://admin.freelancerk.com/view/payment-to-users/" + id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/payment-to-users/{id}")
    public CommonResponse<PaymentToUser> getPaymentToUserById(@PathVariable("id") Long id) {

        return new CommonResponse.Builder<PaymentToUser>().data(paymentToUserRepository.getOne(id)).build();
    }
}
