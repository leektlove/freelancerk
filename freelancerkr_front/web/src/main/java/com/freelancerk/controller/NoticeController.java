package com.freelancerk.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freelancerk.domain.Notice;
import com.freelancerk.domain.repository.NoticeRepository;
import com.freelancerk.io.CommonListResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "공지사항/자료실", description = "등록/조회 등")
@RestController
public class NoticeController {

    private NoticeRepository noticeRepository;

    @Autowired
    public NoticeController(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @ApiOperation("공지사항 목록 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/notices")
    public CommonListResponse<List<Notice>> getNotices(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                       @RequestParam(value = "pageSize", defaultValue = "15", required = false) int pageSize) {
        Page<Notice> noticePage = noticeRepository.findAll(PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        return new CommonListResponse.Builder<List<Notice>>()
                .totalCount(noticePage.getTotalElements())
                .currentPage(pageNumber)
                .totalPages(noticePage.getTotalPages())
                .data(noticePage.getContent())
                .build();
    }
//
//    @ApiOperation("공지사항 상세 조회")
//    @RequestMapping(method = RequestMethod.GET, value = "/notices/{noticeId}")
//    public CommonResponse<Notice> getNotices(@PathVariable(value = "noticeId", required = false) Long noticeId) {
//        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
//        return new CommonResponse.Builder<Notice>().data(noticeRepository.getOne(noticeId)).build();
//    }
//
//    @ApiOperation("자료실 목록 조회")
//    @RequestMapping(method = RequestMethod.GET, value = "/references")
//    public CommonListResponse<List<Reference>> getReferences(@RequestParam(value = "keyword", required = false) String keyword,
//                                                             @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
//                                                             @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
//        Page<Reference> referencePage = null;
//        if (StringUtils.isEmpty(keyword)) {
//            referencePage = referenceRepository.findAll(PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
//        } else {
//            referencePage = referenceRepository.findByTitleContainingOrContentContaining(
//                    keyword,
//                    PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
//        }
//        return new CommonListResponse.Builder<List<Reference>>()
//                .totalCount(referencePage.getTotalElements())
//                .currentPage(pageNumber)
//                .totalPages(referencePage.getTotalPages())
//                .data(referencePage.getContent())
//                .build();
//    }
//
//    @ApiOperation("자료실 상세 조회")
//    @RequestMapping(method = RequestMethod.GET, value = "/references/{referenceId}")
//    public CommonResponse<Reference> getReferences(@PathVariable(value = "referenceId", required = false) Long referenceId) {
//        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
//        return new CommonResponse.Builder<Reference>().data(referenceRepository.getOne(referenceId)).build();
//    }
}
