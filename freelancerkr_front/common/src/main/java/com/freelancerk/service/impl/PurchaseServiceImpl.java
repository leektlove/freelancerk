package com.freelancerk.service.impl;

import com.freelancerk.TimeUtil;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.exception.NotFreePostingException;
import com.freelancerk.service.ContestEntryTicketService;
import com.freelancerk.service.PointService;
import com.freelancerk.service.PurchaseService;
import com.freelancerk.util.OptionDiscountCalculator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class PurchaseServiceImpl implements PurchaseService {

	private PointService pointService;
	private UserRepository userRepository;
	private ProjectRepository projectRepository;
	private PurchaseRepository purchaseRepository;
	private PickMeUpRepository pickMeUpRepository;
	private ContestEntryTicketService contestEntryTicketService;
	private ProjectPointLogRepository projectPointLogRepository;
	private ProjectItemTicketRepository projectItemTicketRepository;
	private ContestEntryTicketRepository contestEntryTicketRepository;
	private ProjectItemTicketLogRepository projectItemTicketLogRepository;
	private FreelancerPayProductRepository freelancerPayProductRepository;
	private ContestEntryTicketLogRepository contestEntryTicketLogRepository;
	private ProjectProductItemTypeRepository projectProductItemTypeRepository;
	private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;

	@Autowired
	public PurchaseServiceImpl(UserRepository userRepository, ProjectRepository projectRepository,
							   PurchaseRepository purchaseRepository, PointService pointService,
							   ProjectProductItemTypeRepository projectProductItemTypeRepository,
							   PickMeUpRepository pickMeUpRepository, ContestEntryTicketService contestEntryTicketService,
							   ProjectItemTicketRepository projectItemTicketRepository,
							   ProjectPointLogRepository projectPointLogRepository, FreelancerProductItemTypeRepository freelancerProductItemTypeRepository,
							   ProjectItemTicketLogRepository projectItemTicketLogRepository,
							   ContestEntryTicketLogRepository contestEntryTicketLogRepository,
							   ContestEntryTicketRepository contestEntryTicketRepository,
							   FreelancerPayProductRepository freelancerPayProductRepository) {
		this.pointService = pointService;
		this.userRepository = userRepository;
		this.projectRepository = projectRepository;
		this.purchaseRepository = purchaseRepository;
		this.pickMeUpRepository = pickMeUpRepository;
		this.contestEntryTicketService = contestEntryTicketService;
		this.projectPointLogRepository = projectPointLogRepository;
		this.projectItemTicketRepository = projectItemTicketRepository;
		this.contestEntryTicketRepository = contestEntryTicketRepository;
		this.projectItemTicketLogRepository = projectItemTicketLogRepository;
		this.freelancerPayProductRepository = freelancerPayProductRepository;
		this.contestEntryTicketLogRepository = contestEntryTicketLogRepository;
		this.projectProductItemTypeRepository = projectProductItemTypeRepository;
		this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
	}

	@Override
	public String makeAdministrationId(Long userId, Long projectId, Integer optionAmountOfMoney, Integer supplyAmountOfMoney, Integer totalDiscountOptionPrice,
									   Integer vatAmountOfMoney, Integer totalAmountOfMoney, Purchase.Type type,
									   String optionIdsCountMap, Integer usePoint, String mode) {
		String adminUid = UUID.randomUUID().toString();
		Purchase purchase = new Purchase();
		purchase.setUser(userRepository.getOne(userId));
		purchase.setProject(projectRepository.getOne(projectId));
		purchase.setType(type);
		purchase.setUsedPoints(usePoint == null?0:usePoint);
		purchase.setTotalDiscountOptionPrice(totalDiscountOptionPrice == null?0:totalDiscountOptionPrice);
		purchase.setTotalAmountOfMoney(totalAmountOfMoney);
		purchase.setSupplyAmountOfMoney(supplyAmountOfMoney);
		purchase.setVatAmountOfMoney(vatAmountOfMoney);
		purchase.setChargedOptions(optionIdsCountMap);
		purchase.setChargedOptionsAmountOfMoney(optionAmountOfMoney == null?0:optionAmountOfMoney);
		purchase.setStatus(Purchase.Status.REQUESTED);
		purchase.setAdministrationId(adminUid);
		purchase.setOptionExtend("EXTEND".equalsIgnoreCase(mode));
		purchaseRepository.save(purchase);
		return adminUid;
	}

	// project, contest
	@Transactional
	@Override
	public Purchase confirmPurchaseViaCallback(String administrationId, String impUid, boolean postingPeriodExtend, String postingEndAt) {
		Purchase purchase = purchaseRepository.findByAdministrationId(administrationId);

		if (StringUtils.isEmpty(impUid) && purchase.getSupplyAmountOfMoney() > 0) {
		    throw NotFreePostingException.getInstance();
        }

		purchase.setStatus(Purchase.Status.COMPLETED);
		purchase.setImpUid(impUid);

		Project project = purchase.getProject();

		Type type = new TypeToken<Map<String, Integer>>(){}.getType();
		Map<String, Integer> parsedOptionCountMap = new Gson().fromJson(purchase.getChargedOptions(), type);
		LocalDateTime lastDay = LocalDateTime.now();

		int optionCount = 0;
		Set<Long> optionIds = new HashSet<>();
		for (String optionId: parsedOptionCountMap.keySet()) {
			Integer weeks = parsedOptionCountMap.get(optionId);
			optionIds.add(Long.parseLong(optionId));
			optionCount+=weeks;
		}

		purchase.setNumberOfOptions(optionCount);

		ProjectItemTicket previousBasicInternalTicket = null;
		ProjectItemTicket previousBasicExternalTicket = null;

		if (Arrays.asList(Purchase.Type.CONTEST, Purchase.Type.PROJECT).contains(purchase.getType())) {
			project.setLastPurchasedAt(LocalDateTime.now());
			project.setStatus(Project.Status.POSTED);
			int previousTicketCount = projectItemTicketRepository.countByProjectId(project.getId());
			if (previousTicketCount == 0) {
				project.setStartAt(LocalDateTime.now());
				project.setPostingStartAt(LocalDateTime.now());
			} else {
				previousBasicInternalTicket = projectItemTicketRepository.findByProjectIdAndProjectProductItemTypeCodeAndProjectProductItemTypeProjectType(
						purchase.getProject().getId(), ProjectProductItemType.Code.INTERNAL, Project.Type.PROJECT
				);
				previousBasicExternalTicket = projectItemTicketRepository.findByProjectIdAndProjectProductItemTypeCodeAndProjectProductItemTypeProjectType(
						purchase.getProject().getId(), ProjectProductItemType.Code.EXTERNAL, Project.Type.PROJECT
				);
			}
		}

		purchase = purchaseRepository.save(purchase);

		pointService.getRidOfPointsFromClient(purchase.getUser().getId(), purchase.getUsedPoints(), "포인트 사용", purchase, false);

		if (project.getPostingEndAt().isBefore(lastDay)) {
			project.setPostingEndAt(lastDay);
		}

		if (StringUtils.isNotEmpty(postingEndAt)) {
			project.setPostingEndAt(LocalDateTime.of(TimeUtil.convertStrToLocalDate(postingEndAt), LocalTime.now()));
		}

		Set<Long> processedIdSet = new HashSet<>();
		int totalOptionMoney = 0;
		for (String optionId: parsedOptionCountMap.keySet()) {
			if (processedIdSet.contains(Long.parseLong(optionId))) {
				continue;
			}
			processedIdSet.add(Long.parseLong(optionId));
			Integer weeks = parsedOptionCountMap.get(optionId);
			ProjectItemTicket projectItemTicket = projectItemTicketRepository.findTop1ByProjectProductItemTypeIdAndProjectId(
					Long.parseLong(optionId), purchase.getProject().getId());

			ProjectProductItemType itemType = projectProductItemTypeRepository.getOne(Long.parseLong(optionId));

			if (ProjectProductItemType.Code.ESCROW.equals(itemType.getCode())) {
				project.setUseEscrow(true);
			}

			if (itemType.getMountOfMoneyUnit() > 0) {
				if (projectItemTicket == null) {
					projectItemTicket = new ProjectItemTicket();
					projectItemTicket.setExpiredAt(LocalDateTime.now().plusWeeks(weeks));
				} else {
					projectItemTicket.setExpiredAt(projectItemTicket.getExpiredAt().plusWeeks(weeks));
				}
			} else { // 무료 옵션의 기한은 포스팅 만료 날짜와 같음
				if (projectItemTicket == null) {
					projectItemTicket = new ProjectItemTicket();
				}
				projectItemTicket.setExpiredAt(project.getPostingEndAt());
			}
			projectItemTicket.setProject(purchase.getProject());
			projectItemTicket.setProjectProductItemType(itemType);
			projectItemTicketRepository.save(projectItemTicket);

			ProjectItemTicketLog log = new ProjectItemTicketLog();
			log.setProject(purchase.getProject());
			log.setProjectProductItemType(projectItemTicket.getProjectProductItemType());
			log.setOptionPrice(projectItemTicket.getProjectProductItemType().getMountOfMoneyUnit()*weeks);
			log.setOptionCount(weeks);
			log.setExpiredAt(LocalDateTime.now().plusWeeks(weeks));
			log.setPurchase(purchase);
			projectItemTicketLogRepository.save(log);

			totalOptionMoney+=projectItemTicket.getProjectProductItemType().getMountOfMoneyUnit()*weeks;

			if (lastDay.isBefore(projectItemTicket.getExpiredAt())) {
				lastDay = projectItemTicket.getExpiredAt();
			}
		}

		changeDefaultOptionPeriod(project);

		projectRepository.save(project);

		int point = 0;

		if (Purchase.Type.CONTEST.equals(purchase.getType())) {
			point = calculatePoint(purchase.getChargedOptionsAmountOfMoney(), User.Role.ROLE_CLIENT);
		} else {
			point = calculatePoint(purchase.getSupplyAmountOfMoney(), User.Role.ROLE_CLIENT);
		}

		if (point == 0 && purchase.getUsedPoints() == 0) {
			return purchase;
		}
		pointService.givePointsToClient(purchase.getUser().getId(), point, "결제 포인트 지급", purchase);

		ProjectPointLog projectPointLog = new ProjectPointLog();
		projectPointLog.setAddedPoint(point);
		projectPointLog.setUsePoint(purchase.getUsedPoints());
		projectPointLog.setAddedPointExpiredAt(LocalDateTime.now().plusYears(1));
		projectPointLog.setCreatedAt(LocalDateTime.now());
		projectPointLog.setProject(purchase.getProject());
		projectPointLog.setUser(purchase.getUser());
		projectPointLog.setPurchase(purchase);
		projectPointLogRepository.save(projectPointLog);

		return purchase;
	}

	private void changeDefaultOptionPeriod(Project project) {
		List<ProjectItemTicket>  projectItemTickets
				= projectItemTicketRepository.findByProjectIdAndProjectProductItemTypeCategoryAndProjectProductItemTypeProjectType(project.getId(), ProjectProductItemType.Category.INTERNAL, project.getProjectType());
		ProjectItemTicket defaultInternalType
				= projectItemTicketRepository.findByProjectIdAndProjectProductItemTypeCodeAndProjectProductItemTypeProjectType(project.getId(), ProjectProductItemType.Code.INTERNAL, project.getProjectType());
		LocalDateTime lastExpiredAt = defaultInternalType.getExpiredAt();

		for (ProjectItemTicket item : projectItemTickets) {
			if (item.getExpiredAt().isAfter(lastExpiredAt)) {
				lastExpiredAt = item.getExpiredAt();
			}
		}

		projectItemTickets
				= projectItemTicketRepository.findByProjectIdAndProjectProductItemTypeCategoryAndProjectProductItemTypeProjectType(project.getId(), ProjectProductItemType.Category.EXTERNAL, project.getProjectType());
		ProjectItemTicket defaultExternalType
				= projectItemTicketRepository.findByProjectIdAndProjectProductItemTypeCodeAndProjectProductItemTypeProjectType(project.getId(), ProjectProductItemType.Code.EXTERNAL, project.getProjectType());


		if (defaultExternalType != null) {
			for (ProjectItemTicket item : projectItemTickets) {
				if (item.getExpiredAt().isAfter(lastExpiredAt)) {
					lastExpiredAt = item.getExpiredAt();
				}
			}
		}

		if (!projectItemTickets.isEmpty() && defaultExternalType == null) {
			defaultExternalType = new ProjectItemTicket();
			defaultExternalType.setProject(project);
			defaultExternalType.setProjectProductItemType(projectProductItemTypeRepository.findByCodeAndProjectType(ProjectProductItemType.Code.EXTERNAL, Project.Type.PROJECT));
			defaultExternalType.setCreatedAt(LocalDateTime.now());
			defaultExternalType.setExpiredAt(lastExpiredAt);
			projectItemTicketRepository.save(defaultExternalType);
		}

		ProjectItemTicket escrowTypeProductTicket = projectItemTicketRepository.findByProjectIdAndProjectProductItemTypeCodeAndProjectProductItemTypeProjectType(
				project.getId(), ProjectProductItemType.Code.ESCROW, Project.Type.PROJECT);

		defaultInternalType.setExpiredAt(lastExpiredAt);
		projectItemTicketRepository.save(defaultInternalType);
		if (defaultExternalType != null) {
			defaultExternalType.setExpiredAt(lastExpiredAt);
			projectItemTicketRepository.save(defaultExternalType);
		}
		if (escrowTypeProductTicket != null) {
			escrowTypeProductTicket.setExpiredAt(lastExpiredAt);
			projectItemTicketRepository.save(escrowTypeProductTicket);
		}
		project.setPostingEndAt(lastExpiredAt);
	}

	@Transactional
	@Override
	public Purchase confirmPurchaseContestEntry(String administrationId, String impUid, String merchantUid, ProjectBid projectBid) {
		Purchase purchase = purchaseRepository.findByAdministrationId(administrationId);
		purchase.setStatus(Purchase.Status.COMPLETED);
		purchase.setProjectBid(projectBid);
		purchase.setMerchantUid(merchantUid);
		purchase.setAdministrationId(administrationId);
		if (StringUtils.isEmpty(impUid) && purchase.getSupplyAmountOfMoney() > 0) {
			throw NotFreePostingException.getInstance();
		}

		List<FreelancerPayProduct> payProductList = new Gson().fromJson(purchase.getChargedOptions(), new TypeToken<ArrayList<FreelancerPayProduct>>(){}.getType());
		int totalOptionPrice = 0;
		int chargedOptionCount = 0;
		for (FreelancerPayProduct payProduct: payProductList) {
			if (payProduct.isUsedInPickMeUp()) {
				continue;
			}
			totalOptionPrice += (payProduct.getAmount() * payProduct.getCount());
			chargedOptionCount += payProduct.getCount();
		}
		int discountPrice = OptionDiscountCalculator.getOptionDiscountAmountForFreelancer(chargedOptionCount, totalOptionPrice);
		int givePoint = calculatePoint(totalOptionPrice - discountPrice,
				User.Role.ROLE_FREELANCER);
		purchase.setChargedOptionsAmountOfMoney(totalOptionPrice);
		purchase.setTotalDiscountOptionPrice(discountPrice);
		purchase.setNumberOfOptions(chargedOptionCount);
		purchase = purchaseRepository.save(purchase);

		pointService.getRidOfPointsFromFreelancerForContestEntry(purchase.getUser().getId(), purchase.getUsedPoints(), purchase);

		contestEntryTicketService.makeTicket(projectBid, purchase, payProductList);
		pointService.givePointsToFreelancerForContestEntry(purchase.getUser(), givePoint, "컨테스트 제출", purchase);

		return purchase;
	}

	@Override
	public Purchase confirmPurchasePickMeUp(Long userId, Purchase purchaseParam) {
		Purchase purchase = null;
		if (purchaseParam.getId() == null) {
			purchase = new Purchase();
		} else {
			purchase = purchaseRepository.getOne(purchaseParam.getId());
			purchase.setCreatedAt(purchaseParam.getCreatedAt());
			purchase.setUpdatedAt(purchaseParam.getUpdatedAt());
		}

		purchase.setStatus(Purchase.Status.COMPLETED);
		purchase.setUser(userRepository.getOne(userId));
		purchase.setAdministrationId(purchaseParam.getAdministrationId());
		purchase.setChargedOptions(purchaseParam.getChargedOptions());
		purchase.setMerchantUid(purchaseParam.getMerchantUid());
		purchase.setImpUid(purchaseParam.getImpUid());
		purchase.setType(purchaseParam.getType());
		purchase.setChargedOptionsAmountOfMoney(purchaseParam.getChargedOptionsAmountOfMoney());
		purchase.setUsedPoints(purchaseParam.getUsedPoints());
		purchase.setSupplyAmountOfMoney(purchaseParam.getSupplyAmountOfMoney());
		purchase.setVatAmountOfMoney(purchaseParam.getVatAmountOfMoney());
		purchase.setTotalAmountOfMoney(purchaseParam.getTotalAmountOfMoney());
		purchase.setTotalDiscountOptionPrice(purchaseParam.getTotalDiscountOptionPrice());
		purchase.setNumberOfOptions(purchaseParam.getNumberOfOptions());
		purchase.setPickMeUp(pickMeUpRepository.getOne(purchaseParam.getSelectedPickMeUpId()));

		return purchaseRepository.save(purchase);
	}

	private int calculatePoint(int totalOptionMoney, User.Role role) {
		if (User.Role.ROLE_CLIENT.equals(role)) return (int) (totalOptionMoney*0.05);
		return (int) (totalOptionMoney*0.10);
	}
}
