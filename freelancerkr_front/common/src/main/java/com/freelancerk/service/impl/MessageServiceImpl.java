package com.freelancerk.service.impl;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.Message;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.AligoKakaoMessageTemplateRepository;
import com.freelancerk.domain.repository.MessageRepository;
import com.freelancerk.gateway.KakaoMessageService;
import com.freelancerk.service.MessageService;
import com.freelancerk.service.SmsService;
import com.freelancerk.util.FrkMessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private SmsService smsService;
    private MessageRepository messageRepository;
    private KakaoMessageService kakaoMessageService;
    private AligoKakaoMessageTemplateRepository aligoKakaoMessageTemplateRepository;

    @Autowired
    public MessageServiceImpl(SmsService smsService, MessageRepository messageRepository, KakaoMessageService kakaoMessageService,
                              AligoKakaoMessageTemplateRepository aligoKakaoMessageTemplateRepository) {
        this.smsService = smsService;
        this.messageRepository = messageRepository;
        this.kakaoMessageService = kakaoMessageService;
        this.aligoKakaoMessageTemplateRepository = aligoKakaoMessageTemplateRepository;
    }

    @Transactional
    @Override
    public void sendMessage(User user, AligoKakaoMessageTemplate.Code code,
                            Map<String, Object> variables) {
        if (Arrays.asList(AligoKakaoMessageTemplate.Code.TA_3172, AligoKakaoMessageTemplate.Code.TA_3173).contains(code)) {
            sendKakaoMessage(user, code, variables);
        } else if (StringUtils.isNotEmpty(user.getCellphone()) && user.isCellphoneCertified()) {
            sendKakaoMessage(user, code, variables);
        }
    }

    @Override
    public void sendMessage(User user, String cellphone, AligoKakaoMessageTemplate.Code code, Map<String, Object> variables) {
        AligoKakaoMessageTemplate template = aligoKakaoMessageTemplateRepository.findByCode(code);
        String content = FrkMessageBuilder.buildMessage(code, template.getMessage(), variables);
        Message message = new Message();
        message.setUser(user);
        message.setContent(content);
        messageRepository.save(message);
        try {
            if (cellphone.startsWith("82")) {
                cellphone = "0" + cellphone.substring(2);
            }
            kakaoMessageService.sendMessage(code.name(), cellphone, template.getTitle(),
                    content);
        } catch (Exception e) {
            log.error("<<< 카카오 알림 톡 전송 실패.", e);
        }
    }

    private void sendKakaoMessage(User user, AligoKakaoMessageTemplate.Code code, Map<String, Object> variables) {
        AligoKakaoMessageTemplate template = aligoKakaoMessageTemplateRepository.findByCode(code);
        String content = FrkMessageBuilder.buildMessage(code, template.getMessage(), variables);
        Message message = new Message();
        message.setUser(user);
        message.setContent(content);
        messageRepository.save(message);
        if (StringUtils.isEmpty(user.getCellphone())) {
            return;
        }

        if (user.getCellphone().startsWith("0") || user.getCellphone().startsWith("82")) {
            try {
                String cellphone = user.getCellphone();
                if (cellphone.startsWith("82") && cellphone.length() > 2) {
                    cellphone = String.format("0%s", cellphone.substring(2));
                }
                kakaoMessageService.sendMessage(code.name(), cellphone, template.getTitle(),
                        content);
            } catch (Exception e) {
                log.error("<<< 카카오 알림 톡 전송 실패.", e);
            }
        } else {
//            smsService.sendMessageAndReturnId(content, user.getCellphone());
        }

    }
}
