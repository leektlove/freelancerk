package com.freelancerk.controller;

import com.freelancerk.TimeUtil;
import com.freelancerk.domain.Message;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.MessageRepository;
import com.freelancerk.domain.specification.MessageSpecifications;
import com.freelancerk.io.CommonListResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "메세지", description = "조회/검색 등")
@RestController
public class MessageController extends RootController {

    @Autowired
    private MessageRepository messageRepository;

    @ApiOperation("메세지 조회/검색")
    @RequestMapping(method = RequestMethod.GET, value = "/users/messages")
    public CommonListResponse<List<Message>> getMessages(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "dateFrom", required = false) String dateFrom,
            @RequestParam(value = "dateTo", required = false) String dateTo,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        Page<Message> page = null;
        if (StringUtils.isEmpty(keyword)) {
            page = messageRepository.findByUserId(userId, PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

        } else if (StringUtils.isAnyEmpty(dateFrom, dateTo)) {
            Specifications<Message> spec = Specifications.where(null);
            spec = spec.and(MessageSpecifications.textInAllColumns(keyword, userId));
            page = messageRepository.findAll(spec, PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        } else {
            page = messageRepository.findByContentContainingAndUserIdAndCreatedAtGreaterThanAndCreatedAtLessThan(
                    keyword, userId,
                    TimeUtil.convertStrToLocalDateTime(dateFrom),
                    TimeUtil.convertStrToLocalDateTime(dateTo),
                    PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        }
        return new CommonListResponse.Builder<List<Message>>()
                .totalCount(page.getTotalElements())
                .currentPage(pageNumber)
                .totalPages(page.getTotalPages())
                .data(page.getContent())
                .build();
    }
}
