package com.freelancerk.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.ContestEntryFile;
import com.freelancerk.domain.ContestEntryTicket;
import com.freelancerk.domain.ContestEntryTicketLog;
import com.freelancerk.domain.FreelancerPayProduct;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.ProjectBidComment;
import com.freelancerk.domain.ProjectProposition;
import com.freelancerk.domain.TaxType;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.BankTypeRepository;
import com.freelancerk.domain.repository.ContestEntryFileRepository;
import com.freelancerk.domain.repository.ContestEntryTicketLogRepository;
import com.freelancerk.domain.repository.ContestEntryTicketRepository;
import com.freelancerk.domain.repository.FreelancerPayProductRepository;
import com.freelancerk.domain.repository.ProjectBidCommentRepository;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectPropositionRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.FrkEmailService;
import com.freelancerk.service.MessageService;
import com.freelancerk.service.ProjectBidService;
import com.freelancerk.service.StorageService;
import com.freelancerk.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "프로젝트 입찰", description = "입찰 참여/조회 등")
@RestController
public class ProjectBidController extends RootController {

	private UserService userService;
	private StorageService storageService;
	private UserRepository userRepository;
	private MessageService messageService;
	private FrkEmailService frkEmailService;
	private ProjectRepository projectRepository;
	private ProjectBidService projectBidService;
	private BankTypeRepository bankTypeRepository;
	private ProjectBidRepository projectBidRepository;
	private ContestEntryFileRepository contestEntryFileRepository;
	private ProjectBidCommentRepository projectBidCommentRepository;
	private ContestEntryTicketRepository contestEntryTicketRepository;
	private ProjectPropositionRepository projectPropositionRepository;
	private FreelancerPayProductRepository freelancerPayProductRepository;
	private ContestEntryTicketLogRepository contestEntryTicketLogRepository;

	@Autowired
	public ProjectBidController(
			UserService userService,
			StorageService storageService,
			UserRepository userRepository,
			MessageService messageService,
			ProjectBidService projectBidService,
			FrkEmailService frkEmailService,
			ProjectRepository projectRepository, 
			ProjectBidRepository projectBidRepository,
			ContestEntryFileRepository contestEntryFileRepository,
			ProjectBidCommentRepository projectBidCommentRepository,
			ContestEntryTicketRepository contestEntryTicketRepository,
			ProjectPropositionRepository projectPropositionRepository,
			FreelancerPayProductRepository freelancerPayProductRepository, BankTypeRepository bankTypeRepository,
			ContestEntryTicketLogRepository contestEntryTicketLogRepository) {
		this.userService = userService;
		this.storageService = storageService;
		this.userRepository = userRepository;
		this.messageService = messageService;
		this.frkEmailService = frkEmailService;
		this.projectRepository = projectRepository;
		this.projectBidService = projectBidService;
		this.bankTypeRepository = bankTypeRepository;
		this.projectBidRepository = projectBidRepository;
		this.contestEntryFileRepository = contestEntryFileRepository;
		this.projectBidCommentRepository = projectBidCommentRepository;
		this.contestEntryTicketRepository = contestEntryTicketRepository;
		this.projectPropositionRepository = projectPropositionRepository;
		this.freelancerPayProductRepository = freelancerPayProductRepository;
		this.contestEntryTicketLogRepository = contestEntryTicketLogRepository;
	}

	/*
	 * ============================= 사용중 API ==================================
	 */
	@ApiOperation("입찰 참여")
	@Transactional
	@RequestMapping(method = RequestMethod.POST, value = "/applyBid")
	public CommonResponse applyBid(
			@RequestParam("projectId") Long projectId, 
			@RequestParam("bidType") ProjectBid.BidType bidType,
			@RequestParam("bidStatus") ProjectBid.BidStatus bidStatus,
			@RequestParam("amount") Integer amount,
			@RequestParam(value = "taxType", required = false) TaxType taxType,
			@RequestParam(value = "bankAccountForReceivingPayment", required = false) String bankAccountForReceivingPayment,
			@RequestParam(value = "bankForReceivingPayment", required = false) String bankForReceivingPayment) {
		
		Long userId = userService.getCurrentUser().getId();
		Project project = projectRepository.getOne(projectId);
		if (project.isPostingEnd()) {
			return new CommonResponse.Builder<String>()
					.message("모집 종료된 프로젝트 입니다.")
					.build();
		}
		int count = projectBidRepository.countByProjectIdAndParticipantIdAndBidStatusNot(projectId, userId, ProjectBid.BidStatus.CANCEL);
		if(count > 0) {
			return new CommonResponse.Builder<String>()
									 .message("이미 입찰참여한 프로젝트입니다.")
									 .build();
		}
		
		ProjectBid projectBid = new ProjectBid();
		projectBid.setProject(project);
		projectBid.setBidType(bidType);
		projectBid.setBidStatus(bidStatus);
		projectBid.setApplyAt(LocalDateTime.now());
		projectBid.setAmountOfMoney(amount);

		projectBid.setParticipant(userRepository.getOne(userId));

		User user = userRepository.getOne(userId);
		if (StringUtils.isNotEmpty(bankAccountForReceivingPayment)) {
			projectBid.setBankAccountForReceivingPayment(bankAccountForReceivingPayment);
			user.setBankAccountForReceivingPayment(bankAccountForReceivingPayment);
		}
		if (StringUtils.isNotEmpty(bankForReceivingPayment)) {
			projectBid.setBankForReceivingPayment(bankTypeRepository.findByName(bankForReceivingPayment));
			user.setBankForReceivingPayment(bankTypeRepository.findByName(bankForReceivingPayment));
		}
		if (taxType != null) {
			projectBid.setTaxType(taxType);
			user.setTaxType(taxType);
		}
	
		projectBidRepository.save(projectBid);
		userRepository.save(user);

		ProjectProposition projectProposition = projectPropositionRepository.findByProjectIdAndFreelancerId(projectId, userId);
		if (projectProposition != null) {
			projectProposition.setStatus(ProjectProposition.Status.ACCEPT);
			projectPropositionRepository.save(projectProposition);
		}

		if (StringUtils.isNotEmpty(projectBid.getProject().getPostingClient().getEmail()) && projectBid.getProject().getPostingClient().isReceiveEmail()) {
			frkEmailService.sendProjectBidAlarm(projectBid.getParticipant(), projectBid.getProject().getPostingClient().getEmail());
		}

		Map<String, Object> messageVariables = new HashMap<>();
		messageVariables.put("freelancerName", projectBid.getParticipant().getExposeName());
		messageService.sendMessage(projectBid.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3177, messageVariables);

		return new CommonResponse.Builder<String>()
								 .message("입찰 참여에 성공했습니다.")
								 .build();
	}
	
	@ApiOperation("프로젝트 입찰 취소")
	@Transactional
	@RequestMapping(method = RequestMethod.POST, value = "/deleteBid")
	public CommonResponse deleteBidProject(@RequestParam("bidId") Long bidId) {
		ProjectBid bid = projectBidRepository.getOne(bidId);

		if(ProjectBid.BidType.CONTEST_BID.equals(bid.getBidType())) {
			List<FreelancerPayProduct> payProducts = freelancerPayProductRepository.findByContestEntryId(bidId);
			for(FreelancerPayProduct product : payProducts) {
				freelancerPayProductRepository.delete(product);
			}
			
			List<ContestEntryFile> entryFiles = contestEntryFileRepository.findByContestEntryId(bidId);
			for(ContestEntryFile file : entryFiles) {
				contestEntryFileRepository.delete(file);
			}

			List<ContestEntryTicket> tickets = contestEntryTicketRepository.findByProjectBidId(bid.getId());
			for (ContestEntryTicket ticket: tickets) {
				ticket.setInvalid(true);
				contestEntryTicketRepository.save(ticket);
			}

			List<ContestEntryTicketLog> logs = contestEntryTicketLogRepository.findByProjectBidIdAndInvalidFalse(bid.getId());
			for (ContestEntryTicketLog log: logs) {
				log.setInvalid(true);
				contestEntryTicketLogRepository.save(log);
			}
		}	
		
		bid.setInvalid(true);
		bid.setInvalidAt(LocalDateTime.now());
		projectBidRepository.save(bid);
		return new CommonResponse.Builder<String>()
								 .message("지원이 취소되었습니다.")
								 .build();
	}
	
	
	@ApiOperation("프로젝트 입찰금액 수정")
	@RequestMapping(method = RequestMethod.POST, value = "/bidPrice")
	public CommonResponse modifyApplyBidPrice(
			@RequestParam("bidId") Long bidId, 
			@RequestParam("price") Integer price) {
		
		ProjectBid bid = projectBidRepository.getOne(bidId);
		bid.setAmountOfMoney(price);
		projectBidRepository.save(bid);

		return new CommonResponse.Builder<String>()
								 .message("입찰가 수정이 완료되었습니다.")
								 .build();
	}
	
	@ApiOperation("제안받은 입찰 참여")
	@Transactional
	@RequestMapping(method = RequestMethod.POST, value = "/applyProposedBid")
	public CommonResponse applyProposedBid(
			@RequestParam("projectId") Long projectId, 
			@RequestParam("projectPropositionId") Long projectPropositionId,
			@RequestParam("bidType") ProjectBid.BidType bidType,
			@RequestParam("bidStatus") ProjectBid.BidStatus bidStatus,
			@RequestParam("amount") Integer amount,
			@RequestParam(value = "taxType", required = false) TaxType taxType,
			@RequestParam(value = "bankAccountForReceivingPayment", required = false) String bankAccountForReceivingPayment,
			@RequestParam(value = "bankForReceivingPayment", required = false) String bankForReceivingPayment) {
		
		Long userId = userService.getCurrentUser().getId();
		ProjectBid projectBid = new ProjectBid();
		projectBid.setProject(projectRepository.getOne(projectId));
		projectBid.setBidType(bidType);
		projectBid.setBidStatus(bidStatus);
		projectBid.setApplyAt(LocalDateTime.now());
		projectBid.setAmountOfMoney(amount);
		projectBid.setParticipant(userRepository.getOne(userId));

		User user = userRepository.getOne(userId);
		if (StringUtils.isNotEmpty(bankAccountForReceivingPayment)) {
			projectBid.setBankAccountForReceivingPayment(bankAccountForReceivingPayment);
			user.setBankAccountForReceivingPayment(bankAccountForReceivingPayment);
		}
		if (StringUtils.isNotEmpty(bankForReceivingPayment)) {
			projectBid.setBankForReceivingPayment(bankTypeRepository.findByName(bankForReceivingPayment));
			user.setBankForReceivingPayment(bankTypeRepository.findByName(bankForReceivingPayment));
		}
		if (taxType != null) {
			projectBid.setTaxType(taxType);
			user.setTaxType(taxType);
		}
	
		projectBidRepository.save(projectBid);
		userRepository.save(user);
		
		ProjectProposition pp = projectPropositionRepository.getOne(projectPropositionId);
		pp.setStatus(ProjectProposition.Status.ACCEPT);
		pp.setAcceptAt(LocalDateTime.now());
		projectPropositionRepository.save(pp);

		if (StringUtils.isNotEmpty(projectBid.getProject().getPostingClient().getEmail()) && projectBid.getProject().getPostingClient().isReceiveEmail()) {
			frkEmailService.sendProjectBidAlarm(projectBid.getParticipant(), projectBid.getProject().getPostingClient().getEmail());
		}

		Map<String, Object> messageVariables = new HashMap<>();
		messageVariables.put("freelancerName", projectBid.getParticipant().getExposeName());
		messageService.sendMessage(projectBid.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3177, messageVariables);

		return new CommonResponse.Builder<String>()
				 .message("입찰 참여에 성공했습니다.")
				 .build();
	}
		
	@ApiOperation("제안받은 입찰 거절")
	@RequestMapping(method = RequestMethod.POST, value = "/rejectProposedBid")
	public CommonResponse rejectProposedBid(
			@RequestParam("propId") Long propId,
			@RequestParam("rejectReason") String rejectReason) {
		
		ProjectProposition pp = projectPropositionRepository.getOne(propId);
		pp.setStatus(ProjectProposition.Status.REJECT);
		pp.setRejectReason(rejectReason);
		projectPropositionRepository.save(pp);
		return new CommonResponse.Builder<String>()
								 .message("제안 받은 입찰을 정상적으로 거절 처리 하였습니다.")
								 .build();
	}
	
	
	
	/**
	 * 컨테스트 파일 추가
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/addContestFile")
	public CommonResponse addContestFile(
			@RequestParam(value = "bidId", required = false) Long bidId,
			@RequestParam(value = "fileType", required = false) String fileType,
			@RequestParam(value = "fileTitle", required = false) String fileTitle,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "representative", required = false) Boolean representative) {
		
		ProjectBid bid = projectBidRepository.getOne(bidId);
		ContestEntryFile.Type type = ContestEntryFile.Type.IMAGE;
		if(fileType.equals("VIDEO")) {
			type = ContestEntryFile.Type.VIDEO;
		} else if(fileType.equals("AUDIO")) {
			type = ContestEntryFile.Type.AUDIO;
		}
		
		if(representative != null) {
			if(representative) {
				List<ContestEntryFile> files = contestEntryFileRepository.findByContestEntryId(bidId);
				for(ContestEntryFile f :  files) {
					f.setRepresentative(false);
					contestEntryFileRepository.save(f);	
				}
			}			
		}
		
		projectBidService.addContestFile(bid, type, fileTitle, file, representative);
				
		return new CommonResponse.Builder<String>()
								 .message("정상적으로 추가 하였습니다.")
								 .build();
	}
	
	
	/**
	 * 컨테스트 파일 수정
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/modifyContestFile")
	public CommonResponse modifyContestFile(
			@RequestParam("fileId") Long fileId,
			@RequestParam(value = "fileTitle", required = false) String fileTitle,
			@RequestParam(value = "newFile", required = false) MultipartFile newFile,
			@RequestParam(value = "representative", required = false) Boolean representative) {
		
		ContestEntryFile originFile = contestEntryFileRepository.getOne(fileId);
				
		if(representative != null) {
			if(representative) {
				List<ContestEntryFile> files = contestEntryFileRepository.findByContestEntryId(originFile.getContestEntry().getId());
				for(ContestEntryFile f :  files) {
					if(f.getId() != originFile.getId()) {
						f.setRepresentative(false);
						contestEntryFileRepository.save(f);	
					}	
				}
				originFile.setRepresentative(true);
			}			
		}
		
		
		if(!StringUtils.isEmpty(fileTitle)) {
			originFile.setFileTitle(fileTitle);
		}		
		
		if(newFile != null) {
			originFile.setFileOriginName(newFile.getOriginalFilename());
			originFile.setFileUrl(storageService.storeFile(newFile));
			originFile.setFileSize(newFile.getSize());
			originFile.setFileOriginName(newFile.getOriginalFilename());
		}
		
		contestEntryFileRepository.save(originFile);		
		return new CommonResponse.Builder<String>()
								 .message("정상적으로 수정하였습니다.")
								 .build();
	}
		
	/**
	 * 컨테스트 파일 삭제
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/deleteContestFile")
	public CommonResponse deleteContestFile(@RequestParam("fileId") Long fileId) {		
		contestEntryFileRepository.deleteById(fileId);
		return new CommonResponse.Builder<String>()
								 .message("정상적으로 삭제하였습니다.")
								 .build();
	}


	@ApiOperation("낙찰 기록 삭제")
	@RequestMapping(method = RequestMethod.DELETE, value = "/project-bids/{bidId}")
	public CommonResponse deleteProjectBid(@PathVariable("bidId") Long bidId) {
		ProjectBid projectBid = projectBidRepository.getOne(bidId);
		projectBid.setInvalid(true);
		projectBidRepository.save(projectBid);

		return CommonResponse.ok();
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/project-bids/{bidId}/comments")
	public CommonResponse insertComment(@PathVariable("bidId") Long bidId,
										@RequestParam(value = "contents", required = false) String contents,
										@RequestParam(value = "commentFile", required = false) MultipartFile file) {
		ProjectBid projectBid = projectBidRepository.getOne(bidId);
		ProjectBidComment comment = new ProjectBidComment();
		comment.setContent(contents);
		if (file != null && !file.isEmpty()) {
			comment.setFileName(file.getOriginalFilename());
			comment.setFileUrl(storageService.storeFile(file));
			comment.setType(ProjectBidComment.Type.FILE);
		} else if (StringUtils.isNotEmpty(contents)) {
			comment.setType(ProjectBidComment.Type.MESSAGE);
		}
		comment.setUserRole(isLoggedIsAsClient()?User.Role.ROLE_CLIENT:User.Role.ROLE_FREELANCER);
		comment.setProjectBid(projectBid);
		comment.setUser(userRepository.getOne(getSessionUserId()));
		projectBidCommentRepository.save(comment);

		if (Project.Status.POSTED.equals(projectBid.getProject().getStatus())) {
			if (getSessionUserId().equals(projectBid.getProject().getPostingClient().getId())) {
				List<ProjectBid> projectBids = projectBidRepository.findByProjectIdAndBidStatusIn(projectBid.getProject().getId(),
						Arrays.asList(ProjectBid.BidStatus.APPLY));
				for (ProjectBid bid : projectBids) {
					Map<String, Object> messageVariables = new HashMap<>();
					messageVariables.put("projectName", projectBid.getProject().getTitle());
					messageService.sendMessage(bid.getParticipant(), AligoKakaoMessageTemplate.Code.TA_3201, messageVariables);
				}
			} else {
				Map<String, Object> messageVariables = new HashMap<>();
				messageVariables.put("projectName", projectBid.getProject().getTitle());
				messageVariables.put("freelancerName", userService.getCurrentUser().getExposeName());
				messageService.sendMessage(projectBid.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3202, messageVariables);
			}

		} else if (Project.Status.IN_PROGRESS.equals(projectBid.getProject().getStatus())) {
			if (getSessionUserId().equals(projectBid.getProject().getPostingClient().getId())) {
				List<ProjectBid> pickedBids = projectBidRepository.findByProjectIdAndBidStatusIn(projectBid.getProject().getId(),
						Arrays.asList(ProjectBid.BidStatus.PICKED));
				for (ProjectBid pickedBid: pickedBids) {
					Map<String, Object> messageVariables = new HashMap<>();
					messageVariables.put("projectName", projectBid.getProject().getTitle());
					messageVariables.put("freelancerName", pickedBid.getParticipant().getExposeName());
					messageService.sendMessage(projectBid.getProject().getContractedFreelancer(), AligoKakaoMessageTemplate.Code.TA_3205, messageVariables);
				}
			} else {
				Map<String, Object> messageVariables = new HashMap<>();
				messageVariables.put("projectName", projectBid.getProject().getTitle());
				messageVariables.put("freelancerName", userService.getCurrentUser().getExposeName());
				messageService.sendMessage(projectBid.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3204, messageVariables);
			}
		}
		return CommonResponse.ok();
	}

	/*
	 * ============================= 사용중 API 끝 ==================================//
	 */
	
	
	
//	@ApiOperation("컨테스트 지원이 가능한지 확인")
//	@RequestMapping(method = RequestMethod.GET, value = "/contestEntryApplyStatus")
//	public CommonResponse contestEntryApplyStatus(@RequestParam("contestId") Long contestId) {
//		Long userId = userService.getCurrentUser().getId();
//		
//		ProjectBid projectBid = projectBidRepository.findByProjectIdAndParticipantId(contestId, userId);
//		
//		// true : 지원가능, false : 지원 불가능
//		String data = "true";
//		if(projectBid != null) {	
//			data = "true";
//		} else {
//			data = "false";
//		}
//		return new CommonResponse.Builder<String>()
//								 .data(data)
//								 .build();
//	}
	
	
//
//    @ApiOperation("입찰 조회")
//    @RequestMapping(method = RequestMethod.GET, value = "/projects/bids")
//    public CommonListResponse<List<ProjectBid>> getProjectBids(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
//                                                         @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
//                                                         @RequestParam("projectStatus") Project.Status status) {
//        Page<ProjectBid> page = projectBidRepository.findByProjectStatus(status, PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC ,"id")));
//        return new CommonListResponse.Builder<List<ProjectBid>>()
//                .totalCount(page.getTotalElements())
//                .currentPage(pageNumber)
//                .totalPages(page.getTotalPages())
//                .data(page.getContent())
//                .build();
//    }
}
