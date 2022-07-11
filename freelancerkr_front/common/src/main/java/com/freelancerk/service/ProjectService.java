package com.freelancerk.service;

import com.freelancerk.domain.*;
import com.freelancerk.vo.TotalPagesInfoVO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ProjectService {

	/**
	 * 프로젝트
	 */

	// 프리랜서
	Page<ProjectFavorite> getFreelancerProjectFavoritePages(Long freelancerId, String searchWord, int currentPageNumber, LocalDateTime startDate, LocalDateTime endDate);
	TotalPagesInfoVO getTotalFreelancerProjectFavoritePages(Long freelancerId, String searchWord, int currentPageNumber, LocalDateTime startDate, LocalDateTime endDate);
	
	// 프리랜서용
	List<ProjectComment> getProjectCommentForFreelancer(Long projectId, Long clientUserId, Long freelancerId);
	
	/**
	 * 프로젝트 계약서 파일
	 */
	List<ProjectContractFile> getProjectContractFiles(Long projectId);
	List<ProjectContractFile> getProjectContractFilesByFreelancer(Long projectId);
	List<ProjectContractFile> getProjectContractFilesByClient(Long projectId);
	
	
	/**
	 * 프로젝트 종료
	 */
	void setProjectComplete(Long projectId);

	int calculateOptionPrice(List<ProjectProductItemType> options, Integer[] optionCount, Project.Type projectType);


	void update(Long projectId, Long userId, String title, String description, MultipartFile logoImageFile, String modifiedLogoImage, List<Long> categoryIds,
				Project.ExpectedPeriod expectedPeriod, String expectedPeriodInput, MultipartFile projectDescriptionFile,
				Integer defaultMoney, Float incentiveFrom, Float incentiveTo, Integer budgetFrom, Integer budgetTo, Integer numberOfPersons, Project.PayCriteria payCriteria, Project.PayMean payMean, Project.WorkPlace workPlace,
				String workPlaceAddress1, String workPlaceAddress2, Boolean useEscrow, LocalDate endAt, MultipartFile[] projectFiles);

	Project createOrUpdateTemporarily(Long projectId, Long userId, String title, String description, MultipartFile logoImageFile, String modifiedLogoImage, List<Long> categoryIds,
                                      Project.ExpectedPeriod expectedPeriod, String expectedPeriodInput, MultipartFile projectDescriptionFile,
                                      Integer defaultMoney, Float incentiveFrom, Float incentiveTo, Integer budgetFrom, Integer budgetTo, Integer numberOfPersons, Project.PayCriteria payCriteria, Project.PayMean payMean, Project.WorkPlace workPlace,
                                      String workPlaceAddress1, String workPlaceAddress2, Boolean useEscrow, Long bankForReceivingPaymentId, String bankAccountForReceivingPayment, String bankAccountName, LocalDate startAt, LocalDate endAt, MultipartFile[] projectFiles);

	void createAndStartWithFreelancer(Long sessionUserId, Project.Type type, String title, String description, Integer price,
                                      MultipartFile projectDescriptionFile, Long freelancerId, Long projectId);

	Project getById(Long projectId);

	void insertContest(Long userId, Long projectId, Integer depositMoney, Integer minimumPrice, Integer incentivePrice,
			Integer prizeFor1st, Integer prizeFor2nd, Integer prizeFor3rd, boolean payForFeeToFreelancer,
			Integer usedPoint, Integer[] optionIds, Integer[] optionCount, Integer[] packIds,
			Integer[] ProjectOptionPackCount);

	void updateContest(Long userId, Long projectId, String title, String ProjectSectorName, String categoryOfBusiness,
			String majorProduct, String description, Integer quantityPage, String descriptionPerPage,
			String[] referenceWebUrl, MultipartFile[] referenceFile, String workingWebUrl,
			String corporateNameOrCatchPhrase, Project.Status status, Integer depositMoney, String priorityTone,
			Project.Feeling[] feelings, Boolean proceedProjectAfterProject, Integer minimumPrice,
			Integer incentivePrice, Integer prizeFor1st, Integer prizeFor2nd, Integer prizeFor3rd,
			Boolean payForFeeToFreelancer);

	int refundDepositMoneyToClient(Long projectId);

	int refundDepositMoneyToFreelancer(Long projectId);

	int refundTotalPrizeToFreelancer(Long projectId);

	int refundTotalPrizeToClient(Long projectId);

	void pick(Long projectId, Long[] contestEntryId);

	void refund(Long projectId);

	Project insertOrUpdateContestTemporaliy(Long projectId, Long userId, String title, Long[] ProjectSectorTypeId, String categoryOfBusiness,
											String majorProduct, String description, Integer quantityPage, String descriptionPerPage,
											String[] referenceWebUrl, MultipartFile referenceFile, String workingWebUrl,
											String corporateNameOrCatchPhrase, String priorityTone, Project.Feeling[] feelings,
											boolean proceedProjectAfterProject);


	void increaseHits(Long projectId);

	void cancel(Long projectId);

	int getRemainEscrow(long userId);
}
