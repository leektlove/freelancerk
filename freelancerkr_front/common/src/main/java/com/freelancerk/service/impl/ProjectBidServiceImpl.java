package com.freelancerk.service.impl;

import com.freelancerk.FileUtil;
import com.freelancerk.Policy;
import com.freelancerk.domain.*;
import com.freelancerk.domain.ContestEntryFile.Type;
import com.freelancerk.domain.Project.Status;
import com.freelancerk.domain.ProjectBid.BidStatus;
import com.freelancerk.domain.repository.*;
import com.freelancerk.exception.ExceedModificationsLimitException;
import com.freelancerk.exception.UserNotMatchedException;
import com.freelancerk.policy.PaymentPolicy;
import com.freelancerk.policy.PaymentPolicy.ContestBid;
import com.freelancerk.service.ProjectBidService;
import com.freelancerk.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Log
@Service
public class ProjectBidServiceImpl implements ProjectBidService {
	private ProjectRepository projectRepository;
	private ProjectBidRepository projectBidRepository;
	private ProjectLogRepository projectLogRepository;
	private ProjectPropositionRepository projectPropositionRepository;

	private UserService userService;
	private StorageServiceImpl storageService;
	private BankTypeRepository bankTypeRepository;
	private ContestEntryFileRepository contestEntryFileRepository;


	@Autowired
	public ProjectBidServiceImpl(
			ProjectRepository projectRepository, 
			ProjectBidRepository projectBidRepository,
			ProjectLogRepository projectLogRepository,
			ProjectPropositionRepository projectPropositionRepository,
			UserService userService,
			BankTypeRepository bankTypeRepository,
			ContestEntryFileRepository contestEntryFileRepository, 
			StorageServiceImpl storageService) {

		this.projectRepository = projectRepository;
		this.projectBidRepository = projectBidRepository;
		this.projectLogRepository = projectLogRepository;
		this.projectPropositionRepository = projectPropositionRepository;
		
		this.userService = userService;
		this.bankTypeRepository = bankTypeRepository;
		this.contestEntryFileRepository = contestEntryFileRepository;
		this.storageService = storageService;
	}

	/**
	 * 입찰 목록을 입찰 상태 및 정렬 방식에 따라 가져옵니다.
	 */
	// 진행중인 입찰, 성공한 입찰, 실패한 입찰의 경우
	@Override
	public Page<ProjectBid> getProjectBidPages(Long userId, BidStatus status, int currentPageNumber, int itemPerPage,
			String sortBy, boolean isSortDesc) {
		Sort.Direction dir = Sort.Direction.DESC;
		if (!isSortDesc)
			dir = Sort.Direction.ASC;
		return projectBidRepository.findByParticipantIdAndBidStatus(userId, status,
				PageRequest.of(currentPageNumber, itemPerPage, new Sort(dir, sortBy)));
	}
	// 입찰 목록 총 갯수
	@Override
	public int getTotalCountOfProjectBid(Long userId, BidStatus status) {
		return projectBidRepository.countByParticipantIdAndBidStatus(userId, status);
	}
	
	// 제안받은 입찰의 경우
	@Override
	public Page<ProjectProposition> getProposedProjectBidPages(Long userId, int currentPageNumber, int itemPerPage,
			String sortBy, boolean isSortDesc) {
		Sort.Direction dir = Sort.Direction.DESC;
		if (!isSortDesc)
			dir = Sort.Direction.ASC;
		return projectPropositionRepository.findByFreelancerId(userId, PageRequest.of(currentPageNumber, itemPerPage, new Sort(dir, sortBy)));
	}
	
	// 제안받은 입찰 총 갯수
	@Override
	public int getTotalCountOfProposedProjectBid(Long userId) {
		return projectPropositionRepository.countByFreelancerId(userId);
	}
	
	/**
	 * 평균 입찰가를 가져옵니다.
	 */
	@Override
	public int getAvgOfProjectBidMoney(Long projectId) {
		return this.projectBidRepository.avgProjectBidMoney(projectId);
	}

	/**
	 * 입찰자들의 평균 경력을 가져옵니다.
	 */
	@Override
	public float getAvgOfProjectBidCareerYear(Long projectId) {
		return this.projectBidRepository.avgParticipantsCareerYear(projectId);
	}
	
	/**
	 * 컨테스트에 지원한 자신의 모든 파일을 가져옵니다.
	 */
	@Override
	public List<ContestEntryFile> getAllContestEntryFile(Long contestEntryId) {
		return this.contestEntryFileRepository.findByContestEntryId(contestEntryId);
	}

	/**
	 * 컨테스트 파일 등록
	 */
	
	@Override
	public void addContestFile(ProjectBid projectBid, Type fileType, String fileTitle, MultipartFile demoFile, boolean isRepresentative) {
		ContestEntryFile entryFile = new ContestEntryFile();
		entryFile.setContestEntry(projectBid);
		entryFile.setFileType(fileType);
		entryFile.setFileTitle(fileTitle);
		entryFile.setFileOriginName(demoFile.getOriginalFilename());
		entryFile.setFileUrl(storageService.store(demoFile));
		entryFile.setRepresentative(isRepresentative);
		entryFile.setFileType(FileUtil.getFileType(entryFile.getFileUrl()));
		entryFile = FileUtil.setMetaInfo(entryFile, entryFile.getFileUrl());
		contestEntryFileRepository.save(entryFile);
	}
	
	@Override
	public void addContestFile(ProjectBid projectBid, String fileTitle, MultipartFile demoFile,	boolean isRepresentative) {
		ContestEntryFile entryFile = new ContestEntryFile();
		entryFile.setContestEntry(projectBid);
		entryFile.setFileTitle(fileTitle);
		entryFile.setFileOriginName(demoFile.getOriginalFilename());
		entryFile.setFileUrl(storageService.store(demoFile));
		entryFile.setRepresentative(isRepresentative);
		entryFile.setFileType(FileUtil.getFileType(entryFile.getFileUrl()));
		entryFile = FileUtil.setMetaInfo(entryFile, entryFile.getFileUrl());
		contestEntryFileRepository.save(entryFile);
	}

	@Override
	public int calculatePrice(Long userId, ContestBid[] contestBidOptions, Integer usedPoint) {
		int count = projectBidRepository.countByParticipantIdAndApplyAtAfter(userId,
				LocalDateTime.now().withDayOfMonth(1));
		int totalPrice = 0;
		if (Policy.CONTEST_ENTRY_COUNT_FREE_MONTHLY <= count) {
			totalPrice += 10000;
		}
		for (PaymentPolicy.ContestBid bidOption : contestBidOptions) {
			totalPrice += bidOption.getUnitPrice();
		}
		totalPrice -= usedPoint;
		totalPrice = (int) (totalPrice + totalPrice * 0.1);
		return totalPrice;
	}

	@Override
	public ProjectBid getInfo(Long contestEntryId) {
		return projectBidRepository.getOne(contestEntryId);
	}

	@Override
	public void cancel(Long userId, Long contestEntryId) {
		contestEntryFileRepository.deleteByContestEntryId(contestEntryId);
		ProjectBid contestEntry = projectBidRepository.getOne(contestEntryId);
		if (!contestEntry.getParticipant().getId().equals(userId)) {
			throw UserNotMatchedException.getInstance();
		}
		projectBidRepository.deleteById(contestEntryId);

	}

	@Transactional
	@Override
	public void update(Long userId, Long contestEntryId, MultipartFile[] imageFiles) {
		ProjectBid contestEntry = projectBidRepository.getOne(contestEntryId);
		if (!contestEntry.getParticipant().getId().equals(userId)) {
			throw UserNotMatchedException.getInstance();
		}

		if (contestEntry.getNumberOfModifications() >= Policy.CONTEST_ENTRY_MODIFICATION_LIMIT) {
			throw ExceedModificationsLimitException.getInstance();
		}

		contestEntryFileRepository.deleteByContestEntryId(contestEntryId);
		int i = 0;
		for (MultipartFile file : imageFiles) {
			ContestEntryFile entryFile = new ContestEntryFile();
			entryFile.setContestEntry(contestEntry);
			entryFile.setFileUrl(storageService.store(file));
			entryFile.setRepresentative(i == 0);
			entryFile.setFileType(FileUtil.getFileType(entryFile.getFileUrl()));
			entryFile = FileUtil.setMetaInfo(entryFile, entryFile.getFileUrl());
			contestEntryFileRepository.save(entryFile);
			i++;
		}
	}

	@Override
	public List<ContestEntryFile> getAllContestEntryRepresentativeFileList(Long contestId) {
		return contestEntryFileRepository.findByContestEntryProjectIdAndContestEntryInvalidFalseAndRepresentativeTrue(contestId);
	}

	@Override
	public ProjectBid getMyContestEntry(Long participantId, Long contestId) {
		return this.projectBidRepository.findTop1ByParticipantIdAndProjectIdAndBidStatusNotOrderByCreatedAtDesc(participantId, contestId, BidStatus.CANCEL);
	}

	@Override
	public void pickBidAndStartProject(long projectId, long projectBidId, int pickedAmount) {
		ProjectBid projectBid = projectBidRepository.getOne(projectBidId);
		Project project = projectRepository.getOne(projectId);
		project.setContractedFreelancer(projectBid.getParticipant());
		project.setStatus(project.isUseEscrow()?Project.Status.IN_PROGRESS: Status.COMPLETED);
		project.setSuccessBidPrice(pickedAmount);
		project.setContractPrice(pickedAmount);
		project.setSuccessBidAt(LocalDateTime.now());
		project.setStartAt(LocalDateTime.now());
		project.setContractAt(LocalDateTime.now());
		if (!project.isUseEscrow()) {
			project.setCompletedAt(LocalDateTime.now());
		}
		project = projectRepository.save(project);

		projectBid.setBidStatus(ProjectBid.BidStatus.PICKED);
		projectBid.setSuccessBidAt(LocalDateTime.now());
		projectBid.setPickedAmountOfMoney(pickedAmount);
		projectBidRepository.save(projectBid);

		List<ProjectBid> otherProjectBids = projectBidRepository.findByProjectIdAndIdNot(projectId, projectBidId);
		for (ProjectBid item: otherProjectBids) {
			item.setBidStatus(BidStatus.FAILED);
			item.setFailedAt(LocalDateTime.now());

		}
		projectBidRepository.saveAll(otherProjectBids);

		ProjectLog projectLog = new ProjectLog();
		projectLog.setCreatedAt(LocalDateTime.now());
		projectLog.setProject(project);
		projectLog.setDesc(project.isUseEscrow()?"프로젝트 시작 - 낙찰":"프로젝트 낙찰 및 종료 (에스크로 사용 안함)");
		projectLog.setUser(project.getPostingClient());
		projectLog.setType(project.isUseEscrow()?ProjectLog.Type.START:ProjectLog.Type.END);
		projectLogRepository.save(projectLog);
	}
}
