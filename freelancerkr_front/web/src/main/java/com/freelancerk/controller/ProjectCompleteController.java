package com.freelancerk.controller;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.PaymentToUser;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectComplete;
import com.freelancerk.domain.repository.PaymentToUserRepository;
import com.freelancerk.domain.repository.ProjectCompleteRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.io.ResponseCode;
import com.freelancerk.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ProjectCompleteController extends RootController {

    private MessageService messageService;
    private ProjectRepository projectRepository;
    private PaymentToUserRepository paymentToUserRepository;
    private ProjectCompleteRepository projectCompleteRepository;

    @Autowired
    public ProjectCompleteController(MessageService messageService,
                                     ProjectRepository projectRepository,
                                     PaymentToUserRepository paymentToUserRepository,
                                     ProjectCompleteRepository projectCompleteRepository) {
        this.messageService = messageService;
        this.projectRepository = projectRepository;
        this.paymentToUserRepository = paymentToUserRepository;
        this.projectCompleteRepository = projectCompleteRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/project-completes/request")
    public CommonResponse requestProjectComplete(@RequestParam("projectId") Long projectId) {
        if (paymentToUserRepository.findByProjectIdAndStatus(projectId, PaymentToUser.Status.PAYED).size() == 0) {
            return new CommonResponse.Builder<Void>().responseCode(ResponseCode.FAIL).message("지급 청구 및 승인 내역이 존재하지 않습니다. 지급 청구를 먼저 진행해 주세요.").build();
        }
        if (projectCompleteRepository.findByProjectId(projectId) != null) {
            return new CommonResponse.Builder<Void>().responseCode(ResponseCode.FAIL).message("종료 요청한 내역이 존재합니다.").build();
        }
        ProjectComplete projectComplete = new ProjectComplete();
        projectComplete.setFreelancerRequest(true);
        projectComplete.setFreelancerRequestAt(LocalDateTime.now());
        projectComplete.setProject(projectRepository.getOne(projectId));
        projectCompleteRepository.save(projectComplete);

        return CommonResponse.ok();
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/project-completes/accept")
    public CommonResponse acceptProjectComplete(@RequestParam("projectId") Long projectId) {
        ProjectComplete projectComplete = projectCompleteRepository.findByProjectId(projectId);
        projectComplete.setClientAccept(true);
        projectComplete.setClientAcceptAt(LocalDateTime.now());
        projectCompleteRepository.save(projectComplete);

        Project project = projectComplete.getProject();
        project.setStatus(Project.Status.COMPLETED);
        project.setCompletedAt(LocalDateTime.now());
        projectRepository.save(project);

        Map<String, Object> messageVariablesFor = new HashMap<>();
        messageService.sendMessage(projectComplete.getProject().getContractedFreelancer(), AligoKakaoMessageTemplate.Code.TA_3196, messageVariablesFor);

        return CommonResponse.ok();
    }
}
