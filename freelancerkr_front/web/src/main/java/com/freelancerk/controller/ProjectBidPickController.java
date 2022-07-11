package com.freelancerk.controller;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.io.ResponseCode;
import com.freelancerk.service.FrkEmailService;
import com.freelancerk.service.MessageService;
import com.freelancerk.service.ProjectBidService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ProjectBidPickController {

    private MessageService messageService;
    private FrkEmailService frkEmailService;
    private ProjectBidService projectBidService;
    private ProjectBidRepository projectBidRepository;

    @Autowired
    public ProjectBidPickController(MessageService messageService, ProjectBidService projectBidService,
                                    FrkEmailService frkEmailService, ProjectBidRepository projectBidRepository) {
        this.messageService = messageService;
        this.frkEmailService = frkEmailService;
        this.projectBidService = projectBidService;
        this.projectBidRepository = projectBidRepository;
    }

    @ApiOperation("낙찰")
    @RequestMapping(method = RequestMethod.POST, value = "/projects/{projectId}/picks")
    public CommonResponse pickBid(
            @PathVariable("projectId") long projectId,
            @RequestParam("projectBidId") long projectBidId,
            @RequestParam("pickedAmount") int pickedAmount) {
        ProjectBid projectBid = projectBidRepository.getOne(projectBidId);

        if (ProjectBid.BidStatus.PICKED.equals(projectBid.getBidStatus())) {
            return new CommonResponse.Builder<String>().message("이미 선정한 지원자 입니다.").responseCode(ResponseCode.FAIL).build();
        }
        Project project = projectBid.getProject();

        if (StringUtils.isNotEmpty(project.getPostingClient().getEmail()) && project.getPostingClient().isReceiveEmail()) {
            frkEmailService.sendProjectBidSuccessToClient(projectBid.getParticipant(), project.getPostingClient(), project, project.getPostingClient().getEmail());
        }

        User freelancer = projectBid.getParticipant();
        if (StringUtils.isNotEmpty(freelancer.getEmail()) && freelancer.isReceiveEmail()) {
            frkEmailService.sendProjectBidSuccessToFreelancer(freelancer, project.getPostingClient(), project, freelancer.getEmail());
        }

        projectBidService.pickBidAndStartProject(projectId, projectBidId, pickedAmount);

        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("clientName", project.getPostingClient().getExposeName());
        variableMap.put("projectName", project.getTitle());
        variableMap.put("freelancerName", freelancer.getExposeName());
        messageService.sendMessage(freelancer, AligoKakaoMessageTemplate.Code.TA_3180, variableMap);

        return new CommonResponse.Builder<String>()
                .message("정상적으로 낙찰 처리 하였습니다.")
                .build();
    }
}
