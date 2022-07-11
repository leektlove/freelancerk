package com.freelancerk.controller;

import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.ProjectCompleteRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.domain.repository.ProjectRateRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.service.FrkEmailService;
import com.freelancerk.service.MessageService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ProjectRateController extends RootController {

    private MessageService messageService;
    private FrkEmailService frkEmailService;
    private ProjectRepository projectRepository;
    private ProjectRateRepository projectRateRepository;
    private ProjectCompleteRepository projectCompleteRepository;

    @Autowired
    public ProjectRateController(MessageService messageService, FrkEmailService frkEmailService, ProjectRepository projectRepository,
                                 ProjectRateRepository projectRateRepository, ProjectCompleteRepository projectCompleteRepository) {
        this.messageService = messageService;
        this.frkEmailService = frkEmailService;
        this.projectRepository = projectRepository;
        this.projectRateRepository = projectRateRepository;
        this.projectCompleteRepository = projectCompleteRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/projects/{projectId}/rates")
    public CommonResponse<ProjectRate> getProjectRate(@PathVariable("projectId") final Long projectId,
                                                      @RequestParam("raterType") final ProjectRate.RaterType raterType) {

        return new CommonResponse.Builder<ProjectRate>().data(projectRateRepository.findByProjectIdAndRaterType(projectId, raterType)).build();
    }

    @ApiOperation("프로젝트 평가하기")
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/projects/{projectId}/rates")
    public CommonResponse rateContestEntry(@PathVariable("projectId") Long projectId, @RequestParam("type1Rate") int type1Rate,
                                           @RequestParam("type2Rate") int type2Rate, @RequestParam("type3Rate") int type3Rate,
                                           @RequestParam("type4Rate") int type4Rate, @RequestParam("type5Rate") int type5Rate,
                                           @RequestParam(value = "content", required = false) String content,
                                           @RequestParam("raterType") final ProjectRate.RaterType raterType) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();

        if (projectRateRepository.countByProjectIdAndRaterType(projectId, raterType) > 0) {
            return CommonResponse.fail();
        }

        ProjectRate projectRate = new ProjectRate();
        projectRate.setProject(projectRepository.getOne(projectId));
        projectRate.setRaterType(raterType);
        projectRate.setContent(content);
        projectRate.setRaterUser(ProjectRate.RaterType.CLIENT.equals(raterType)?
                projectRate.getProject().getPostingClient():
                projectRate.getProject().getContractedFreelancer());
        projectRate.setRatedUser(ProjectRate.RaterType.CLIENT.equals(raterType)?
                projectRate.getProject().getContractedFreelancer():
                projectRate.getProject().getPostingClient());
        projectRate.setType1Rate(type1Rate);
        projectRate.setType2Rate(type2Rate);
        projectRate.setType3Rate(type3Rate);
        projectRate.setType4Rate(type4Rate);
        projectRate.setType5Rate(type5Rate);
        projectRateRepository.save(projectRate);

        ProjectComplete projectComplete = projectCompleteRepository.findByProjectId(projectId);
        if (projectComplete == null) {
            projectComplete = new ProjectComplete();
            projectComplete.setProject(projectRepository.getOne(projectId));
            projectComplete.setFreelancerRequest(true);
            projectComplete.setFreelancerRequestAt(LocalDateTime.now());
        } else {
            projectComplete.setClientAccept(true);
            projectComplete.setClientAcceptAt(LocalDateTime.now());
        }
        projectCompleteRepository.save(projectComplete);

        if (ProjectRate.RaterType.FREELANCER.equals(raterType)) {
            User client = projectRate.getProject().getPostingClient();
            if (StringUtils.isNotEmpty(client.getEmail()) && client.isReceiveEmail()) {
                frkEmailService.sendRatedAlarmToClient(client.getEmail());
            }

            Map<String, Object> messageVariablesFor = new HashMap<>();
            if (StringUtils.isNotEmpty(projectComplete.getProject().getPostingClient().getCellphone())) {
                messageService.sendMessage(projectComplete.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3194, messageVariablesFor);
            }
        }

        return CommonResponse.ok();
    }
}
