package com.freelancerk.controller.view;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.freelancerk.TimeUtil;
import com.freelancerk.controller.RootController;
import com.freelancerk.domain.Message;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectItemTicket;
import com.freelancerk.domain.ProjectPointLog;
import com.freelancerk.domain.ProjectProductItemType;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.MessageRepository;
import com.freelancerk.domain.repository.ProjectItemTicketRepository;
import com.freelancerk.domain.repository.ProjectPointLogRepository;
import com.freelancerk.domain.repository.ProjectProductItemTypeRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.domain.specification.ProjectItemTicketSpecifications;
import com.freelancerk.domain.specification.ProjectSpecifications;
import com.freelancerk.service.ProjectService;

@Controller
@RequestMapping("/client/profile")
public class ClientProfileViewController extends RootController {

    private ProjectService projectService;
    private UserRepository userRepository;
    private MessageRepository messageRepository;
    private ProjectRepository projectRepository;
    private ProjectPointLogRepository projectPointLogRepository;
    private ProjectItemTicketRepository projectItemTicketRepository;
    private ProjectProductItemTypeRepository projectProductItemTypeRepository;

    @Autowired
    public ClientProfileViewController(ProjectService projectService, MessageRepository messageRepository,
                                       UserRepository userRepository, ProjectRepository projectRepository,
                                       ProjectProductItemTypeRepository projectProductItemTypeRepository,
                                       ProjectItemTicketRepository projectItemTicketRepository,
                                       ProjectPointLogRepository projectPointLogRepository) {
        this.projectService = projectService;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectProductItemTypeRepository = projectProductItemTypeRepository;
        this.projectItemTicketRepository = projectItemTicketRepository;
        this.projectPointLogRepository = projectPointLogRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/index")
    public String getClientProfileView(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = getSessionUserId();

        Page<Project> page = projectRepository.findAll(ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.POSTED, null, null),
                PageRequest.of(0, 12, new Sort(Sort.Direction.DESC, "createdAt")));

        List<ProjectProductItemType> priorityItems = projectProductItemTypeRepository.findByPriorityTrue();

        for (Project project: page) {

            List<ProjectProductItemType.Code> projectProductItemCodes = projectItemTicketRepository.findByProjectIdAndInvalidFalseAndExpiredAtAfterOrderByExpiredAtAsc(project.getId(), LocalDateTime.now())
                    .stream().map(ProjectItemTicket::getProjectProductItemType).map(ProjectProductItemType::getCode).collect(Collectors.toList());

            project.setUrgency(projectProductItemCodes.contains(ProjectProductItemType.Code.EXTERNAL_URGENT) || projectProductItemCodes.contains(ProjectProductItemType.Code.INTERNAL_URGENT));
            project.setPremium(
                    projectItemTicketRepository.findAll(ProjectItemTicketSpecifications.filter(priorityItems, null, false, getSessionUserId(), false)).size() > 0
            );
        }

        Page<Message> messagePage = messageRepository.findByUserId(userId, PageRequest.of(0, 5, new Sort(Sort.Direction.DESC, "id")));
        List<Message> messages = messagePage.getContent();
        model.addAttribute("totalProjectCount", projectRepository.count(ProjectSpecifications.filter(getSessionUserId(), null, null,
                Arrays.asList(Project.Status.POSTED, Project.Status.IN_PROGRESS, Project.Status.CANCELLED, Project.Status.COMPLETED), Project.Type.PROJECT)));
        model.addAttribute("totalContestCount", projectRepository.count(ProjectSpecifications.filter(getSessionUserId(), null, null,
                Arrays.asList(Project.Status.POSTED, Project.Status.IN_PROGRESS, Project.Status.CANCELLED, Project.Status.COMPLETED), Project.Type.CONTEST)));
        model.addAttribute("messagePage", messagePage);
        model.addAttribute("messages", messages);
        if (messages != null && messages.size() > 0) {
            model.addAttribute("topMessageCreatedAt", TimeUtil.convertLocalDateTimeToStr(messages.get(0).getCreatedAt()));
        }
        User user = userRepository.getOne(userId);
        List<ProjectPointLog> pointLogs = projectPointLogRepository.findByUserIdAndAddedPointExpiredAtGreaterThan(user.getId(), LocalDateTime.now());
        user.setPoints(pointLogs.stream().mapToInt(ProjectPointLog::getAddedPoint).sum() - pointLogs.stream().mapToInt(ProjectPointLog::getUsePoint).sum());
        model.addAttribute("page", page);
        model.addAttribute("user", user);
        return "client/profile/view";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/modify")
    public String getClientProfileEditView(Model model,
                                           @RequestParam(value = "after-redirect", required = false) String afterDirect,
                                           @RequestParam(value = "type", required = false) Project.Type type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication).getId();

        model.addAttribute("user", userRepository.getOne(userId));
        try {
            model.addAttribute("afterRedirect", StringUtils.isEmpty(afterDirect)? "/client/profile/index": java.net.URLDecoder.decode(afterDirect, "UTF-8"));
            model.addAttribute("type", type);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "client/profile/modify";

    }

}
