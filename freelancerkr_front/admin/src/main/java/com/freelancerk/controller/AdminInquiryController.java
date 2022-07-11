package com.freelancerk.controller;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.Inquiry;
import com.freelancerk.domain.InquiryAnswer;
import com.freelancerk.domain.repository.AdminUserRepository;
import com.freelancerk.domain.repository.InquiryAnswerRepository;
import com.freelancerk.domain.repository.InquiryRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Api(tags = "문의사항", description = "등록/조회 등")
@RestController
public class AdminInquiryController {

    private UserRepository userRepository;
    private InquiryRepository inquiryRepository;
    private AdminUserRepository adminUserRepository;
    private InquiryAnswerRepository inquiryAnswerRepository;
    private MessageService messageService;

    @Autowired
    public AdminInquiryController(UserRepository userRepository, MessageService messageService, InquiryRepository inquiryRepository,
                                  AdminUserRepository adminUserRepository, InquiryAnswerRepository inquiryAnswerRepository) {
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.inquiryRepository = inquiryRepository;
        this.adminUserRepository = adminUserRepository;
        this.inquiryAnswerRepository = inquiryAnswerRepository;
    }

    @ApiOperation("문의사항 답변 등록")
    @RequestMapping(method = RequestMethod.POST, value = "/inquiries/{inquiryId}/answers")
    public CommonResponse insertInquiry(@PathVariable("inquiryId") Long inquiryId, @RequestParam("content") String content,
                                        @RequestParam("status") Inquiry.Status status,
                                        @RequestParam(value = "sendKakaoAlarmTalk", required = false) boolean sendKakaoAlarmTalk) {
        Inquiry inquiry = inquiryRepository.getOne(inquiryId);
        inquiry.setStatus(status);
        inquiry = inquiryRepository.save(inquiry);

        if (StringUtils.isNotEmpty(content)) {
            InquiryAnswer inquiryAnswer = new InquiryAnswer();
            inquiryAnswer.setContent(content);
            inquiryAnswer.setCreatedAt(LocalDateTime.now());
            inquiryAnswer.setInquiry(inquiry);
            inquiryAnswerRepository.save(inquiryAnswer);
        }

        return CommonResponse.ok();
    }

    @Transactional
    @ApiOperation("문의사항 알림 발송")
    @RequestMapping(method = RequestMethod.POST, value = "/inquiries/{inquiryId}/alarms")
    public CommonResponse sendAlarm(@PathVariable("inquiryId") Long inquiryId) {
        Inquiry inquiry = inquiryRepository.getOne(inquiryId);
        inquiry.setAnswerAlarm(true);
        messageService.sendMessage(inquiry.getUser(), AligoKakaoMessageTemplate.Code.TB_0735, null);
        return CommonResponse.ok();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/inquiry-answers/{id}")
    public CommonResponse<Void> deleteAnswer(@PathVariable("id") Long answerId) {
        inquiryAnswerRepository.deleteById(answerId);

        return CommonResponse.ok();
    }
}
