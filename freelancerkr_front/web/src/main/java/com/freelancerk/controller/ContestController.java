package com.freelancerk.controller;

import com.freelancerk.TimeUtil;
import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.io.CommonListResponse;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.service.FrkEmailService;
import com.freelancerk.service.MessageService;
import com.freelancerk.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = "컨테스트", description = "등록/수정/조회/환불 등")
@RestController
public class ContestController {

	private MessageService messageService;
	private ProjectService projectService;
	private FrkEmailService frkEmailService;
	private ProjectRepository projectRepository;
	private ProjectBidRepository projectBidRepository;

	@Autowired
	public ContestController(MessageService messageService, ProjectService projectService, FrkEmailService frkEmailService,
							 ProjectRepository projectRepository, ProjectBidRepository projectBidRepository) {
		this.messageService = messageService;
		this.projectService = projectService;
		this.frkEmailService = frkEmailService;
		this.projectRepository = projectRepository;
		this.projectBidRepository = projectBidRepository;
	}

	@ApiOperation("컨테스트 등록")
	@RequestMapping(method = RequestMethod.POST, value = "/contests")
	public CommonResponse insertContest(
			@RequestParam("contestId") Long contestId, @RequestParam("depositMoney") Integer depositMoney,
			@RequestParam("minPrize") Integer minPrize, @RequestParam("incentivePrize") Integer incentivePrice,
			@RequestParam("prizeFor1st") Integer prizeFor1st, @RequestParam("prizeFor2nd") Integer prizeFor2nd,
			@RequestParam("prizeFor3rd") Integer prizeFor3rd,
			@RequestParam("payForFeeToFreelancer") boolean payForFeeToFreelancer,
			@RequestParam(value = "usedPoint", required = false) Integer usedPoint,
			@RequestParam(value = "contestOption[]", required = false) Integer[] optionIds,
			@RequestParam(value = "contestOptionCount[]", required = false) Integer[] optionCount,
			@RequestParam(value = "contestOptionPack[]", required = false) Integer[] packIds,
			@RequestParam(value = "contestOptionPackCount[]", required = false) Integer[] contestOptionPackCount) {
		Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();

		projectService.insertContest(userId, contestId, depositMoney, minPrize, incentivePrice, prizeFor1st, prizeFor2nd, prizeFor3rd, payForFeeToFreelancer, usedPoint,
				optionIds, optionCount, packIds, contestOptionPackCount);

		return CommonResponse.ok();
	}

	@ApiOperation("컨테스트 임시 저장")
	@RequestMapping(method = RequestMethod.POST, value = "/contest/temp")
	public CommonResponse<Long> insertTempContest(
			@RequestParam(value = "projectId", required = false) Long projectId,
			@RequestParam(value = "contestSectorTypeId[]", required = false) Long[] contestSectorTypeId,
			@RequestParam("title") String title, @RequestParam(value = "categoryOfBusiness", required = false) String categoryOfBusiness,
			@RequestParam(value = "majorProduct", required = false) String majorProduct, @RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "quantityPage", required = false) Integer quantityPage,
			@RequestParam(value = "descriptionPerPage", required = false) String descriptionPerPage,
			@RequestParam(value = "referenceWebUrl", required = false) String[] referenceWebUrl,
			@RequestParam(value = "referenceFile", required = false) MultipartFile referenceFile,
			@RequestParam(value = "workingWebUrl", required = false) String workingWebUrl,
			@RequestParam(value = "corporateNameOrCatchPhrase", required = false) String corporateNameOrCatchPhrase,
			@RequestParam(value = "priorityTone", required = false) String priorityTone, @RequestParam(value = "feeling", required = false) Project.Feeling[] feelings,
			@RequestParam(value = "proceedProjectAfterContest", required = false) boolean proceedProjectAfterContest) {
		Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();

		log.info("<<< projectId: {}, contestSectorTypeId: {}, title: {}, categoryOfBusiness: {}, majorProduct: {}, description: {}, " +
						"quantityPage: {}, descriptionPerPage: {}, feelings: {}, proceedProjectAfterContest: {}", projectId, contestSectorTypeId, title,
				categoryOfBusiness, majorProduct, description, quantityPage, descriptionPerPage, feelings, proceedProjectAfterContest);
		Project project = projectService.insertOrUpdateContestTemporaliy(projectId, userId, title, contestSectorTypeId, categoryOfBusiness, majorProduct,
				description, quantityPage, descriptionPerPage, referenceWebUrl, referenceFile, workingWebUrl,
				corporateNameOrCatchPhrase, priorityTone, feelings, proceedProjectAfterContest);

		return new CommonResponse.Builder<Long>().data(project.getId()).build();
	}

	@ApiOperation("컨테스트 상세 데이터 저장")
	@RequestMapping(method = RequestMethod.POST, value = "/contests/{id}/details")
	public CommonResponse<Integer> updateDetailContest(@PathVariable("id") Long id,
											  @RequestParam("minPrize") Integer minPrize, @RequestParam("incentivePrize") Integer incentivePrize,
											  @RequestParam(value = "prizeFor1st", required = false, defaultValue = "0") Integer prizeFor1st,
											  @RequestParam(value = "prizeFor2nd", required = false, defaultValue = "0") Integer prizeFor2nd,
											  @RequestParam(value = "prizeFor3rd", required = false, defaultValue = "0") Integer prizeFor3rd,
											  @RequestParam("postingStartAt") String startAt, @RequestParam("postingEndAt") String endAt,
											  @RequestParam("payForFeeToFreelancer") boolean payForFeeToFreelancer) {

		Project project = projectService.getById(id);
		project.setIncentive(incentivePrize);
		project.setMinPrize(minPrize);

		if (prizeFor1st == null || prizeFor1st == 0) {
			prizeFor1st = minPrize + incentivePrize;
		}

		project.setPostingStartAt(LocalDateTime.of(TimeUtil.convertStrToLocalDate(startAt), LocalTime.now()));
		project.setPostingEndAt(LocalDateTime.of(TimeUtil.convertStrToLocalDate(endAt), LocalTime.now()));
		project.setPrizeFor1st(prizeFor1st);
		project.setPrizeFor2nd(prizeFor2nd);
		project.setPrizeFor3rd(prizeFor3rd);
		project.setPayForFeeToFreelancer(payForFeeToFreelancer);

		project = projectRepository.save(project);

		return new CommonResponse.Builder<Integer>().data(project.getRemainDays()).build();
	}

	@ApiOperation("컨테스트 수정")
	@RequestMapping(method = RequestMethod.POST, value = "/contests/{contestId}/modifications")
	public CommonResponse modifyContest(@PathVariable("contestId") Long contestId,
			@RequestParam("contestSectorName[]") String contestSectorName, @RequestParam("name") String name,
			@RequestParam("categoryOfBusiness") String categoryOfBusiness,
			@RequestParam("majorProduct") String majorProduct, @RequestParam("description") String description,
			@RequestParam("quantityPage") Integer quantityPage, @RequestParam("depositMoney") Integer depositMoney,
			@RequestParam("descriptionPerPage") String descriptionPerPage,
			@RequestParam(value = "referenceWebUrl", required = false) String[] referenceWebUrl,
			@RequestParam("referenceFile[]") MultipartFile[] referenceFile,
			@RequestParam(value = "workingWebUrl", required = false) String workingWebUrl,
			@RequestParam(value = "corporateNameOrCatchPhrase", required = false) String corporateNameOrCatchPhrase,
			@RequestParam(value = "status", required = false) Project.Status status,
			@RequestParam(value = "priorityTone", required = false) String priorityTone,
			@RequestParam(value = "feeling", required = false) Project.Feeling[] feelings,
			@RequestParam(value = "proceedProjectAfterContest", required = false) Boolean proceedProjectAfterContest,
			@RequestParam("minPrice") Integer minimumPrice, @RequestParam("incentivePrice") Integer incentivePrice,
			@RequestParam("prizeFor1st") Integer prizeFor1st, @RequestParam("prizeFor2nd") Integer prizeFor2nd,
			@RequestParam("prizeFor3rd") Integer prizeFor3rd,
			@RequestParam("payForFeeToFreelancer") Boolean payForFeeToFreelancer) {
		Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();

		projectService.updateContest(userId, contestId, name, contestSectorName, categoryOfBusiness, majorProduct, description,
				quantityPage, descriptionPerPage, referenceWebUrl, referenceFile, workingWebUrl,
				corporateNameOrCatchPhrase, status, depositMoney, priorityTone, feelings, proceedProjectAfterContest,
				minimumPrice, incentivePrice, prizeFor1st, prizeFor2nd, prizeFor3rd, payForFeeToFreelancer);

		return CommonResponse.ok();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/contests/{contestId}/prize/modifications")
	public CommonResponse updatePrize(@PathVariable("contestId") Long contestId,
									  @RequestParam("prizeFor1st") Integer prizeFor1st, @RequestParam("prizeFor2nd") Integer prizeFor2nd,
									  @RequestParam("prizeFor3rd") Integer prizeFor3rd) {
		Project contest = projectRepository.getOne(contestId);
		if ( (prizeFor1st == null || prizeFor1st == 0) && (prizeFor2nd == null || prizeFor2nd == 0) && (prizeFor3rd == null || prizeFor3rd == 0) ) {
			contest.setPrizeFor1st(contest.getTotalPrize());
			contest.setPrizeFor2nd(0);
			contest.setPrizeFor3rd(0);
		} else {
			contest.setPrizeFor1st(prizeFor1st);
			contest.setPrizeFor2nd(prizeFor2nd);
			contest.setPrizeFor3rd(prizeFor3rd);
		}
		contest.setPrizeLastModifiedAt(LocalDateTime.now());
		projectRepository.save(contest);

		return CommonResponse.ok();
	}

	@ApiOperation("컨테스트 조회")
	@RequestMapping(method = RequestMethod.GET, value = "/contests")
	public CommonListResponse<List<Project>> getProjects(@RequestParam("status") Project.Status status,
			@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") Sort.Direction sortDirection,
			@RequestParam(value = "sortProperty", required = false, defaultValue = "successfulBidPrice") String sortProperty,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
		Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
		return new CommonListResponse();
	}

	@ApiOperation("컨테스트 상세 보기")
	@RequestMapping(method = RequestMethod.GET, value = "/contests/{contestId}")
	public CommonResponse<Project> getContestDetail(@PathVariable("contestId") Long contestId) {
        Project contest = projectService.getById(contestId);
		return new CommonResponse.Builder<Project>().data(contest).build();
	}

	@ApiOperation("컨테스트 입찰 조회")
	@RequestMapping(method = RequestMethod.GET, value = "/contests/{contestId}/entries")
	public CommonListResponse<List<ProjectBid>> getContestEntries(@PathVariable("contestId") Long contestId,
                                                                  @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                  @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
		Page<ProjectBid> page = projectBidRepository.findByProjectId(contestId,
				PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
		return new CommonListResponse.Builder<List<ProjectBid>>().currentPage(pageNumber)
				.totalPages(page.getTotalPages()).totalCount(page.getTotalElements()).data(page.getContent()).build();
	}

	@ApiOperation("환불")
	@RequestMapping(method = RequestMethod.POST, value = "/contents/{contestId}/refund")
	public CommonResponse refundContest(@PathVariable("contentId") Long contestId) {
		Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        projectService.refund(contestId);
		return CommonResponse.ok();
	}

	@ApiOperation("컨테스트 작품 선정")
	@RequestMapping(method = RequestMethod.POST, value = "/contests/{contestId}/picks")
	public CommonResponse pickContest(@PathVariable("contestId") Long contestId,
			@RequestParam("contestEntryId[]") Long[] contestEntryIds) {
		Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();

        projectService.pick(contestId, contestEntryIds);

        Project project = projectRepository.getOne(contestId);
        List<ProjectBid> projectBids = projectBidRepository.findAllById(Arrays.asList(contestEntryIds));

        List<User> freelancerUsers = projectBids.stream().map(ProjectBid::getParticipant).collect(Collectors.toList());
        List<String> freelancerExposeNames = projectBids.stream().map(ProjectBid::getParticipant).map(User::getExposeName).collect(Collectors.toList());

		if (StringUtils.isNotEmpty(project.getPostingClient().getEmail()) && project.getPostingClient().isReceiveEmail()) {
			frkEmailService.sendContestBidSuccessToClient(String.join(", ", freelancerExposeNames), project.getPostingClient(), project, project.getPostingClient().getEmail());
		}

		for (User freelancerUser: freelancerUsers) {
			if (StringUtils.isNotEmpty(freelancerUser.getEmail()) && freelancerUser.isReceiveEmail()) {
				frkEmailService.sendContestBidSuccessToFreelancer(freelancerUser, String.join(", ", freelancerExposeNames), project.getPostingClient(), project, freelancerUser.getEmail());
			}

			Map<String, Object> variableMap = new HashMap<>();
			variableMap.put("clientName", project.getPostingClient().getExposeName());
			variableMap.put("projectName", project.getTitle());
			variableMap.put("freelancerName", String.join(", ", freelancerExposeNames));
			messageService.sendMessage(freelancerUser, AligoKakaoMessageTemplate.Code.TA_3181, variableMap);
		}

		return CommonResponse.ok();
	}
}
