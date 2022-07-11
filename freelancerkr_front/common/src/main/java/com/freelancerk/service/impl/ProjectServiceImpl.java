
package com.freelancerk.service.impl;

import com.freelancerk.domain.*;
import com.freelancerk.domain.Project.Feeling;
import com.freelancerk.domain.Project.Status;
import com.freelancerk.domain.repository.*;
import com.freelancerk.domain.specification.ProjectCommentSpecifications;
import com.freelancerk.exception.ContestNotFullyPickedException;
import com.freelancerk.service.PointService;
import com.freelancerk.service.ProjectService;
import com.freelancerk.service.StorageService;
import com.freelancerk.service.UserService;
import com.freelancerk.vo.TotalPagesInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

	private static int ITEM_PER_PREMIUM_PAGE = 8;
	private static int ITEM_PER_SPONSORED_PAGE = 16;
	private static int ITEM_PER_PROJECT_COMPLETED_PAGE = 12;
	private static int ITEM_PER_PROJECT_FAVORITE_PAGE = 12;

	private UserService userService;
	private PointService pointService;
	private UserRepository userRepository;
	private StorageService storageService;
	private KeywordRepository keywordRepository;
	private ProjectRepository projectRepository;
	private PurchaseRepository purchaseRepository;
	private BankTypeRepository bankTypeRepository;
	private CategoryRepository categoryRepository;
	private EscrowLogRepository escrowLogRepository;
	private ProjectBidRepository projectBidRepository;
	private ProjectLogRepository projectLogRepository;
	private ContestSectorRepository contestSectorRepository;
	private ClientPointLogRepository clientPointLogRepository;
	private ProjectCommentRepository projectCommentRepository;
	private ProjectCategoryRepository projectCategoryRepository;
	private ProjectFavoriteRepository projectFavoriteRepository;
	private ContestSectorTypeRepository contestSectorTypeRepository;
	private ProjectPropositionRepository projectPropositionRepository;
	private ProjectFromContestRepository projectFromContestRepository;
	private ProjectContractFileRepository projectContractFileRepository;
	private CancelRefundPointLogRepository cancelRefundPointLogRepository;
	private ProjectProductItemTypeRepository projectProductItemTypeRepository;
	private ContestSectorMetaCategoryRepository contestSectorMetaCategoryRepository;

	public ProjectServiceImpl(UserService userService, PointService pointService,
							  UserRepository userRepository, StorageService storageService, KeywordRepository keywordRepository,
							  ProjectRepository projectRepository, PurchaseRepository purchaseRepository,
							  BankTypeRepository bankTypeRepository, CategoryRepository categoryRepository, EscrowLogRepository escrowLogRepository,
							  ProjectBidRepository projectBidRepository, ProjectLogRepository projectLogRepository, ClientPointLogRepository clientPointLogRepository,
							  ContestSectorRepository contestSectorRepository, ProjectCommentRepository projectCommentRepository,
							  ProjectCategoryRepository projectCategoryRepository, ProjectFavoriteRepository projectFavoriteRepository,
							  ContestSectorTypeRepository contestSectorTypeRepository, ProjectPropositionRepository projectPropositionRepository,
							  ProjectFromContestRepository projectFromContestRepository, ProjectContractFileRepository projectContractFileRepository,
							  CancelRefundPointLogRepository cancelRefundPointLogRepository,
							  ProjectProductItemTypeRepository projectProductItemTypeRepository, ContestSectorMetaCategoryRepository contestSectorMetaCategoryRepository) {
		this.userService = userService;
		this.pointService = pointService;
		this.userRepository = userRepository;
		this.storageService = storageService;
		this.keywordRepository = keywordRepository;
		this.projectRepository = projectRepository;
		this.purchaseRepository = purchaseRepository;
		this.bankTypeRepository = bankTypeRepository;
		this.categoryRepository = categoryRepository;
		this.escrowLogRepository = escrowLogRepository;
		this.projectBidRepository = projectBidRepository;
		this.projectLogRepository = projectLogRepository;
		this.contestSectorRepository = contestSectorRepository;
		this.clientPointLogRepository = clientPointLogRepository;
		this.projectCommentRepository = projectCommentRepository;
		this.projectCategoryRepository = projectCategoryRepository;
		this.projectFavoriteRepository = projectFavoriteRepository;
		this.contestSectorTypeRepository = contestSectorTypeRepository;
		this.projectPropositionRepository = projectPropositionRepository;
		this.projectFromContestRepository = projectFromContestRepository;
		this.projectContractFileRepository = projectContractFileRepository;
		this.cancelRefundPointLogRepository = cancelRefundPointLogRepository;
		this.projectProductItemTypeRepository = projectProductItemTypeRepository;
		this.contestSectorMetaCategoryRepository = contestSectorMetaCategoryRepository;
	}

	/**
	 * 프로젝트 계약서 파일을 가져옵니다.
	 */
	@Override
	public List<ProjectContractFile> getProjectContractFiles(Long projectId) {
		return projectContractFileRepository.findByProjectId(projectId);
	}

	@Override
	public List<ProjectContractFile> getProjectContractFilesByFreelancer(Long projectId) {
		return projectContractFileRepository.findByProjectIdAndUserRoleAndInvalidFalse(projectId,
				User.Role.ROLE_FREELANCER);
	}

	@Override
	public List<ProjectContractFile> getProjectContractFilesByClient(Long projectId) {
		return projectContractFileRepository.findByProjectIdAndUserRoleAndInvalidFalse(projectId, User.Role.ROLE_CLIENT);
	}

	/**
	 * 찜한 프로젝트 목록을 가져옵니다.
	 */
	@Override
	public Page<ProjectFavorite> getFreelancerProjectFavoritePages(Long freelancerId, String searchWord,
			int currentPageNumber, LocalDateTime startDate, LocalDateTime endDate) {

		if (startDate == null || endDate == null) {
			return projectFavoriteRepository.findByUserIdAndProjectTitleContaining(freelancerId, searchWord,
					PageRequest.of(currentPageNumber, ITEM_PER_PROJECT_FAVORITE_PAGE,
							new Sort(Sort.Direction.DESC, "createdAt")));
		} else {
			return projectFavoriteRepository.findByUserIdAndProjectTitleContainingAndCreatedAtAfterAndCreatedAtBefore(
					freelancerId, searchWord, startDate, endDate, PageRequest.of(currentPageNumber,
							ITEM_PER_PROJECT_FAVORITE_PAGE, new Sort(Sort.Direction.DESC, "createdAt")));
		}

	}

	/**
	 * 프로젝트 완료하기
	 */
	@Override
	public void setProjectComplete(Long projectId) {
		Project project = projectRepository.getOne(projectId);
		project.setStatus(Project.Status.COMPLETED);
		project.setCompletedAt(LocalDateTime.now());
		projectRepository.save(project);
	}

	/**
	 * 찜한 프로젝트의 페이지 정보를 가져옵니다.
	 */
	@Override
	public TotalPagesInfoVO getTotalFreelancerProjectFavoritePages(Long freelancerId, String searchWord,
			int currentPageNumber, LocalDateTime startDate, LocalDateTime endDate) {
		int countOfElements = 0;
		if (startDate == null || endDate == null) {
			countOfElements = projectFavoriteRepository.countByUserIdAndProjectTitleContaining(freelancerId,
					searchWord);
		} else {
			countOfElements = projectFavoriteRepository
					.countByUserIdAndProjectTitleContainingAndCreatedAtAfterAndCreatedAtBefore(freelancerId, searchWord,
							startDate, endDate);
		}

		TotalPagesInfoVO vo = new TotalPagesInfoVO();
		vo.setCountOfTotalElements(countOfElements);
		int countOfPage = (int) (Math.ceil((double) countOfElements / ITEM_PER_PREMIUM_PAGE));
		countOfPage = countOfPage < 0 ? 0 : countOfPage;
		vo.setCountOfTotalPages(countOfPage);
		return vo;
	}

	@Transactional
	@Override
	public void update(Long projectId, Long userId, String title, String description, MultipartFile logoImageFile, String modifiedLogoImage, List<Long> categoryIds,
					   Project.ExpectedPeriod expectedPeriod, String expectedPeriodInput, MultipartFile projectDescriptionFile,
					   Integer defaultMoney, Float incentiveFrom, Float incentiveTo, Integer budgetFrom, Integer budgetTo, Integer numberOfPersons, Project.PayCriteria payCriteria, Project.PayMean payMean, Project.WorkPlace workPlace,
					   String workPlaceAddress1, String workPlaceAddress2, Boolean useEscrow, LocalDate endAt, MultipartFile[] projectFiles) {
		Project project = projectRepository.getOne(projectId);
		project.setUpdatedAt(LocalDateTime.now());
		project.setDefaultMoney(defaultMoney);
		project.setIncentiveFrom(incentiveFrom);
		project.setIncentiveTo(incentiveTo);
		project.setBudgetFrom(budgetFrom);
		project.setBudgetTo(budgetTo);
		project.setNumberOfPersons(numberOfPersons);
		project.setDescription(description);
		if (endAt != null) {
			project.setPostingEndAt(LocalDateTime.of(endAt, LocalTime.now()));
		}
		project.setExpectedPeriod(expectedPeriod);
		project.setExpectedPeriodEtc(expectedPeriodInput);
		project.setPayCriteria(payCriteria);
		project.setPayMean(payMean);
		project.setTitle(title);
		project.setUseEscrow(useEscrow);
		project.setWorkPlace(workPlace);
		if (Project.WorkPlace.OFF_LINE.equals(workPlace)) {
			project.setWorkPlaceAddress1(workPlaceAddress1);
			project.setWorkPlaceAddress2(workPlaceAddress2);
		} else {
			project.setWorkPlaceAddress1(null);
			project.setWorkPlaceAddress2(null);
		}
		if (projectDescriptionFile != null && !projectDescriptionFile.isEmpty()) {
			project.setProjectDescriptionFileName(projectDescriptionFile.getOriginalFilename());
			project.setProjectDescriptionFileUrl(storageService.storeFile(projectDescriptionFile));
		}
		project = projectRepository.save(project);

		project.getProjectCategories().clear();
		projectCategoryRepository.deleteByProjectId(projectId);
		for (Long categoryId: categoryIds) {
			ProjectCategory projectCategory = new ProjectCategory();
			projectCategory.setCategory(categoryRepository.getOne(categoryId));
			projectCategory.setProject(project);
			projectCategoryRepository.save(projectCategory);

			Keyword keyword = keywordRepository.findTop1ByCategoryId(categoryId);
			if (keyword == null) {
				keyword = new Keyword();
				keyword.setUsageCount(0L);
				keyword.setCategory(projectCategory.getCategory());
				keyword.setName(projectCategory.getCategory().getName());
			}
			keyword.setUsageCount(keyword.getUsageCount() + 1);
			keywordRepository.save(keyword);
		}

		User user = project.getPostingClient();
		if (StringUtils.isNotEmpty(modifiedLogoImage)) {
			user.setLogoImageUrl(modifiedLogoImage);
		} else if (logoImageFile != null && !logoImageFile.isEmpty()) {
			user.setLogoImageUrl(storageService.storeFile(logoImageFile));
		}
		userRepository.save(user);

		projectRepository.save(project);
	}

	@Transactional
	@Override
	public Project createOrUpdateTemporarily(Long projectId, Long userId, String title, String description, MultipartFile logoImageFile, String modifiedLogoImage, List<Long> categoryIds,
											 Project.ExpectedPeriod expectedPeriod, String expectedPeriodInput,
											 MultipartFile projectDescriptionFile,
											 Integer defaultMoney, Float incentiveFrom, Float incentiveTo,
											 Integer budgetFrom, Integer budgetTo, Integer numberOfPersons,
											 Project.PayCriteria payCriteria, Project.PayMean payMean, Project.WorkPlace workPlace,
											 String workPlaceAddress1, String workPlaceAddress2, Boolean useEscrow,
											 Long bankForReceivingPaymentId, String bankAccountForReceivingPayment, String bankAccountName, LocalDate startAt, LocalDate endAt, MultipartFile[] projectFiles) {
		Project project = projectId == null?new Project():projectRepository.getOne(projectId);
		project.setPostingClient(userRepository.getOne(userId));
		project.setStatus(Project.Status.TEMP);
		project.setProjectType(Project.Type.PROJECT);
		project.setDefaultMoney(defaultMoney);
		project.setIncentiveFrom(incentiveFrom);
		project.setIncentiveTo(incentiveTo);
		project.setBudgetFrom(budgetFrom);
		project.setBudgetTo(budgetTo);
		project.setNumberOfPersons(numberOfPersons);
		project.setDescription(description);
		project.setPostingStartAt(LocalDateTime.of(startAt, LocalTime.now()));
		project.setPostingEndAt(LocalDateTime.of(endAt, LocalTime.now()));
		project.setExpectedPeriod(expectedPeriod);
		project.setExpectedPeriodEtc(expectedPeriodInput);
		project.setPayCriteria(payCriteria);
		project.setPayMean(payMean);
		project.setTitle(title);
		project.setUseEscrow(useEscrow);
		project.setWorkPlace(workPlace);
		if (Project.WorkPlace.OFF_LINE.equals(workPlace)) {
			project.setWorkPlaceAddress1(workPlaceAddress1);
			project.setWorkPlaceAddress2(workPlaceAddress2);
		} else {
			project.setWorkPlaceAddress1(null);
			project.setWorkPlaceAddress2(null);
		}
		if (projectDescriptionFile != null && !projectDescriptionFile.isEmpty()) {
			project.setProjectDescriptionFileName(projectDescriptionFile.getOriginalFilename());
			project.setProjectDescriptionFileUrl(storageService.storeFile(projectDescriptionFile));
		}

		if (useEscrow && bankForReceivingPaymentId != null) {
			project.setBankForReceivingPayment(bankTypeRepository.getOne(bankForReceivingPaymentId));
			project.setBankAccountName(bankAccountName);
			project.setBankAccountForReceivingPayment(bankAccountForReceivingPayment);

			User clientUser = project.getPostingClient();
			clientUser.setBankForReceivingPayment(project.getBankForReceivingPayment());
			clientUser.setBankAccountName(bankAccountName);
			clientUser.setBankAccountForReceivingPayment(bankAccountForReceivingPayment);
			userRepository.save(clientUser);
		}

		project = projectRepository.save(project);

		projectCategoryRepository.deleteByProjectId(project.getId());
		for (Long categoryId: categoryIds) {
			ProjectCategory projectCategory = new ProjectCategory();
			projectCategory.setCategory(categoryRepository.getOne(categoryId));
			projectCategory.setProject(project);
			projectCategoryRepository.save(projectCategory);
		}

		User user = project.getPostingClient();
		if (StringUtils.isNotEmpty(modifiedLogoImage)) {
			user.setLogoImageUrl(modifiedLogoImage);
		} else if (logoImageFile != null && !logoImageFile.isEmpty()) {
			user.setLogoImageUrl(storageService.storeFile(logoImageFile));
		}
		userRepository.save(user);

		return project;
	}

	@Transactional
	@Override
	public void createAndStartWithFreelancer(Long userId, Project.Type type, String title, String description, Integer price,
											 MultipartFile projectDescriptionFile, Long freelancerId, Long projectId) {
		Project project = new Project();
		project.setTitle(title);
		project.setPostingClient(userRepository.getOne(userId));
		project.setContractedFreelancer(userRepository.getOne(freelancerId));
		project.setStatus(Project.Status.IN_PROGRESS);
		project.setDescription(description);
		if (projectDescriptionFile != null && !projectDescriptionFile.isEmpty()) {
			project.setProjectDescriptionFileName(projectDescriptionFile.getOriginalFilename());
			project.setProjectDescriptionFileUrl(storageService.storeFile(projectDescriptionFile));
		}
		project.setProjectType(type);
		project.setUseEscrow(true);
		project.setSuccessBidPrice(price);
		project.setContractPrice(price); // todo vat?
		project.setUseEscrow(true);
		project.setStartAt(LocalDateTime.now());
		project.setSuccessBidAt(LocalDateTime.now());
		project.setContractAt(LocalDateTime.now());
		project.setPostingStartAt(LocalDateTime.now());
		project.setPostingEndAt(LocalDateTime.now().plusMonths(1));
		project.setStartFromDirectMarket(true);
		project = projectRepository.save(project);

		ProjectBid projectBid = new ProjectBid();
		projectBid.setBidStatus(ProjectBid.BidStatus.PICKED);
		projectBid.setSuccessBidAt(LocalDateTime.now());
		projectBid.setProject(project);
		projectBid.setParticipant(userRepository.getOne(freelancerId));
		projectBid.setAmountOfMoney(price); // todo vat?
		projectBid = projectBidRepository.save(projectBid);

		List<ProjectBid> otherProjectBids = projectBidRepository.findByProjectIdAndIdNot(project.getId(), projectBid.getId());
		for (ProjectBid item: otherProjectBids) {
			item.setBidStatus(ProjectBid.BidStatus.FAILED);
			item.setFailedAt(LocalDateTime.now());
		}
		projectBidRepository.saveAll(otherProjectBids);

		ProjectLog projectLog = new ProjectLog();
		projectLog.setCreatedAt(LocalDateTime.now());
		projectLog.setProject(project);
		projectLog.setDesc("프로젝트 시작 - 선택한 프리랜서");
		projectLog.setUser(project.getPostingClient());
		projectLog.setType(ProjectLog.Type.START_WITH_FREELANCER);
		projectLogRepository.save(projectLog);

		ProjectProposition projectProposition = projectPropositionRepository.findByProjectIdAndFreelancerId(project.getId(), userId);
		if (projectProposition != null) {
			projectProposition.setStatus(ProjectProposition.Status.ACCEPT);
			projectPropositionRepository.save(projectProposition);
		}

		if (projectId != null) {
			ProjectFromContest projectFromContest = new ProjectFromContest();
			projectFromContest.setContest(projectRepository.getOne(projectId));
			projectFromContest.setProject(project);
			projectFromContestRepository.save(projectFromContest);
		}
	}

	@Override
	public Project getById(Long projectId) {
		return projectRepository.getOne(projectId);
	}

	@Override
	public int calculateOptionPrice(List<ProjectProductItemType> options, Integer[] optionCount, Project.Type projectType) {

		int i = 0;
		int optionPrice = 0;
		Set<String> optionItemsInPack = new HashSet<>();
		if (options != null && !options.isEmpty()) {
			for (ProjectProductItemType option : options) {
				if (option.isPack()) {
					List<String> optionCodesInPack = Arrays.asList(option.getContainsItemTypes().split(","));
					for (String optionCode: optionCodesInPack) {
						if (!optionCode.endsWith("_PACK")) {
							optionItemsInPack.add(optionCode);
						} else {
							ProjectProductItemType projectProductItemTypes = projectProductItemTypeRepository.findByCodeAndProjectType(ProjectProductItemType.Code.valueOf(optionCode), projectType);
							if (projectProductItemTypes == null || StringUtils.isEmpty(projectProductItemTypes.getContainsItemTypes())) continue;
							optionItemsInPack.addAll(Arrays.asList(projectProductItemTypes.getContainsItemTypes().split(",")));
						}
					}
				}
				i++;
			}
			i = 0;
			for (ProjectProductItemType option : options) {
				if (optionItemsInPack.contains(option.getCode().name())) continue;
				if (optionCount.length <= i || optionCount[i] == null) continue;
				optionPrice += option.getMountOfMoneyUnit() * optionCount[i];
				i++;
			}
		}
		return optionPrice;
	}

	@Override
	public void insertContest(Long userId, Long projectId, Integer depositMoney, Integer minimumPrice, Integer incentivePrice,
			Integer prizeFor1st, Integer prizeFor2nd, Integer prizeFor3rd, boolean payForFeeToFreelancer,
			Integer usedPoint, Integer[] optionIds, Integer[] optionCount, Integer[] packIds,
			Integer[] ProjectOptionPackCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateContest(Long userId, Long projectId, String title, String ProjectSectorName, String categoryOfBusiness,
			String majorProduct, String description, Integer quantityPage, String descriptionPerPage,
			String[] referenceWebUrl, MultipartFile[] referenceFile, String workingWebUrl,
			String corporateNameOrCatchPhrase, Status status, Integer depositMoney, String priorityTone,
			Feeling[] feelings, Boolean proceedProjectAfterProject, Integer minimumPrice, Integer incentivePrice,
			Integer prizeFor1st, Integer prizeFor2nd, Integer prizeFor3rd, Boolean payForFeeToFreelancer) {
		// TODO Auto-generated method stub

	}

	@Override
	public int refundDepositMoneyToClient(Long contestId) {
		Project contest = projectRepository.getOne(contestId);
		int depositMoney = contest.getDepositMoney() == null?0:contest.getDepositMoney();
		int numberOfEntries = projectBidRepository.countByProjectIdAndInvalidFalse(contestId);
		if (numberOfEntries <= 2 && numberOfEntries > 0) {
			return (int) (depositMoney * 0.8);
		} else if (numberOfEntries < 6) {
			return (int) (depositMoney * 0.5);
		} else  {
			return (int) (depositMoney * 0.2);
		}
	}

	@Override
	public int refundDepositMoneyToFreelancer(Long contestId) {
		Project contest = projectRepository.getOne(contestId);
		if (!Project.Type.CONTEST.equals(contest.getProjectType())) return 0;
		int depositMoney = contest.getDepositMoney() == null?0:contest.getDepositMoney();
		int numberOfEntries = projectBidRepository.countByProjectIdAndInvalidFalse(contestId);
		if (numberOfEntries == 0) return 0;

		if (numberOfEntries <= 2 && numberOfEntries > 0) {
			return (int) (depositMoney * 0.2) / numberOfEntries;
		} else if (numberOfEntries < 6) {
			return (int) (depositMoney * 0.4) / numberOfEntries;
		} else {
			return (int) (depositMoney * 0.7) / numberOfEntries;
		}
	}

	@Override
	public int refundTotalPrizeToFreelancer(Long contestId) {
		Project contest = projectRepository.getOne(contestId);
		int totalPrize = contest.getPrizeFor1st() + contest.getPrizeFor2nd() + contest.getPrizeFor3rd();
		int numberOfEntries = projectBidRepository.countByProjectIdAndInvalidFalse(contestId);
		if (numberOfEntries == 0) return 0;
		if (numberOfEntries <= 2 && numberOfEntries > 0) {
			return (int) (20000 + totalPrize * 0.2) / numberOfEntries;
		} else if (numberOfEntries <= 9) {
			return (int) (50000 + totalPrize * 0.4) / numberOfEntries;
		} else if (numberOfEntries > 9) {
			return (int) (80000 + totalPrize * 0.7) / numberOfEntries;
		}
		return 0;
	}

	@Override
	public int refundTotalPrizeToClient(Long contestId) {
		Project contest = projectRepository.getOne(contestId);
		int totalPrize = 0;
		if (Project.Type.CONTEST.equals(contest.getProjectType())) {
			totalPrize = contest.getTotalPrize();
		}
		int numberOfEntries = projectBidRepository.countByProjectIdAndInvalidFalse(contestId);
		if (numberOfEntries <= 2 && numberOfEntries > 0) {
			return (int) (20000 + totalPrize * 0.7);
		} else if (numberOfEntries <= 9) {
			return (int) (50000 + totalPrize * 0.5);
		} else if (numberOfEntries > 9) {
			return (int) (80000 + totalPrize * 0.2);
		}
		return 0;
	}

	@Transactional
	@Override
	public void pick(Long projectId, Long[] contestEntryIds) {
		int i = 0;
		Project contest = projectRepository.getOne(projectId);

		if (contest.getPrizeFor3rd() != null && contest.getPrizeFor3rd() > 0 && contestEntryIds.length < 3) {
			throw ContestNotFullyPickedException.getInstance();
		}

		if (contest.getPrizeFor2nd() != null && contest.getPrizeFor2nd() > 0 && (contest.getPrizeFor3rd() == null || contest.getPrizeFor3rd() == 0) && contestEntryIds.length < 2) {
			throw ContestNotFullyPickedException.getInstance();
		}

		if (contest.getPrizeFor1st() != null && contest.getPrizeFor1st() > 0 && (contest.getPrizeFor2nd() == null || contest.getPrizeFor2nd() == 0) && (contest.getPrizeFor3rd() == null || contest.getPrizeFor3rd() == 0)
				&& contestEntryIds.length < 1) {
			throw ContestNotFullyPickedException.getInstance();
		}

		for (Long contestEntryId : contestEntryIds) {
			ProjectBid contestEntry = projectBidRepository.getOne(contestEntryId);
			contestEntry.setPickedRank(i++);
			contestEntry.setBidStatus(ProjectBid.BidStatus.PICKED);
			contestEntry.setSuccessBidAt(LocalDateTime.now());
			if (i == 1) {
				contestEntry.setAmountOfMoney(contest.getPrizeFor1st());
			} else if (i == 2) {
				contestEntry.setAmountOfMoney(contest.getPrizeFor2nd());
			} else if (i == 3) {
				contestEntry.setAmountOfMoney(contest.getPrizeFor3rd());
			}
			projectBidRepository.save(contestEntry);
		}

		List<ProjectBid> otherProjectBids = projectBidRepository.findByProjectIdAndIdNotIn(projectId, contestEntryIds);
		for (ProjectBid item: otherProjectBids) {
			item.setBidStatus(ProjectBid.BidStatus.FAILED);
			item.setFailedAt(LocalDateTime.now());
		}
		projectBidRepository.saveAll(otherProjectBids);

		contest.setStatus(Project.Status.IN_PROGRESS);
		contest.setStartAt(LocalDateTime.now());
		contest.setSuccessBidAt(LocalDateTime.now());
		projectRepository.save(contest);
	}

	@Transactional
	@Override
	public void refund(Long projectId) {
		refundToFreelancer(projectId);
		refundToClient(projectId);
	}

	private void refundToClient(Long projectId) {
		Project project = projectRepository.getOne(projectId);
		if (!Project.Type.CONTEST.equals(project.getProjectType())) return;
		CancelRefundPointLog cancelRefundPointLog = new CancelRefundPointLog();
		cancelRefundPointLog.setProject(project);
		cancelRefundPointLog.setUser(project.getPostingClient());
		cancelRefundPointLog.setUserRole(User.Role.ROLE_CLIENT);
		cancelRefundPointLog.setPoints(project.getExpectedCancelDividend());
		cancelRefundPointLogRepository.save(cancelRefundPointLog);

		try {
			List<Purchase> purchases = purchaseRepository.findByProjectIdAndStatus(projectId, Purchase.Status.COMPLETED);
			for (Purchase purchase : purchases) {
				ClientPointLog pointLog = clientPointLogRepository.findTop1ByPurchaseId(purchase.getId());
				pointService.getRidOfPointsFromClient(project.getPostingClient().getId(), pointLog.getAmount(), "컨테스트 취소로 지급 포인트 회수", purchase, true);
			}
		} catch (Exception e) {
			log.error("<<< 컨테스트 취소로 지급 포인트 회수 에러", e);
		}
	}

	private void refundToFreelancer(Long projectId) {
		Project project = projectRepository.getOne(projectId);
		if (!Project.Type.CONTEST.equals(project.getProjectType())) return;
		int refundMoney = refundDepositMoneyToFreelancer(projectId) + refundTotalPrizeToFreelancer(projectId);
		for (ProjectBid bid: projectBidRepository.findByProjectIdAndBidStatusIn(projectId, Arrays.asList(ProjectBid.BidStatus.CANCEL))) {
			User user = bid.getParticipant();
			bid.setPointsForRefund(refundMoney);

			CancelRefundPointLog cancelRefundPointLog = new CancelRefundPointLog();
			cancelRefundPointLog.setProjectBid(bid);
			cancelRefundPointLog.setProject(project);
			cancelRefundPointLog.setUser(bid.getParticipant());
			cancelRefundPointLog.setUserRole(User.Role.ROLE_FREELANCER);
			cancelRefundPointLog.setPoints(project.getExpectedCancelDividend());
			cancelRefundPointLogRepository.save(cancelRefundPointLog);

			pointService.givePointsToFreelancerForContestRefund(user.getId(), refundMoney, "취소배당", project);
		}
	}

	@Override
	public Project insertOrUpdateContestTemporaliy(Long projectId, Long userId, String title, Long[] projectSectorTypeIds, String categoryOfBusiness,
												   String majorProduct, String description, Integer quantityPage, String descriptionPerPage,
												   String[] referenceWebUrl, MultipartFile referenceFile, String workingWebUrl,
												   String corporateNameOrCatchPhrase, String priorityTone, Feeling[] feelings,
												   boolean proceedProjectAfterProject) {
		Project project = (projectId == null?new Project():projectRepository.getOne(projectId));
		project.setTitle(title);
		project.setPostingClient(userRepository.getOne(userId));
		project.setDescription(description);
		project.setQuantityPage(quantityPage);
		project.setCorporateNameOrCatchPhrase(corporateNameOrCatchPhrase);
		project.setCategoryOfBusiness(categoryOfBusiness);
		project.setMajorProduct(majorProduct);
		project.setDescriptionPerPage(descriptionPerPage);
		project.setPriorityTone(priorityTone);
		project.setWorkingWebUrl(workingWebUrl);
		project.setReferenceWebUrl(String.join(",", referenceWebUrl));
		project.setProceedProjectAfterContest(proceedProjectAfterProject);
		if (feelings != null) {
			EnumSet<Feeling> temp = EnumSet.noneOf(Feeling.class);
			temp.addAll(Arrays.asList(feelings));
			project.setFeelings(temp);
		}
		if (project.getId() == null) {
			project.setStatus(Status.TEMP);
		}
		project.setProjectType(Project.Type.CONTEST);
		if (referenceFile != null && !referenceFile.isEmpty()) {
			project.setProjectDescriptionFileName(referenceFile.getOriginalFilename());
			project.setProjectDescriptionFileUrl(storageService.storeFile(referenceFile));
		}
		project = projectRepository.save(project);

		Set<ProjectCategory> projectCategories = new LinkedHashSet<>();
		int prize = 0;
		if (projectSectorTypeIds != null) {
			for (Long projectSectorTypeId : projectSectorTypeIds) {
				if (contestSectorRepository.countByContestIdAndContestSectorTypeId(projectId, projectSectorTypeId) > 0) {
					continue;
				}
				ContestSector contestSector = new ContestSector();
				ContestSectorType contestSectorType = contestSectorTypeRepository.getOne(projectSectorTypeId);
				contestSector.setContest(project);
				contestSector.setContestSectorType(contestSectorType);
				contestSector.setCreatedAt(LocalDateTime.now());
				contestSectorRepository.save(contestSector);

				List<ContestSectorMetaCategory> categories = contestSectorMetaCategoryRepository.findByContestSectorMetaTypeId(contestSectorType.getContestSectorMetaType().getId());

				for (ContestSectorMetaCategory category : categories) {
					ProjectCategory projectCategory = new ProjectCategory();
					projectCategory.setCategory(category.getCategory());
					projectCategory.setProject(project);
					projectCategories.add(projectCategory);
				}

				prize += contestSectorType.getMinAmountOfPrice();
			}
		}
		if (projectId == null) {
			project.setMinPrize(prize);
		}
		projectCategoryRepository.saveAll(projectCategories);

		return project;
	}

	@Transactional
	@Override
	public void increaseHits(Long projectId) {
		Project project = projectRepository.getOne(projectId);
		project.setHits(project.getHits() + 1);
		projectRepository.save(project);
	}

	@Transactional
	@Override
	public void cancel(Long projectId) {
		Project item = projectRepository.getOne(projectId);

		item.setCancelAt(LocalDateTime.now());
		item.setStatus(Project.Status.CANCELLED);
		item.setCancelReason("포스팅 기간 만료 및 미선정 - 시스템에 의한 자동 취소");
		projectRepository.save(item);

		for (ProjectBid projectBid: projectBidRepository.findByProjectIdAndBidStatusIn(item.getId(), Arrays.asList(ProjectBid.BidStatus.APPLY))) {
			projectBid.setBidStatus(ProjectBid.BidStatus.CANCEL);
			projectBid.setFailedAt(LocalDateTime.now());
			projectBidRepository.save(projectBid);
		}
		refund(item.getId());
	}

	@Override
	public int getRemainEscrow(long userId) {
		return escrowLogRepository.findByDepositUserIdAndType(userId, EscrowLog.Type.DEPOSIT).stream().mapToInt(EscrowLog::getAmount).sum()
				- escrowLogRepository.findByWithdrawalUserIdAndType(userId, EscrowLog.Type.WITHDRAWAL).stream().mapToInt(EscrowLog::getAmount).sum();
	}

	@Override
	public List<ProjectComment> getProjectCommentForFreelancer(Long projectId, Long clientUserId, Long freelancerId) {
		return this.projectCommentRepository.findAll(ProjectCommentSpecifications.filterForFreelancer(projectId, clientUserId, freelancerId), Sort.by(Sort.Direction.ASC, "createdAt"));
	}

}
