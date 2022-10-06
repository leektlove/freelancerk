package com.freelancerk.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freelancerk.domain.Inquiry;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.InquiryAnswerRepository;
import com.freelancerk.domain.repository.InquiryRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonListResponse;
import com.freelancerk.io.CommonResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "문의사항", description = "등록/조회 등")
@RestController
public class InquiryController extends RootController {

    private UserRepository userRepository;
    private InquiryRepository inquiryRepository;
    private InquiryAnswerRepository inquiryAnswerRepository;

    @Autowired
    public InquiryController(UserRepository userRepository, InquiryRepository inquiryRepository,
                             InquiryAnswerRepository inquiryAnswerRepository) {
        this.userRepository = userRepository;
        this.inquiryRepository = inquiryRepository;
        this.inquiryAnswerRepository = inquiryAnswerRepository;
    }

    @ApiOperation("문의사항 목록 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/inquiries")
    public CommonListResponse<List<Inquiry>> getNoticeList(@RequestParam(value = "keyword", required = false) String keyword,
                                                        @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                        @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();

        Page<Inquiry> noticePage = null;
        if (StringUtils.isEmpty(keyword)) {
            noticePage = inquiryRepository.findByUserId(userId, PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        } else {
            noticePage = inquiryRepository.findByUserIdAndContentContaining(
                    userId,
                    keyword,
                    PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        }
        return new CommonListResponse.Builder<List<Inquiry>>()
                .totalCount(noticePage.getTotalElements())
                .currentPage(pageNumber)
                .totalPages(noticePage.getTotalPages())
                .data(noticePage.getContent())
                .build();
    }

    @ApiOperation("문의사항 상세 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/inquiries/{inquiryId}")
    public CommonResponse<Inquiry> getNotices(@PathVariable(value = "inquiryId", required = false) Long inquiryId) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        return new CommonResponse.Builder<Inquiry>().data(inquiryRepository.getOne(inquiryId)).build();
    }

    @ApiOperation("문의사항 등록")
    @RequestMapping(method = RequestMethod.POST, value = "/inquiries")
    public CommonResponse insertInquiry(@RequestParam("title") String title, @RequestParam("content") String content) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        Inquiry inquiry = new Inquiry();
        inquiry.setContent(content);
        inquiry.setCreatedAt(LocalDateTime.now());
        inquiry.setTitle(title);
        inquiry.setUser(userRepository.getOne(userId));
        inquiry.setUserRole(isLoggedIsAsClient()?User.Role.ROLE_CLIENT:User.Role.ROLE_FREELANCER);
        inquiryRepository.save(inquiry);

        return CommonResponse.ok();
    }
}
