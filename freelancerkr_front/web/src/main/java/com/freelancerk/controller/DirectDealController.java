package com.freelancerk.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.DirectDeal;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.PickMeUpMemo;
import com.freelancerk.domain.repository.DirectDealRepository;
import com.freelancerk.domain.repository.PickMeUpMemoRepository;
import com.freelancerk.domain.repository.PickMeUpRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonListResponse;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.MessageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "직거래", description = "담기/조회 등")
@RestController
public class DirectDealController extends RootController {

	private MessageService messageService;
	private UserRepository userRepository;
	private PickMeUpRepository pickMeUpRepository;
	private PickMeUpMemoRepository pickMeUpMemoRepository;
	private DirectDealRepository directDealRepository;

	@Autowired
	public DirectDealController(MessageService messageService, UserRepository userRepository, PickMeUpRepository pickMeUpRepository,
			PickMeUpMemoRepository pickMeUpMemoRepository, DirectDealRepository directDealRepository) {

		this.messageService = messageService;
		this.userRepository = userRepository;
		this.pickMeUpRepository = pickMeUpRepository;
		this.pickMeUpMemoRepository = pickMeUpMemoRepository;
		this.directDealRepository = directDealRepository;
	}

	@ApiOperation("직거래 ok 등록")
	@RequestMapping(method = RequestMethod.POST, value = "/pick-me-ups/{id}/direct-deals")
	public CommonResponse insertDirectDeal(@PathVariable("id") Long pickMeUpId, HttpServletResponse response) {
		int count = directDealRepository.countByPickMeUpIdAndUserId(pickMeUpId, getSessionUserId());
		if (count > 0) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			return CommonResponse.ok();
		}
		DirectDeal directDeal = new DirectDeal();
		directDeal.setCreatedAt(LocalDateTime.now());
		directDeal.setPickMeUp(pickMeUpRepository.getOne(pickMeUpId));
		directDeal.setUser(userRepository.getOne(getSessionUserId()));
		directDealRepository.save(directDeal);

		Map<String, Object> messageVariables = new HashMap<>();
		messageVariables.put("clientName", directDeal.getUser().getExposeName());
		messageVariables.put("freelancerName", directDeal.getPickMeUp().getUser().getExposeName());
		messageService.sendMessage(directDeal.getPickMeUp().getUser(), AligoKakaoMessageTemplate.Code.TA_3199, messageVariables);

		return CommonResponse.ok();
	}

	@ApiOperation("직거래 ok 삭제")
	@RequestMapping(method = RequestMethod.DELETE, value = "/direct-deals")
	public CommonResponse deleteDirectDeals(@RequestParam("id[]") Long[] ids) {
		for (Long id : ids) {
			directDealRepository.deleteById(id);
		}
		return CommonResponse.ok();
	}

	@ApiOperation("내가 둘러본 직거래 조회")
	@RequestMapping(method = RequestMethod.GET, value = "/users/me/direct-deals")
	public CommonListResponse<List<DirectDeal>> getDirectDeals(
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "8", required = false) int pageSize) {
		Page<DirectDeal> directDeals = directDealRepository.findByUserId(getSessionUserId(),
				PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

		return new CommonListResponse.Builder<List<DirectDeal>>().currentPage(pageNumber)
				.totalCount(directDeals.getTotalElements()).data(directDeals.getContent()).build();
	}

	@ApiOperation("직거래 메모 등록")
	@RequestMapping(method = RequestMethod.POST, value = "/direct-deals/{id}/memo")
	public CommonResponse insertMemo(@PathVariable("id") Long directDealId, @RequestParam("content") String content) {
		PickMeUp pickMeUp = directDealRepository.getOne(directDealId).getPickMeUp();
		PickMeUpMemo pickMeUpMemo = pickMeUpMemoRepository.findByUserIdAndPickMeUpId(getSessionUserId(), pickMeUp.getId());
		if (pickMeUpMemo == null) {
			pickMeUpMemo = new PickMeUpMemo();
		}
		pickMeUpMemo.setCreatedAt(LocalDateTime.now());
		pickMeUpMemo.setPickMeUp(pickMeUp);
		pickMeUpMemo.setUser(userRepository.getOne(getSessionUserId()));
		pickMeUpMemo.setContent(content);
		pickMeUpMemoRepository.save(pickMeUpMemo);

		return CommonResponse.ok();
	}
}
