package com.freelancerk.controller;

import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonListResponse;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.PointService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "클라이언트", description = "리스트/등록 등")
@RestController
public class AdminClientController {

    private PointService pointService;
    private UserRepository userRepository;

    @Autowired
    public AdminClientController(PointService pointService, UserRepository userRepository) {
        this.pointService = pointService;
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/clients")
    public CommonListResponse<List<User>> getClientUsers(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                         @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        Page<User> page = userRepository.findByRolesContaining(User.Role.ROLE_CLIENT.name(), PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        return new CommonListResponse.Builder<List<User>>()
                .totalCount(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(pageNumber)
                .data(page.getContent()).build();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/users/client/points")
    public CommonResponse<Void> givePoints(@RequestParam("id") Long[] ids, @RequestParam("amount") int amount) {
        for (long id: ids) {
            pointService.givePointsToClient(id, amount, "관리자 포인트 일괄 지급", null);
        }

        return CommonResponse.ok();
    }
}
