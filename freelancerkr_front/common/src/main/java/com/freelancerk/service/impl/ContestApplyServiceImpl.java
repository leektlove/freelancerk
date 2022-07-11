package com.freelancerk.service.impl;

import com.freelancerk.FileUtil;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.ContestEntryFileRepository;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectPropositionRepository;
import com.freelancerk.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ContestApplyServiceImpl implements ContestApplyService {

    private UserService userService;
    private MessageService messageService;
    private FrkEmailService frkEmailService;
    private ProjectBidRepository projectBidRepository;
    private VideoThumbnailService videoThumbnailService;
    private ContestEntryFileRepository contestEntryFileRepository;
    private ProjectPropositionRepository projectPropositionRepository;

    public ContestApplyServiceImpl(UserService userService, MessageService messageService, FrkEmailService frkEmailService,
                                   ProjectBidRepository projectBidRepository,
                                   VideoThumbnailService videoThumbnailService, ContestEntryFileRepository contestEntryFileRepository,
                                   ProjectPropositionRepository projectPropositionRepository) {
        this.userService = userService;
        this.messageService = messageService;
        this.frkEmailService = frkEmailService;
        this.projectBidRepository = projectBidRepository;
        this.videoThumbnailService = videoThumbnailService;
        this.contestEntryFileRepository = contestEntryFileRepository;
        this.projectPropositionRepository = projectPropositionRepository;
    }

    @Transactional
    @Override
    public ProjectBid apply(Project contest, Long userId, String mainFileUrl, String croppedMainPiecesFileUrl,
                      String[] subFileUrls, String[] croppedSubPiecesFileUrls, String videoThumbnailImageUrl) {
        User user = userService.getCurrentUser();
        ProjectBid projectBid = projectBidRepository.findTop1ByParticipantIdAndProjectIdOrderByCreatedAtDesc(userId, contest.getId());
        if (projectBid == null) {
            projectBid = new ProjectBid();
            projectBid.setParticipant(user);
            projectBid.setProject(contest);
            projectBid.setApplyAt(LocalDateTime.now());
            projectBid.setBidStatus(ProjectBid.BidStatus.APPLY);
            projectBid.setBidType(ProjectBid.BidType.CONTEST_BID);
            projectBid.setTaxType(user.getTaxType());

            projectBid = projectBidRepository.save(projectBid);

            ContestEntryFile contestEntryFile = new ContestEntryFile();
            contestEntryFile.setFileUrl(mainFileUrl);
            contestEntryFile.setCroppedFileUrl(mainFileUrl);
            contestEntryFile.setUser(user);
            contestEntryFile.setRepresentative(true);
            contestEntryFile.setFileType(FileUtil.getFileType(contestEntryFile.getFileUrl()));
            contestEntryFile.setContestEntry(projectBid);
            contestEntryFile.setVideoImageUrl(videoThumbnailImageUrl);
            contestEntryFile = FileUtil.setMetaInfo(contestEntryFile, mainFileUrl);
            if (ContestEntryFile.Type.VIDEO.equals(contestEntryFile.getFileType()) && StringUtils.isEmpty(videoThumbnailImageUrl)) {
                videoThumbnailService.getOneThumbnailUrl(contestEntryFile.getFileUrl());
            }
            contestEntryFileRepository.save(contestEntryFile);

            if (subFileUrls != null) {
                int i = 0;
                for (String subFileUrl : subFileUrls) {
                    ContestEntryFile contestEntrySubFile = new ContestEntryFile();
                    contestEntrySubFile.setFileUrl(subFileUrl);
                    if (croppedSubPiecesFileUrls != null && croppedSubPiecesFileUrls.length > i) {
                        contestEntrySubFile.setCroppedFileUrl(croppedSubPiecesFileUrls[i++]);
                    }
                    contestEntrySubFile.setUser(user);
                    contestEntrySubFile.setRepresentative(false);
                    contestEntrySubFile.setFileType(FileUtil.getFileType(subFileUrl));
                    contestEntrySubFile.setContestEntry(projectBid);
                    contestEntrySubFile = FileUtil.setMetaInfo(contestEntrySubFile, subFileUrl);
                    contestEntryFileRepository.save(contestEntrySubFile);
                }
            }

            ProjectProposition projectProposition = projectPropositionRepository.findByProjectIdAndFreelancerId(contest.getId(), userId);
            if (projectProposition != null) {
                projectProposition.setStatus(ProjectProposition.Status.ACCEPT);
                projectPropositionRepository.save(projectProposition);
            }

            if (StringUtils.isNotEmpty(projectBid.getProject().getPostingClient().getEmail()) && projectBid.getProject().getPostingClient().isReceiveEmail()) {
                frkEmailService.sendContestBidAlarm(projectBid.getParticipant(), projectBid.getProject().getPostingClient().getEmail());
            }

            Map<String, Object> messageVariables = new HashMap<>();
            messageVariables.put("freelancerName", projectBid.getParticipant().getExposeName());
            messageService.sendMessage(projectBid.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3178, messageVariables);
        }

        return projectBid;
    }
}
