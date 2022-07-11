package com.freelancerk.service;

import com.freelancerk.domain.ContestEntryFile;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.ProjectProposition;
import com.freelancerk.domain.TaxType;
import com.freelancerk.policy.PaymentPolicy;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectBidService {
	Page<ProjectBid> getProjectBidPages(Long userId, ProjectBid.BidStatus status, int currentPageNumber, int itemPerPage, String sortBy, boolean isSortDesc);
	int getTotalCountOfProjectBid(Long userId, ProjectBid.BidStatus status);

	Page<ProjectProposition> getProposedProjectBidPages(Long userId, int currentPageNumber, int itemPerPage, String sortBy, boolean isSortDesc);
	int getTotalCountOfProposedProjectBid(Long userId);

	List<ContestEntryFile> getAllContestEntryFile(Long contestEntryId);
	int getAvgOfProjectBidMoney(Long projectId);
	float getAvgOfProjectBidCareerYear(Long projectId);

	
	ProjectBid getMyContestEntry(Long userId, Long contestId);

	void pickBidAndStartProject(long projectId, long projectBidId, int pickedAmount);
	
	// 컨테스트 입찰 참여 1단계 - 파일 등록
	void addContestFile(ProjectBid projectBid, String fileTitle, MultipartFile demoFile, boolean isRepresentative);
	void addContestFile(ProjectBid projectBid, ContestEntryFile.Type fileType, String fileTitle, MultipartFile demoFile, boolean isRepresentative);

	int calculatePrice(Long userId, PaymentPolicy.ContestBid[] contestBidOptions, Integer usedPoint);
	ProjectBid getInfo(Long contestEntryId);
	void cancel(Long userId, Long contestEntryId);
	void update(Long userId, Long contestEntryId, MultipartFile[] imageFiles);

	List<ContestEntryFile> getAllContestEntryRepresentativeFileList(Long contestId);
}
