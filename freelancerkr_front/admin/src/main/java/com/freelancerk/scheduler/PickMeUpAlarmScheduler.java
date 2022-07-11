package com.freelancerk.scheduler;

import com.freelancerk.TimeUtil;
import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.FreelancerProductItemType;
import com.freelancerk.domain.PickMeUpTicket;
import com.freelancerk.domain.repository.PickMeUpTicketRepository;
import com.freelancerk.domain.specification.PickMeUpTicketSpecifications;
import com.freelancerk.service.FrkEmailService;
import com.freelancerk.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PickMeUpAlarmScheduler {

    private MessageService messageService;
    private FrkEmailService frkEmailService;
    private PickMeUpTicketRepository pickMeUpTicketRepository;

    @Autowired
    public PickMeUpAlarmScheduler(MessageService messageService, FrkEmailService frkEmailService,
                                  PickMeUpTicketRepository pickMeUpTicketRepository) {
        this.messageService = messageService;
        this.frkEmailService = frkEmailService;
        this.pickMeUpTicketRepository = pickMeUpTicketRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 12 * * ?")
    public void giveAlarmTicketExpiration() {
        List<PickMeUpTicket> pickMeUpTickets = pickMeUpTicketRepository.findAll(PickMeUpTicketSpecifications.filterForActiveTicket(null));

        for (PickMeUpTicket pickMeUpTicket: pickMeUpTickets) {
            if (pickMeUpTicket.getEndAt() != null && LocalDateTime.now().plusDays(2).isAfter(pickMeUpTicket.getEndAt()) && !pickMeUpTicket.isSendExpirationInTwoDaysNoti()) {

                if (!pickMeUpTicket.getFreelancerProductItemType().getCode().equals(FreelancerProductItemType.Code.PICK_ME_UP)) {

                    if (pickMeUpTicket.getUser().isReceiveEmail() && StringUtils.isNotEmpty(pickMeUpTicket.getUser().getEmail())
                            && !pickMeUpTicket.getUser().isLeaved()) {
                        frkEmailService.sendPickMeUpOptionEndInTwoDaysToFreelancer(
                                pickMeUpTicket.getUser(),
                                TimeUtil.convertLocalDateTimeToStr(pickMeUpTicket.getEndAt()),
                                pickMeUpTicket.getPickMeUp(),
                                pickMeUpTicket.getFreelancerProductItemType().getName(),
                                pickMeUpTicket.getUser().getEmail());
                    }

                    if (StringUtils.isNotEmpty(pickMeUpTicket.getUser().getCellphone()) && !pickMeUpTicket.getUser().isLeaved()) {
                        Map<String, Object> messageVariablesFor = new HashMap<>();
                        messageVariablesFor.put("endAt", TimeUtil.convertLocalDateTimeToStr(pickMeUpTicket.getEndAt()));
                        messageVariablesFor.put("freelancerName", pickMeUpTicket.getUser().getExposeName());
                        messageVariablesFor.put("pickMeUpName", pickMeUpTicket.getPickMeUp().getTitle());
                        messageVariablesFor.put("optionName", pickMeUpTicket.getFreelancerProductItemType().getName());
                        messageService.sendMessage(pickMeUpTicket.getUser(), AligoKakaoMessageTemplate.Code.TA_3208, messageVariablesFor);
                    }

                    pickMeUpTicket.setSendExpirationInTwoDaysNoti(true);
                    pickMeUpTicketRepository.save(pickMeUpTicket);
                } else {

                    if (StringUtils.isNotEmpty(pickMeUpTicket.getUser().getCellphone())) {
                        Map<String, Object> messageVariablesFor = new HashMap<>();
                        messageVariablesFor.put("endAt", TimeUtil.convertLocalDateTimeToStr(pickMeUpTicket.getEndAt()));
                        messageVariablesFor.put("freelancerName", pickMeUpTicket.getUser().getExposeName());
                        messageVariablesFor.put("pickMeUpName", pickMeUpTicket.getPickMeUp().getTitle());
                        messageService.sendMessage(pickMeUpTicket.getUser(), AligoKakaoMessageTemplate.Code.TA_3207, messageVariablesFor);
                    }

                    if (pickMeUpTicket.getUser().isReceiveEmail() && StringUtils.isNotEmpty(pickMeUpTicket.getUser().getEmail()) && !pickMeUpTicket.getUser().isLeaved()) {
                        frkEmailService.sendPickMeUpExposeEndInTwoDaysToFreelancer(
                                pickMeUpTicket.getUser(),
                                TimeUtil.convertLocalDateTimeToStr(pickMeUpTicket.getEndAt()),
                                pickMeUpTicket.getPickMeUp(),
                                pickMeUpTicket.getUser().getEmail());
                    }

                    pickMeUpTicket.setSendExpirationInTwoDaysNoti(true);
                    pickMeUpTicketRepository.save(pickMeUpTicket);
                }
            }
        }

        List<PickMeUpTicket> expiredInADayExposeTicket = pickMeUpTicketRepository.findByFreelancerProductItemTypeCodeAndEndAtLessThanAndEndAtGreaterThan(
                FreelancerProductItemType.Code.PICK_ME_UP, LocalDateTime.now(), LocalDateTime.now().minusDays(1));

        for (PickMeUpTicket ticket: expiredInADayExposeTicket) {
            if (ticket.getUser().isLeaved() || ticket.isSendExpirationNoti()) continue;

            if (StringUtils.isNotEmpty(ticket.getUser().getCellphone())) {
                Map<String, Object> messageVariablesFor = new HashMap<>();
                messageVariablesFor.put("freelancerName", ticket.getUser().getExposeName());
                messageVariablesFor.put("pickMeUpName", ticket.getPickMeUp().getTitle());
                messageService.sendMessage(ticket.getUser(), AligoKakaoMessageTemplate.Code.TA_3206, messageVariablesFor);
            }
            
            if (ticket.getUser().isReceiveEmail() && StringUtils.isNotEmpty(ticket.getUser().getEmail()) && !ticket.getUser().isLeaved()) {
                frkEmailService.sendPickMeUpExposeEndToFreelancer(
                        TimeUtil.convertLocalDateTimeToStr(ticket.getStartAt()),
                        ticket.getUser(),
                        ticket.getPickMeUp(),
                        ticket.getUser().getEmail());
            }
            ticket.setSendExpirationNoti(true);
            pickMeUpTicketRepository.save(ticket);
        }
    }
}
