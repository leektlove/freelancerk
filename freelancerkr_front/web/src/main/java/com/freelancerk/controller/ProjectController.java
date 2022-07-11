package com.freelancerk.controller;

import com.freelancerk.TimeUtil;
import com.freelancerk.io.CommonListResponse;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.model.SelectedKeywordModel;
import com.freelancerk.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = "프로젝트", description = "포스팅/조회 등")
@RestController
public class ProjectController extends RootController {

	private UserService userService;
	private UserRepository userRepository;
	private ProjectService projectService;
	private StorageService storageService;
	private MessageService messageService;
	private FrkEmailService frkEmailService;
	private CategoryService categoryService;
	private ProjectRepository projectRepository;
	private ProjectBidRepository projectBidRepository;
	private ProjectCommentRepository projectCommentRepository;
	private ProjectFavoriteRepository projectFavoriteRepository;
	private KeywordOrSectorAlarmService keywordOrSectorAlarmService;
	private ProjectItemTicketRepository projectItemTicketRepository;
	private ProjectContractFileRepository projectContractFileRepository;
	private ProjectItemTicketLogRepository projectItemTicketLogRepository;
	private ProjectProductItemTypeRepository projectProductItemTypeRepository;

	@Autowired
	public ProjectController(UserService userService, UserRepository userRepository, ProjectService projectService,
							 MessageService messageService, StorageService storageService, CategoryService categoryService,
							 FrkEmailService frkEmailService, ProjectRepository projectRepository,
							 ProjectBidRepository projectBidRepository, ProjectCommentRepository projectCommentRepository,
							 KeywordOrSectorAlarmService keywordOrSectorAlarmService,
							 ProjectFavoriteRepository projectFavoriteRepository, ProjectItemTicketRepository projectItemTicketRepository,
							 ProjectContractFileRepository projectContractFileRepository, ProjectProductItemTypeRepository projectProductItemTypeRepository,
							 ProjectItemTicketLogRepository projectItemTicketLogRepository) {
		this.userService = userService;
		this.userRepository = userRepository;
		this.projectService = projectService;
		this.storageService = storageService;
		this.messageService = messageService;
		this.frkEmailService = frkEmailService;
		this.categoryService = categoryService;
		this.projectRepository = projectRepository;
		this.projectBidRepository = projectBidRepository;
		this.projectCommentRepository = projectCommentRepository;
		this.projectFavoriteRepository = projectFavoriteRepository;
		this.keywordOrSectorAlarmService = keywordOrSectorAlarmService;
		this.projectItemTicketRepository = projectItemTicketRepository;
		this.projectContractFileRepository = projectContractFileRepository;
		this.projectItemTicketLogRepository = projectItemTicketLogRepository;
		this.projectProductItemTypeRepository = projectProductItemTypeRepository;
	}

	@ApiOperation("프로젝트, 컨테스트 찜하기")
	@RequestMapping(method = RequestMethod.POST, value = "/addWishList")
	public CommonResponse addToWishList(@RequestParam(value = "projectId") Long projectId) {

		Long userId = userService.getCurrentUser().getId();

		ProjectFavorite projectFavorite = projectFavoriteRepository.findByProjectIdAndUserId(projectId, userId);
		if (projectFavorite != null) {
			return new CommonResponse.Builder<String>().message("이미 찜한 프로젝트입니다.").build();
		}
		projectFavorite = new ProjectFavorite();
		projectFavorite.setCreatedAt(LocalDateTime.now());
		projectFavorite.setUser(userService.getById(userId));
		projectFavorite.setProject(projectRepository.getOne(projectId));

		projectFavoriteRepository.save(projectFavorite);
		return new CommonResponse.Builder<String>().message("나의 프로젝트 > 찜한 프로젝트에서 확인하실 수 있습니다.").build();
	}

	@ApiOperation("프로젝트, 컨테스트 찜하기 해제")
	@RequestMapping(method = RequestMethod.POST, value = "/deleteFromWishList")
	public CommonResponse deleteFromWishList(@RequestParam(value = "projectId") Long projectId) {
		Long userId = userService.getCurrentUser().getId();
		ProjectFavorite projectFavorite = projectFavoriteRepository.findByProjectIdAndUserId(projectId, userId);
		projectFavoriteRepository.delete(projectFavorite);
		return CommonResponse.ok();
	}

	@ApiOperation("프로젝트, 컨테스트 댓글 달기")
	@RequestMapping(method = RequestMethod.POST, value = "/projects/{projectId}/comments")
	public CommonResponse replyProjectComment(
			@PathVariable("projectId") Long projectId, @RequestParam(value = "content") String content,
			@RequestParam(value = "contestEntryId", required = false) Long contestEntryId,
			@RequestParam(value = "targetUserId", required = false) Long targetUserId,
			@RequestParam(value = "parentProjectCommentId", required = false) Long parentProjectCommentId,
			@RequestParam(value = "forContestEntryUser", required = false, defaultValue = "false") boolean forContestEntryUser) {
		ProjectComment projectComment = new ProjectComment();
		projectComment.setProject(projectRepository.getOne(projectId));
		projectComment.setContent(content);
		if (Project.Status.POSTED.equals(projectComment.getProject().getStatus())) {
			if (getSessionUserId().equals(projectComment.getProject().getPostingClient().getId()) && targetUserId == null) {
				List<ProjectBid> projectBids = projectBidRepository.findByProjectIdAndBidStatusIn(projectId, Arrays.asList(ProjectBid.BidStatus.APPLY));
				for (ProjectBid bid : projectBids) {
					Map<String, Object> messageVariables = new HashMap<>();
					messageVariables.put("projectName", projectComment.getProject().getTitle());
					messageService.sendMessage(bid.getParticipant(), AligoKakaoMessageTemplate.Code.TA_3201, messageVariables);
				}
			} else {
				Map<String, Object> messageVariables = new HashMap<>();
				messageVariables.put("projectName", projectComment.getProject().getTitle());
				messageVariables.put("freelancerName", userService.getCurrentUser().getExposeName());
				messageService.sendMessage(projectComment.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3202, messageVariables);
			}

			if (targetUserId != null && !targetUserId.equals(projectComment.getProject().getPostingClient().getId())) {
				User targetUser = userRepository.getOne(targetUserId);
				Map<String, Object> messageVariables = new HashMap<>();
				messageVariables.put("projectName", projectComment.getProject().getTitle());
				messageVariables.put("freelancerName", targetUser.getExposeName());
				messageService.sendMessage(targetUser, AligoKakaoMessageTemplate.Code.TA_3203, messageVariables);
			}
		} else if (Project.Status.IN_PROGRESS.equals(projectComment.getProject().getStatus())) {
			if (getSessionUserId().equals(projectComment.getProject().getPostingClient().getId())) {
				List<ProjectBid> pickedBids = projectBidRepository.findByProjectIdAndBidStatusIn(projectId, Arrays.asList(ProjectBid.BidStatus.PICKED));
				for (ProjectBid pickedBid: pickedBids) {
					Map<String, Object> messageVariables = new HashMap<>();
					messageVariables.put("projectName", projectComment.getProject().getTitle());
					messageVariables.put("freelancerName", pickedBid.getParticipant().getExposeName());
					messageService.sendMessage(projectComment.getProject().getContractedFreelancer(), AligoKakaoMessageTemplate.Code.TA_3205, messageVariables);
				}
			} else {
				Map<String, Object> messageVariables = new HashMap<>();
				messageVariables.put("projectName", projectComment.getProject().getTitle());
				messageVariables.put("freelancerName", userService.getCurrentUser().getExposeName());
				messageService.sendMessage(projectComment.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3204, messageVariables);
			}
		}
		if (parentProjectCommentId != null) {
			projectComment.setParentComment(projectCommentRepository.getOne(parentProjectCommentId));
		}
		if (targetUserId != null) {
			projectComment.setTargetUser(userRepository.getOne(targetUserId));
		}
		if (forContestEntryUser) {
			if (contestEntryId != null) {
				projectComment.setTargetUser(projectBidRepository.getOne(contestEntryId).getParticipant());
			}
		}
		projectComment.setUser(userService.getCurrentUser());
		if (!isLoggedIsAsClient()) {
			projectComment.setTargetUser(projectComment.getProject().getPostingClient());
		}
		projectCommentRepository.save(projectComment);

		return CommonResponse.ok();
	}

	/**
	 * 프로젝트 계약 파일 추가
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/addContractFile")
	public CommonResponse addContractFile(@RequestParam(value = "projectId") Long projectId,
			@RequestParam(value = "whoAdd") String whoAdd, @RequestParam(value = "file") MultipartFile file) {

		ProjectContractFile conFile = new ProjectContractFile();
		conFile.setProject(projectRepository.getOne(projectId));
		conFile.setFileUrl(storageService.storeFile(file));
		conFile.setFileName(file.getOriginalFilename());

		if (whoAdd.equals("CLIENT")) {
			conFile.setUserRole(User.Role.ROLE_CLIENT);
		} else {
			conFile.setUserRole(User.Role.ROLE_FREELANCER);
		}

		projectContractFileRepository.save(conFile);
		return new CommonResponse.Builder<String>().message("정상적으로 추가 하였습니다.").build();
	}

	/**
	 * 평가 없이 프로젝트 종료하기
	 */

	@RequestMapping(method = RequestMethod.POST, value = "/projectComplete")
	public CommonResponse projectComplete(@RequestParam("projectId") Long projectId) {
		projectService.setProjectComplete(projectId);
		return new CommonResponse.Builder<String>().message("정상적으로 프로젝트를 종료 하였습니다.").build();
	}

    @ApiOperation("프로젝트 결제 요청")
    @RequestMapping(method = RequestMethod.POST, value = "/project/{id}/purchases")
    public CommonResponse purchaseProjectOptions() {

        return CommonResponse.ok();
    }

    @ApiOperation("선택한 전문가와 프로젝트 진행")
    @RequestMapping(method = RequestMethod.POST, value = "/projects/freelancers/{freelancerId}")
    public CommonResponse startProjectWithSelectedFreelancer(@PathVariable("freelancerId") Long freelancerId,
                                                             @RequestParam(value = "type", defaultValue = "PROJECT") Project.Type type,
															 @RequestParam("name") String name, @RequestParam("description") String description,
                                                             @RequestParam("price") Integer price,
                                                             @RequestParam(value = "projectId", required = false) Long projectId,
                                                             @RequestParam(value = "projectDescriptionFile", required = false) MultipartFile projectDescriptionFile) {
        projectService.createAndStartWithFreelancer(getSessionUserId(), type, name, description, price, projectDescriptionFile, freelancerId, projectId);

        return CommonResponse.ok();
    }

    @ApiOperation("프로젝트 임시저장")
    @RequestMapping(method = RequestMethod.POST, value = "/projects/temp")
    public CommonResponse insertProject(@RequestParam("name") String name, @RequestParam("description") String description,
										@RequestParam(value = "logoImageFile", required = false) MultipartFile logoImageFile,
										@RequestParam(value = "modifiedLogoImage", required = false) String modifiedLogoImage,
										@RequestParam(value = "selectedKeywordJsonId[]", required = false) Long[] selectedKeywordJsonId,
										@RequestParam(value = "selectedKeywordJsonCategoryName[]", required = false) String[] selectedKeywordJsonCategoryName,
										@RequestParam(value = "selectedKeywordJsonKeyword[]", required = false) String[] selectedKeywordJsonKeyword,
										@RequestParam("expectedPeriod") Project.ExpectedPeriod expectedPeriod,
										@RequestParam(value = "expectedPeriodInput", required = false) String expectedPeriodInput,
                                        @RequestParam(value = "projectDescriptionFile", required = false) MultipartFile projectDescriptionFile,
                                        @RequestParam(value = "defaultMoney", required = false) Integer defaultMoney,
                                        @RequestParam(value = "incentiveFrom", required = false) Float incentiveFrom,
                                        @RequestParam(value = "incentiveTo", required = false) Float incentiveTo,
										@RequestParam(value = "budgetFrom", required = false) Integer budgetFrom,
										@RequestParam(value = "budgetTo", required = false) Integer budgetTo,
										@RequestParam(value = "numberOfPersons", required = false) Integer numberOfPersons,
										@RequestParam(value = "payCriteria", required = false) Project.PayCriteria payCriteria,
										@RequestParam(value = "payMean", required = false) Project.PayMean payMean,
										@RequestParam(value = "workPlaceAddress1", required = false) String workPlaceAddress1,
										@RequestParam(value = "workPlaceAddress2", required = false) String workPlaceAddress2,
                                        @RequestParam("workPlace") Project.WorkPlace workPlace,
                                        @RequestParam("useEscrow") boolean useEscrow,
                                        @RequestParam(value = "bankForReceivingPayment", required = false) Long bankForReceivingPaymentId,
                                        @RequestParam(value = "bankAccountForReceivingPayment", required = false) String bankAccountForReceivingPayment,
                                        @RequestParam(value = "bankAccountName", required = false) String bankAccountName,
                                        @RequestParam("postingEndAt") String endAt,
                                        @RequestParam(value = "projectId", required = false) Long projectId,
                                        @RequestParam(value = "projectFile[]", required = false) MultipartFile[] projectFiles) {


		List<Category> categories = new ArrayList<>();
		if (selectedKeywordJsonId!= null && selectedKeywordJsonId.length > 0) {
			Set<SelectedKeywordModel> keywordModels = new HashSet<>();
			for (int i = 0; i < selectedKeywordJsonId.length; i++) {
				SelectedKeywordModel selectedKeywordModel = new SelectedKeywordModel();
				selectedKeywordModel.setKeyword(selectedKeywordJsonKeyword[i]);
				selectedKeywordModel.setId(selectedKeywordJsonId[i]);
				selectedKeywordModel.setCategoryName(selectedKeywordJsonCategoryName[i]);
				keywordModels.add(selectedKeywordModel);
			}

			categories = categoryService.createCategoryByParentIdNameJsons(new ArrayList<>(keywordModels));
		}

		List<Long> categoryIds = categories.stream().map(Category::getId).collect(Collectors.toList());

		Project project = projectService.createOrUpdateTemporarily(projectId, getSessionUserId(), name, description,
				logoImageFile, modifiedLogoImage,
				categoryIds, expectedPeriod, expectedPeriodInput, projectDescriptionFile, defaultMoney, incentiveFrom, incentiveTo,
				budgetFrom, budgetTo, numberOfPersons, payCriteria, payMean, workPlace, workPlaceAddress1, workPlaceAddress2,
				useEscrow, bankForReceivingPaymentId, bankAccountForReceivingPayment, bankAccountName, LocalDate.now(), TimeUtil.convertStrToLocalDate(endAt), projectFiles);
        return new CommonResponse.Builder<Project>().data(project).build();
    }

    @ApiOperation("프로젝트 수정")
    @RequestMapping(method = RequestMethod.POST, value = "/projects/{projectId}/modifications")
    public CommonResponse modifyProject(@PathVariable(value = "projectId", required = false) Long projectId,
										@RequestParam("name") String name, @RequestParam("description") String description,
										@RequestParam(value = "logoImageFile", required = false) MultipartFile logoImageFile,
										@RequestParam(value = "modifiedLogoImage", required = false) String modifiedLogoImage,
										@RequestParam(value = "selectedKeywordJsonId[]", required = false) Long[] selectedKeywordJsonId,
										@RequestParam(value = "selectedKeywordJsonCategoryName[]", required = false) String[] selectedKeywordJsonCategoryName,
										@RequestParam(value = "selectedKeywordJsonKeyword[]", required = false) String[] selectedKeywordJsonKeyword,
										@RequestParam("expectedPeriod") Project.ExpectedPeriod expectedPeriod,
										@RequestParam(value = "expectedPeriodInput", required = false) String expectedPeriodInput,
										@RequestParam(value = "projectDescriptionFile", required = false) MultipartFile projectDescriptionFile,
										@RequestParam(value = "defaultMoney", required = false) Integer defaultMoney,
										@RequestParam(value = "incentiveFrom", required = false) Float incentiveFrom,
										@RequestParam(value = "incentiveTo", required = false) Float incentiveTo,
										@RequestParam(value = "budgetFrom", required = false) Integer budgetFrom,
										@RequestParam(value = "budgetTo", required = false) Integer budgetTo,
										@RequestParam(value = "numberOfPersons", required = false) Integer numberOfPersons,
										@RequestParam(value = "payCriteria", required = false) Project.PayCriteria payCriteria,
										@RequestParam(value = "payMean", required = false) Project.PayMean payMean,
										@RequestParam(value = "workPlaceAddress1", required = false) String workPlaceAddress1,
										@RequestParam(value = "workPlaceAddress2", required = false) String workPlaceAddress2,
										@RequestParam("workPlace") Project.WorkPlace workPlace,
										@RequestParam("useEscrow") boolean useEscrow,
										@RequestParam(value = "postingEndAt", required = false) String endAt,
										@RequestParam(value = "projectFile[]", required = false) MultipartFile[] projectFiles) {
		List<Category> categories = new ArrayList<>();
		if (selectedKeywordJsonId!= null && selectedKeywordJsonId.length > 0) {
			List<SelectedKeywordModel> keywordModels = new ArrayList<>();
			for (int i = 0; i < selectedKeywordJsonId.length; i++) {
				SelectedKeywordModel selectedKeywordModel = new SelectedKeywordModel();
				selectedKeywordModel.setKeyword(selectedKeywordJsonKeyword[i]);
				selectedKeywordModel.setId(selectedKeywordJsonId[i]);
				selectedKeywordModel.setCategoryName(selectedKeywordJsonCategoryName[i]);
				keywordModels.add(selectedKeywordModel);
			}

			categories = categoryService.createCategoryByParentIdNameJsons(keywordModels);
		}

		List<Long> categoryIds = new ArrayList<>(categories.stream().map(Category::getId).collect(Collectors.toSet()));

        projectService.update(projectId, getSessionUserId(), name, description,
				logoImageFile, modifiedLogoImage,
				categoryIds, expectedPeriod, expectedPeriodInput, projectDescriptionFile, defaultMoney, incentiveFrom, incentiveTo,
				budgetFrom, budgetTo, numberOfPersons, payCriteria, payMean, workPlace, workPlaceAddress1, workPlaceAddress2,
				useEscrow, TimeUtil.convertStrToLocalDate(endAt), projectFiles);

        return CommonResponse.ok();
    }

    @ApiOperation("프로젝트 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/projects")
    public CommonListResponse<List<Project>> getProjects(@RequestParam("status") Project.Status status, @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") Sort.Direction sortDirection,
                                                  @RequestParam(value = "sortProperty", required = false, defaultValue = "successfulBidPrice") String sortProperty,
                                                  @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                  @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        Page<Project> projectPage = projectRepository.findByPostingClientIdAndStatus(userId, status, PageRequest.of(pageNumber, pageSize, new Sort(sortDirection,sortProperty)));
        return new CommonListResponse.Builder<List<Project>>()
                .totalCount(projectPage.getTotalElements())
                .currentPage(pageNumber)
                .totalPages(projectPage.getTotalPages())
                .data(projectPage.getContent())
                .build();
    }

    @ApiOperation("프로젝트 상세 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/projects/{projectId}")
    public CommonResponse<Project> getProjects(@PathVariable("projectId") Long projectId) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        Project project = projectRepository.getOne(projectId);
        return new CommonResponse.Builder<Project>().data(project).build();
    }

    @ApiOperation("찜한 프로젝트 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/projects/favorite")
    public CommonListResponse<List<ProjectFavorite>> getFavoriteProjects(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                   @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        Page<ProjectFavorite> projectFavorites = projectFavoriteRepository.findByUserId(userId, PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        return new CommonListResponse.Builder<List<ProjectFavorite>>()
                .totalCount(projectFavorites.getTotalElements())
                .currentPage(pageNumber)
                .totalPages(projectFavorites.getTotalPages())
                .data(projectFavorites.getContent())
                .build();
    }

    @ApiOperation("프로젝트 상태 변경")
	@Transactional
	@RequestMapping(method = RequestMethod.PUT, value = "/projects/{id}")
	public void changeProjectStatus(@PathVariable("id") Long projectId,
									@RequestParam(value = "status", required = false) Project.Status status,
									@RequestParam(value = "postingEndAt", required = false) String postingEndAt,
									@RequestParam(value = "useEscrow", required = false) Boolean useEscrow,
									@RequestParam(value = "externalExpose", required = false) Boolean externalExpose,
									HttpServletResponse response) throws IOException {
    	Project project = projectRepository.getOne(projectId);
    	if (!project.getPostingClient().getId().equals(getSessionUserId())) {
    		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    		return;
		}
    	project.setStatus(status);
    	if (Project.Status.POSTED.equals(status)) {
    		project.setPostingStartAt(LocalDateTime.now());
		} else if (Project.Status.CANCELLED.equals(status)) {
    		project.setCancelAt(LocalDateTime.now());
		}
    	project.setUpdatedAt(LocalDateTime.now());
    	if (StringUtils.isNotEmpty(postingEndAt)) {
    		project.setPostingEndAt(LocalDateTime.of(TimeUtil.convertStrToLocalDate(postingEndAt), LocalTime.now()));
		}
		if (useEscrow != null && useEscrow) {
			project.setUseEscrow(true);
		}
    	projectRepository.save(project);

    	if (Project.Status.POSTED.equals(status)) {
			ProjectItemTicket projectItemTicketForInternal = new ProjectItemTicket();
			projectItemTicketForInternal.setExpiredAt(project.getPostingEndAt());
			projectItemTicketForInternal.setProject(project);
			projectItemTicketForInternal.setProjectProductItemType(projectProductItemTypeRepository.findByCodeAndProjectType(ProjectProductItemType.Code.INTERNAL, Project.Type.PROJECT));
			projectItemTicketRepository.save(projectItemTicketForInternal);

			ProjectItemTicketLog logForInternal = new ProjectItemTicketLog();
			logForInternal.setExpiredAt(project.getPostingEndAt());
			logForInternal.setProject(project);
			logForInternal.setProjectProductItemType(projectItemTicketForInternal.getProjectProductItemType());
			projectItemTicketLogRepository.save(logForInternal);

			if (externalExpose != null && externalExpose) {
				ProjectItemTicket projectItemTicket = new ProjectItemTicket();
				projectItemTicket.setExpiredAt(project.getPostingEndAt());
				projectItemTicket.setProject(project);
				projectItemTicket.setProjectProductItemType(projectProductItemTypeRepository.findByCodeAndProjectType(ProjectProductItemType.Code.EXTERNAL, Project.Type.PROJECT));
				projectItemTicketRepository.save(projectItemTicket);

				ProjectItemTicketLog log = new ProjectItemTicketLog();
				log.setExpiredAt(project.getPostingEndAt());
				log.setProject(project);
				log.setProjectProductItemType(projectItemTicket.getProjectProductItemType());
				projectItemTicketLogRepository.save(log);
			}

			if (useEscrow != null && useEscrow) {
				ProjectItemTicket projectItemTicket = new ProjectItemTicket();
				projectItemTicket.setExpiredAt(project.getPostingEndAt());
				projectItemTicket.setProject(project);
				projectItemTicket.setProjectProductItemType(projectProductItemTypeRepository.findByCodeAndProjectType(ProjectProductItemType.Code.ESCROW, Project.Type.PROJECT));
				projectItemTicketRepository.save(projectItemTicket);

				ProjectItemTicketLog log = new ProjectItemTicketLog();
				log.setExpiredAt(project.getPostingEndAt());
				log.setProject(project);
				log.setProjectProductItemType(projectItemTicket.getProjectProductItemType());
				projectItemTicketLogRepository.save(log);
			}

			if (!project.isSendKeywordDuplicatedMail()) {
				keywordOrSectorAlarmService.sendMail(project);
			}
			messageService.sendMessage(project.getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3174, null);
		} else if (Project.Status.CANCELLED.equals(status)) {
    		for (ProjectBid bid: projectBidRepository.findByProjectIdAndInvalidFalse(projectId)) {
    			bid.setBidStatus(ProjectBid.BidStatus.CANCEL);
    			bid.setFailedAt(LocalDateTime.now());
    			projectBidRepository.save(bid);

    			if (Project.Type.CONTEST.equals(project.getProjectType())) {
					Map<String, Object> messageVariables = new HashMap<>();
					messageVariables.put("freelancerName", bid.getParticipant().getExposeName());
					messageVariables.put("projectName", project.getTitle());
					messageVariables.put("cancelDividend", project.getExpectedCancelDividend());
					messageService.sendMessage(bid.getParticipant(), AligoKakaoMessageTemplate.Code.TA_3183, messageVariables);
				}
			}
			projectService.refund(projectId);

    		if (Project.Type.CONTEST.equals(project.getProjectType())) {
				if (StringUtils.isNotEmpty(project.getPostingClient().getEmail()) && project.getPostingClient().isReceiveEmail()) {
					frkEmailService.sendContestCancelByClientAlarmToClient(project.getPostingClient(), project, project.getPostingClient().getEmail());
				}
				List<User> receiveFreelancerUsers = projectBidRepository.findByProjectIdAndInvalidFalse(projectId).stream().map(ProjectBid::getParticipant).filter(User::isReceiveEmail).collect(Collectors.toList());

				for (User freelancer: receiveFreelancerUsers) {
					if (StringUtils.isEmpty(freelancer.getEmail())) continue;
					frkEmailService.sendContestCancelByClientAlarmToFreelancer(freelancer, project, freelancer.getEmail());
				}

				Map<String, Object> messageVariables = new HashMap<>();
				messageVariables.put("clientName", project.getPostingClient().getExposeName());
				messageVariables.put("projectName", project.getTitle());
				messageService.sendMessage(project.getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3182, messageVariables);

			} else if (Project.Type.PROJECT.equals(project.getProjectType())) {
				if (StringUtils.isNotEmpty(project.getPostingClient().getEmail()) && project.getPostingClient().isReceiveEmail()) {
					frkEmailService.sendProjectCancelByClientAlarmToClient(project.getPostingClient(), project, project.getPostingClient().getEmail());
				}
			}
		}
	}

	@ApiOperation("프로젝트 삭제")
	@RequestMapping(method = RequestMethod.DELETE, value = "/projects/{id}")
	public void deleteProjectItem(@PathVariable("id") Long projectId) {
    	Project project = projectRepository.getOne(projectId);
    	project.setInvalid(true);
    	projectRepository.save(project);
	}

}
