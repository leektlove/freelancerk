package com.freelancerk.controller;


import com.freelancerk.TimeUtil;
import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.io.CommonListResponse;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.ProjectProposition;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.*;
import com.freelancerk.exception.UserNotMatchedException;
import com.freelancerk.io.ResponseCode;
import com.freelancerk.service.MessageService;
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

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "프로젝트 제안", description = "제안/조회 등")
@RestController
public class ProjectPropositionController extends RootController {

    private UserRepository userRepository;
    private MessageService messageService;
    private ProjectRepository projectRepository;
    private PickMeUpRepository pickMeUpRepository;
    private ProjectBidRepository projectBidRepository;
    private ProjectPropositionRepository projectPropositionRepository;

    @Autowired
    public ProjectPropositionController(UserRepository userRepository, MessageService messageService, PickMeUpRepository pickMeUpRepository, ProjectBidRepository projectBidRepository,
                                        ProjectPropositionRepository projectPropositionRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.pickMeUpRepository = pickMeUpRepository;
        this.projectRepository = projectRepository;
        this.projectBidRepository = projectBidRepository;
        this.projectPropositionRepository = projectPropositionRepository;
    }

    @ApiOperation("입찰 참여제안")
    @RequestMapping(method = RequestMethod.POST, value = "/projects/{projectId}/propositions")
    public CommonResponse requestProjectProposition(@PathVariable("projectId") Long projectId,
                                                     @RequestParam("pickMeUpId") Long pickMeUpId,
                                                     HttpServletResponse response) {
        PickMeUp pickMeUp = pickMeUpRepository.getOne(pickMeUpId);
        Long userId = pickMeUp.getUser().getId();
        if (projectPropositionRepository.countByFreelancerIdAndProjectId(userId, projectId) > 0) {
            return new CommonResponse.Builder<Void>().message("이미 제안하신 프리랜서의 포트폴리오입니다.").responseCode(ResponseCode.FAIL).build();
        }

        if (projectBidRepository.countByProjectIdAndParticipantId(projectId, userId) > 0) {
            return new CommonResponse.Builder<Void>().message("이미 해당 프로젝트의 입찰에 참여하셨습니다. 입찰자 내역을 확인해보세요").responseCode(ResponseCode.FAIL).build();
        }

        User user = userRepository.getOne(userId);

        ProjectProposition projectProposition = new ProjectProposition();
        projectProposition.setPickMeUp(pickMeUp);
        projectProposition.setFreelancer(user);
        projectProposition.setCreatedAt(LocalDateTime.now());
        projectProposition.setProject(projectRepository.getOne(projectId));
        projectProposition.setStatus(ProjectProposition.Status.PROPOSE);
        projectPropositionRepository.save(projectProposition);

        Map<String, Object> messageVariables = new HashMap<>();
        messageVariables.put("clientName", projectProposition.getProject().getPostingClient().getExposeName());
        messageService.sendMessage(user, AligoKakaoMessageTemplate.Code.TA_3198, messageVariables);
        return CommonResponse.ok();
    }

    @Transactional
    @ApiOperation("입찰 참여 제안 거절")
    @RequestMapping(method = RequestMethod.POST, value = "/project-propositions/{projectPropositionId}/deny")
    public CommonResponse denyProjectProposition(@PathVariable("projectPropositionId") Long projectPropositionId) {
        ProjectProposition projectProposition = projectPropositionRepository.getOne(projectPropositionId);
        if (!projectProposition.getFreelancer().getId().equals(getSessionUserId())) {
            throw UserNotMatchedException.getInstance();
        }
        projectProposition.setStatus(ProjectProposition.Status.REJECT);
        projectPropositionRepository.save(projectProposition);

        return CommonResponse.ok();
    }

    @Transactional
    @ApiOperation("입찰 참여 제안 취소")
    @RequestMapping(method = RequestMethod.DELETE, value = "/project-propositions/{projectPropositionId}")
    public CommonResponse cancelProjectProposition(@PathVariable("projectPropositionId") Long projectPropositionId) {
        ProjectProposition projectProposition = projectPropositionRepository.getOne(projectPropositionId);
        if (!projectProposition.getProject().getPostingClient().getId().equals(getSessionUserId())) {
            throw UserNotMatchedException.getInstance();
        }
        projectPropositionRepository.deleteById(projectPropositionId);

        return CommonResponse.ok();
    }

    @ApiOperation("입찰 참여제안 내역")
    @RequestMapping(method = RequestMethod.GET, value = "/project-propositions")
    private CommonListResponse<List<ProjectProposition>> getProjectPropositions(@RequestParam(value = "keyword", required = false) String keyword,
                                                                                @RequestParam(value = "dateFrom", required = false) String dateFrom,
                                                                                @RequestParam(value = "dateTo", required = false) String dateTo,
                                                                                @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                                @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();

        Page<ProjectProposition> projectPropositionPage = null;
        if (StringUtils.isEmpty(keyword)) {
            projectPropositionPage = projectPropositionRepository.findByFreelancerId(userId, PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        } else if (StringUtils.isAnyEmpty(dateFrom, dateTo)) {
            projectPropositionPage = projectPropositionRepository.findByProjectTitleContainingAndFreelancerId(keyword, userId, PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        } else {
            projectPropositionPage = projectPropositionRepository.findByProjectTitleContainingAndFreelancerIdAndCreatedAtGreaterThanAndCreatedAtLessThan(
                    keyword, userId,
                    TimeUtil.convertStrToLocalDateTime(dateFrom),
                    TimeUtil.convertStrToLocalDateTime(dateTo),
                    PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        }
        return new CommonListResponse.Builder<List<ProjectProposition>>()
                .totalCount(projectPropositionPage.getTotalElements())
                .currentPage(pageNumber)
                .totalPages(projectPropositionPage.getTotalPages())
                .data(projectPropositionPage.getContent())
                .build();
    }
}
