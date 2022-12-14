package com.freelancerk.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.PaymentToUser;
import com.freelancerk.domain.PaymentToUserModificationHistory;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.PaymentToUserModificationHistoryRepository;
import com.freelancerk.domain.repository.PaymentToUserRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.io.ResponseCode;
import com.freelancerk.service.FrkEmailService;
import com.freelancerk.service.MessageService;

@RestController
public class PaymentToUserController extends RootController {

    private MessageService messageService;
    private FrkEmailService frkEmailService;
    private ProjectRepository projectRepository;
    private PaymentToUserRepository paymentToUserRepository;
    private PaymentToUserModificationHistoryRepository paymentToUserModificationHistoryRepository;

    @Autowired
    public PaymentToUserController(MessageService messageService, FrkEmailService frkEmailService,
                                   PaymentToUserRepository paymentToUserRepository, ProjectRepository projectRepository,
                                   PaymentToUserModificationHistoryRepository paymentToUserModificationHistoryRepository) {
        this.messageService = messageService;
        this.frkEmailService = frkEmailService;
        this.projectRepository = projectRepository;
        this.paymentToUserRepository = paymentToUserRepository;
        this.paymentToUserModificationHistoryRepository = paymentToUserModificationHistoryRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/payment-to-users")
    public CommonResponse<PaymentToUser> getPaymentToUser(@RequestParam(value = "projectId", required = false) Long projectId,
                                                          @RequestParam(value = "contestEntryId", required = false) Long contestEntryId,
                                                          @RequestParam("type") PaymentToUser.Type type) {
        PaymentToUser paymentToUser = null;
        if (PaymentToUser.Type.PROJECT.equals(type)) {
            List<PaymentToUser> paymentToUserList = paymentToUserRepository.findByProjectIdAndStatus(projectId, PaymentToUser.Status.PAYED);
            if (paymentToUserList.isEmpty()) {
                return new CommonResponse.Builder<PaymentToUser>().responseCode(ResponseCode.FAIL).message("??????????????????????????? ????????????????????????. ?????????????????? ?????? ??? 1??? ??? ????????? ???????????????.").build();
            }
            paymentToUser = paymentToUserList.get(0);

            int totalAmount = 0;
            for (PaymentToUser item: paymentToUserList) {
                totalAmount += item.getAmount();
            }
            paymentToUser.setAmount(totalAmount);

        } else if (PaymentToUser.Type.CONTEST_REWARD.equals(type)) {
            paymentToUser = paymentToUserRepository.findTop1ByContestEntryRewardProjectBidIdOrderByCreatedAtDesc(contestEntryId);
        }

        return new CommonResponse.Builder<PaymentToUser>().data(paymentToUser).build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/payment-to-users/{id}")
    public CommonResponse<PaymentToUser> getPaymentToUserById(@PathVariable("id") Long id) {
        return new CommonResponse.Builder<PaymentToUser>().data(paymentToUserRepository.getOne(id)).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/payment-to-users/{id}")
    public CommonResponse<Void> updatePaymentToUser(@PathVariable("id") Long id, @RequestParam("amount") int amount, @RequestParam("description") String description) {
        PaymentToUser paymentToUser = paymentToUserRepository.getOne(id);
        if (!PaymentToUser.Status.REQUESTED.equals(paymentToUser.getStatus())) {
            return new CommonResponse.Builder<Void>().responseCode(ResponseCode.FAIL).message("????????? ??? ?????? ?????? ?????????.").build();
        }

        long priorAmount = paymentToUser.getAmount();
        String priorDescription = paymentToUser.getDescription();

        paymentToUser.setAmount(amount);
        paymentToUser.setDescription(description);
        paymentToUserRepository.save(paymentToUser);

        PaymentToUserModificationHistory history = new PaymentToUserModificationHistory();
        history.setPaymentToUser(paymentToUser);
        history.setDescription(String.format("???????????? %d -> %d, ?????? ?????? %s -> %s", priorAmount, amount, priorDescription, description));
        paymentToUserModificationHistoryRepository.save(history);

        Map<String, Object> messageVariablesFor = new HashMap<>();
        messageVariablesFor.put("freelancerName", paymentToUser.getUser().getExposeName());
        messageService.sendMessage(paymentToUser.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TB_3001, messageVariablesFor);

        return CommonResponse.ok();
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/payment-to-users/status/requested")
    public CommonResponse makePaymentToUserRequest(@RequestParam(value = "projectId", required = false) Long projectId,
                                                   @RequestParam("amount") int amount, @RequestParam("description") String description,
                                                   HttpServletResponse response) throws IOException {
        Project project = projectRepository.getOne(projectId);

        if (!getSessionUserId().equals(project.getContractedFreelancer().getId())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        PaymentToUser payment = new PaymentToUser();
        payment.setUser(project.getContractedFreelancer());
        payment.setFreelancerName(payment.getUser().getName());
        payment.setBankType(payment.getUser().getBankForReceivingPayment());
        payment.setBankAccountName(payment.getUser().getBankAccountName());
        payment.setBankAccountForReceivingPayment(payment.getUser().getBankAccountForReceivingPayment());
        payment.setTaxType(payment.getUser().getTaxType());
        payment.setAmount(amount);
        payment.setType(PaymentToUser.Type.PROJECT);
        payment.setDescription(description);
        payment.setStatus(PaymentToUser.Status.REQUESTED);
        payment.setProject(project);
        payment = paymentToUserRepository.save(payment);

        User client = project.getPostingClient();
        if (StringUtils.isNotEmpty(client.getEmail()) && client.isReceiveEmail()) {
            frkEmailService.sendPaymentRequestAlarmToClient(payment.getProject(), payment.getUser(), client.getEmail());
        }

        Map<String, Object> messageVariablesFor = new HashMap<>();
        messageVariablesFor.put("freelancerName", payment.getUser().getExposeName());
        messageService.sendMessage(client, AligoKakaoMessageTemplate.Code.TB_3000, messageVariablesFor);

        return CommonResponse.ok();
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/payment-to-users/{paymentToUserId}/status/accepted")
    public CommonResponse makePaymentToUserAccept(
            @PathVariable("paymentToUserId") long paymentToUserId,
            HttpServletResponse response) throws IOException {
        PaymentToUser payment = paymentToUserRepository.getOne(paymentToUserId);

        if (!getSessionUserId().equals(payment.getProject().getPostingClient().getId())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        payment.setAcceptedAt(LocalDateTime.now());
        payment.setStatus(PaymentToUser.Status.ACCEPTED);
        payment = paymentToUserRepository.save(payment);

        User freelancer = payment.getUser();
        if (StringUtils.isNotEmpty(freelancer.getEmail()) && freelancer.isReceiveEmail()) {
            frkEmailService.sendPaymentAcceptedAlarmToFreelancer(payment.getProject(), freelancer, freelancer.getEmail());
        }

        Map<String, Object> messageVariablesFor = new HashMap<>();
        messageVariablesFor.put("freelancerName", payment.getUser().getExposeName());
        messageService.sendMessage(payment.getUser(), AligoKakaoMessageTemplate.Code.TA_3193, messageVariablesFor);

        return CommonResponse.ok();
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/payment-to-users/{paymentToUserId}/status/denied")
    public CommonResponse makePaymentToUserDeny(
            @PathVariable("paymentToUserId") long paymentToUserId,
            @RequestParam(value = "denyReason", required = false) String denyReason,
            HttpServletResponse response) throws IOException {
        PaymentToUser payment = paymentToUserRepository.getOne(paymentToUserId);

        if (!getSessionUserId().equals(payment.getProject().getPostingClient().getId())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        payment.setStatus(PaymentToUser.Status.DENIED);
        payment.setDenyReason(denyReason);
        payment.setDeniedAt(LocalDateTime.now());
        payment = paymentToUserRepository.save(payment);

        Map<String, Object> messageVariablesFor = new HashMap<>();
        messageVariablesFor.put("clientName", payment.getProject().getPostingClient().getExposeName());
        messageService.sendMessage(payment.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3192, messageVariablesFor);

        return CommonResponse.ok();
    }
}
