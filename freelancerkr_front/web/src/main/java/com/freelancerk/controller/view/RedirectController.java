package com.freelancerk.controller.view;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.User;
import com.freelancerk.domain.UserMasterTempToken;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.UserMasterTempTokenRepository;
import com.freelancerk.domain.repository.UserRepository;

@Controller
public class RedirectController {

    @Value("${server.url}") String serverUrl;
    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private UserMasterTempTokenRepository userMasterTempTokenRepository;

    @Autowired
    public RedirectController(UserRepository userRepository, ProjectRepository projectRepository, UserMasterTempTokenRepository userMasterTempTokenRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.userMasterTempTokenRepository = userMasterTempTokenRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/redirect/users/{id}")
    public String redirectUser(@PathVariable("id") Long id, @RequestParam("role") User.Role userRole) {
        final User authenticatedUser = userRepository.getOne(id);
        authenticatedUser.setLoginRole(userRole);
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

        return String.format("redirect:%s/main", serverUrl);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/redirect/projects/users/{id}")
    public String getProjectDetailsForUser(@PathVariable("id") Long id) {
        final User authenticatedUser = userRepository.getOne(id);
        authenticatedUser.setLoginRole(User.Role.ROLE_CLIENT);
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

        UserMasterTempToken userMasterTempToken = new UserMasterTempToken();
        userMasterTempToken.setToken(UUID.randomUUID().toString());
        userMasterTempToken.setExpiredAt(LocalDateTime.now().plusHours(1));
        userMasterTempTokenRepository.save(userMasterTempToken);
        return String.format("redirect:%s/client/posting/project?token=" + userMasterTempToken.getToken(), serverUrl);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/redirect/projects/{projectId}")
    public String getProjectModifyView(@PathVariable("projectId") Long projectId) {
        Project project = projectRepository.getOne(projectId);
        final User authenticatedUser = userRepository.getOne(project.getPostingClient().getId());
        authenticatedUser.setLoginRole(User.Role.ROLE_CLIENT);
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

        UserMasterTempToken userMasterTempToken = new UserMasterTempToken();
        userMasterTempToken.setToken(UUID.randomUUID().toString());
        userMasterTempToken.setExpiredAt(LocalDateTime.now().plusHours(1));
        userMasterTempTokenRepository.save(userMasterTempToken);
        return String.format("redirect:%s/client/posting/project/uuid?id=%d&mode=MODIFY&token=null&isLoggedIn=true", serverUrl, project.getId());
    }


    @RequestMapping(method = RequestMethod.GET, value = "/redirect/contests/users/{id}")
    public String getContestDetailsForUser(@PathVariable("id") Long id) {
        final User authenticatedUser = userRepository.getOne(id);
        authenticatedUser.setLoginRole(User.Role.ROLE_CLIENT);
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

        UserMasterTempToken userMasterTempToken = new UserMasterTempToken();
        userMasterTempToken.setToken(UUID.randomUUID().toString());
        userMasterTempToken.setExpiredAt(LocalDateTime.now().plusHours(1));
        userMasterTempTokenRepository.save(userMasterTempToken);
        return String.format("redirect:%s/client/posting/contest?token=" + userMasterTempToken.getToken(), serverUrl);
    }
}
