package com.freelancerk.controller;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.ProjectRate;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectRateRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.MessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ContestRateController {

    private MessageService messageService;
    private UserRepository userRepository;
    private ProjectBidRepository projectBidRepository;
    private ProjectRateRepository projectRateRepository;

    @Autowired
    public ContestRateController(MessageService messageService, UserRepository userRepository,
                                 ProjectBidRepository projectBidRepository, ProjectRateRepository projectRateRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.projectBidRepository = projectBidRepository;
        this.projectRateRepository = projectRateRepository;
    }

    @ApiOperation("평가하기")
    @RequestMapping(method = RequestMethod.POST, value = "/contests/{contestId}/rates")
    public CommonResponse rateProject(@PathVariable("contestId") Long contestId, @RequestParam("type") ProjectRate.RaterType type,
                                      @RequestParam("ratedUserId") Long ratedUserId, @RequestParam("rateSatisfactory") float rateSatisfactory,
                                      @RequestParam("rateSpeed") String rateSpeed, @RequestParam("rateAccuracy") float rateAccuracy,
                                      @RequestParam("rateReliability") float rateReliability, @RequestParam("repectAttribute") float respectAttribute,
                                      @RequestParam("rateCommunication") float rateCommunication, @RequestParam("comment") String comment) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
//        projectService.rate(userId, ratedUserId, contestId, type, rateSatisfactory, rateAccuracy, rateReliability,
//                rateCommunication, respectAttribute, rateSpeed, comment); // todo
        return CommonResponse.ok();
    }

    @ApiOperation("당선자 평가하기")
    @RequestMapping(method = RequestMethod.POST, value = "/contests/entries/{contestEntryId}/rates")
    public CommonResponse rateContestEntry(@PathVariable("contestEntryId") Long contestEntryId, @RequestParam("type1Rate") int type1Rate,
                                           @RequestParam("type2Rate") int type2Rate, @RequestParam("type3Rate") int type3Rate,
                                           @RequestParam("type4Rate") int type4Rate, @RequestParam("type5Rate") int type5Rate,
                                           @RequestParam(value = "content", required = false) String content) {
        ProjectBid projectBid = projectBidRepository.getOne(contestEntryId);
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        ProjectRate projectRate = new ProjectRate();
        projectRate.setProject(projectBid.getProject());
        projectRate.setRaterType(ProjectRate.RaterType.CLIENT);
        projectRate.setContent(content);
        projectRate.setRaterUser(userRepository.getOne(userId));
        projectRate.setRatedUser(projectBid.getParticipant());
        projectRate.setType1Rate(type1Rate);
        projectRate.setType2Rate(type2Rate);
        projectRate.setType3Rate(type3Rate);
        projectRate.setType4Rate(type4Rate);
        projectRate.setType5Rate(type5Rate);
        projectRateRepository.save(projectRate);

        Map<String, Object> messageVariablesFor = new HashMap<>();
        messageService.sendMessage(projectBid.getParticipant(), AligoKakaoMessageTemplate.Code.TA_3196, messageVariablesFor);

        return CommonResponse.ok();
    }
}
