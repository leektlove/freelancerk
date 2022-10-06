package com.freelancerk.controller;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freelancerk.FileUtil;
import com.freelancerk.domain.ContestEntryFile;
import com.freelancerk.domain.ContestEntryReward;
import com.freelancerk.domain.ContestEntryTicket;
import com.freelancerk.domain.ContestEntryTicketLog;
import com.freelancerk.domain.PaymentToUser;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.repository.ContestEntryRewardRepository;
import com.freelancerk.domain.repository.ContestEntryTicketLogRepository;
import com.freelancerk.domain.repository.ContestEntryTicketRepository;
import com.freelancerk.domain.repository.PaymentToUserRepository;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.domain.specification.ContestEntryTicketSpecifications;
import com.freelancerk.io.CommonResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "컨테스트 입찰", description = "입찰 참여/조회 등")
@RestController
public class ContestBidController extends RootController {

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private ProjectBidRepository projectBidRepository;
    private PaymentToUserRepository paymentToUserRepository;
    private ContestEntryRewardRepository contestEntryRewardRepository;
    private ContestEntryTicketRepository contestEntryTicketRepository;
    private ContestEntryTicketLogRepository contestEntryTicketLogRepository;

    @Autowired
    public ContestBidController(UserRepository userRepository, ProjectRepository projectRepository,
                                ProjectBidRepository projectBidRepository, PaymentToUserRepository paymentToUserRepository,
                                ContestEntryRewardRepository contestEntryRewardRepository,
                                ContestEntryTicketRepository contestEntryTicketRepository,
                                ContestEntryTicketLogRepository contestEntryTicketLogRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectBidRepository = projectBidRepository;
        this.paymentToUserRepository = paymentToUserRepository;
        this.contestEntryRewardRepository = contestEntryRewardRepository;
        this.contestEntryTicketRepository = contestEntryTicketRepository;
        this.contestEntryTicketLogRepository = contestEntryTicketLogRepository;
    }

    @ApiOperation("컨테스트 상금 지급")
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/contests/entries/{id}/rewards")
    public CommonResponse giveReward(@PathVariable("id") Long projectBidId) {
        ProjectBid projectBid = projectBidRepository.getOne(projectBidId);
        Project project = projectBid.getProject();

        ContestEntryReward contestEntryReward = new ContestEntryReward();
        contestEntryReward.setProjectBid(projectBid);
        if (projectBid.getPickedRank() == 0) {
            contestEntryReward.setAmount(project.getPrizeFor1st());
        } else if (projectBid.getPickedRank() == 1) {
            contestEntryReward.setAmount(project.getPrizeFor2nd());
        } else if (projectBid.getPickedRank() == 2) {
            contestEntryReward.setAmount(project.getPrizeFor3rd());
        }
        contestEntryReward.setStatus(ContestEntryReward.Status.REQUESTED);
        contestEntryRewardRepository.save(contestEntryReward);

        int pickedContestEntries = contestEntryRewardRepository.countByProjectBidProjectId(project.getId());
        if (pickedContestEntries == project.getPrizeTargetPersons()) {
            project.setStatus(Project.Status.COMPLETED);
            project.setCompletedAt(LocalDateTime.now());
            projectRepository.save(project);
        }

        PaymentToUser payment = new PaymentToUser();
        payment.setUser(contestEntryReward.getProjectBid().getParticipant());
        payment.setFreelancerName(payment.getUser().getRealName());
        payment.setTaxType(payment.getUser().getTaxType());
        payment.setBankType(payment.getUser().getBankForReceivingPayment());
        payment.setBankAccountName(payment.getUser().getBankAccountName());
        payment.setBankAccountForReceivingPayment(payment.getUser().getBankAccountForReceivingPayment());
        payment.setAmount(contestEntryReward.getAmount());
        payment.setStatus(PaymentToUser.Status.REQUESTED);
        payment.setDescription("컨테스트 상급 지급 요청");
        payment.setContestEntryReward(contestEntryReward);
        payment.setProject(project);
        payment.setType(PaymentToUser.Type.CONTEST_REWARD);
        paymentToUserRepository.save(payment);

        return new CommonResponse.Builder<Project.Status>()
                .data(project.getStatus())
                .message(Project.Status.COMPLETED.equals(project.getStatus())?
                        (project.getPrizeTargetPersons() ==1?"축하합니다! 이제 이 작품의 저작권은 클라이언트에게 귀속됩니다. 완료된 프로젝트로 이동합니다.":"모든 작품에 대한 상금지급을 완료하셨습니다. 완료된 프로젝트로 이동합니다. ")
                        :"축하합니다! 이제 이 작품의 저작권은 클라이언트에게 귀속됩니다. 나머지 작품에 대한 상금지급이 모두 완료되어야 컨테스트가 종료됩니다. ")
                .build();
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/contest-entries/{bidId}/files")
    public CommonResponse updateFiles(@PathVariable("bidId") Long bidId,
                                      @RequestParam(value = "mainPiecesFileUrl", required = false) String mainPiecesFileUrl,
                                      @RequestParam(value = "subPiecesFileUrl[]", required = false) String[] subPiecesFileUrl) {
        ProjectBid bid = projectBidRepository.getOne(bidId);

        Set<ContestEntryFile> allContestFiles = new LinkedHashSet<>();

        ContestEntryFile mainContestEntryFile = new ContestEntryFile();
        mainContestEntryFile.setFileUrl(mainPiecesFileUrl);
        mainContestEntryFile.setCroppedFileUrl(mainContestEntryFile.getFileUrl());
        mainContestEntryFile.setFileType(FileUtil.getFileType(mainPiecesFileUrl));
        mainContestEntryFile.setFileTitle(mainContestEntryFile.getFileOriginName());
        mainContestEntryFile.setContestEntry(bid);
        mainContestEntryFile.setRepresentative(true);
        mainContestEntryFile.setUser(userRepository.getOne(getSessionUserId()));
        mainContestEntryFile = FileUtil.setMetaInfo(mainContestEntryFile, mainPiecesFileUrl);
        allContestFiles.add(mainContestEntryFile);

        if (subPiecesFileUrl != null) {
            for (String fileUrl : subPiecesFileUrl) {
                ContestEntryFile contestEntryFile = new ContestEntryFile();
                contestEntryFile.setFileUrl(fileUrl);
                contestEntryFile.setCroppedFileUrl(contestEntryFile.getFileUrl());
                contestEntryFile.setFileType(FileUtil.getFileType(fileUrl));
                contestEntryFile.setFileTitle(contestEntryFile.getFileOriginName());
                contestEntryFile.setContestEntry(bid);
                contestEntryFile.setUser(mainContestEntryFile.getUser());
                mainContestEntryFile.setUser(userRepository.getOne(getSessionUserId()));
                contestEntryFile = FileUtil.setMetaInfo(contestEntryFile, fileUrl);
                allContestFiles.add(contestEntryFile);
            }
        }

        if (bid.getAllContestEntryFiles() != null) {
            bid.getAllContestEntryFiles().clear();
            bid.getAllContestEntryFiles().addAll(allContestFiles);
        } else {
            bid.setAllContestEntryFiles(allContestFiles);
        }
        projectBidRepository.save(bid);

        return CommonResponse.ok();
    }

    @Transactional
    @RequestMapping(method = RequestMethod.DELETE, value = "/contest-entries/{id}/purchase-records")
    public CommonResponse deletePurchaseRecords(@PathVariable("id") long id) {
        ProjectBid projectBid = projectBidRepository.getOne(id);
        projectBid.setPurchaseRecordDeleted(true);

        projectBidRepository.save(projectBid);

        List<ContestEntryTicket> tickets = contestEntryTicketRepository.findAll(ContestEntryTicketSpecifications.filterForActiveTicketByContestEntryId(id));
        for (ContestEntryTicket ticket: tickets) {
            ticket.setInvalid(true);
            contestEntryTicketRepository.save(ticket);
        }

        List<ContestEntryTicketLog> logs = contestEntryTicketLogRepository.findByProjectBidIdAndInvalidFalse(id);
        for (ContestEntryTicketLog log: logs) {
            log.setInvalid(true);
            contestEntryTicketLogRepository.save(log);
        }

        return CommonResponse.ok();
    }

    //
//    private ContestBidService contestBidService;
//    private ContestEntryRepository contestEntryRepository;
//
//    @Autowired
//    public ContestBidController(ContestBidService contestBidService, ContestEntryRepository contestEntryRepository) {
//        this.contestBidService = contestBidService;
//        this.contestEntryRepository = contestEntryRepository;
//    }

//    @ApiOperation("입찰참여")
//    @RequestMapping(method = RequestMethod.POST, value = "/contests/{contestId}/bids")
//    public CommonResponse bidContest(@PathVariable("contestId") Long contestId,
//                                     @RequestParam("contestEntryFileId[]") MultipartFile[] imageFiles,
//                                     @RequestParam("taxType") TaxType taxType, @RequestParam("businessType") BusinessType businessType,
//                                     @RequestParam(value = "bankAccountForReceivingPayment", required = false) String bankAccountForReceivingPayment,
//                                     @RequestParam(value = "bankForReceivingPayment", required = false) String bankForReceivingPayment,
//                                     @RequestParam(value = "contestBidOption[]") PaymentPolicy.ContestBid[] contestBidOptions,
//                                     @RequestParam(value = "usedPoints", required = false) Integer usedPoint) {
//        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
//
//        contestBidService.insert(userId, contestId, imageFiles, taxType, businessType, bankAccountForReceivingPayment, bankForReceivingPayment,
//                contestBidOptions, usedPoint);
//
//        return CommonResponse.ok();
//    }
//
//    @ApiOperation("입찰참여 수정")
//    @RequestMapping(method = RequestMethod.POST, value = "/contest-entries/{contestEntryId}/modifications")
//    public CommonResponse modifyContestEntry(@PathVariable("contestEntryId") Long contestEntryId,
//                                             @RequestParam("imageFile[]") MultipartFile[] imageFiles) {
//        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
//
//        contestBidService.update(userId, contestEntryId, imageFiles);
//
//        return CommonResponse.ok();
//    }
//
//    @ApiOperation("입찰참여내역 상세보기")
//    @RequestMapping(method = RequestMethod.GET, value = "/contest-entries/{contestEntryId}/info")
//    public CommonResponse<ContestEntry> getContestEntryDetails(@PathVariable("contestEntryId") Long contestEntryId) {
//        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
//        return new CommonResponse.Builder<ContestEntry>().data(contestBidService.getInfo(contestEntryId)).build();
//    }
//
//    @ApiOperation("입찰 취소")
//    @RequestMapping(method = RequestMethod.DELETE, value = "/contest-entries/{contestEntryId}")
//    public CommonResponse cancelContestEntry(@PathVariable("contestEntryId") Long contestEntryId) {
//        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
//        contestBidService.cancel(userId, contestEntryId);
//        return CommonResponse.ok();
//    }
//
//    @ApiOperation("입찰 조회")
//    @RequestMapping(method = RequestMethod.GET, value = "/contests/bids")
//    public CommonListResponse<List<ContestEntry>> getContestEntries(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
//                                                              @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
//                                                              @RequestParam("contestStatus") Contest.Status status) {
//        Page<ContestEntry> page = contestEntryRepository.findByContestStatus(status, PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC ,"id")));
//        return new CommonListResponse.Builder<List<ContestEntry>>()
//                .totalCount(page.getTotalElements())
//                .currentPage(pageNumber)
//                .totalPages(page.getTotalPages())
//                .data(page.getContent())
//                .build();
//    }
}
