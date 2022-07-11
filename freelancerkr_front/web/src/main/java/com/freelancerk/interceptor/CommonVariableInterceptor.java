package com.freelancerk.interceptor;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.service.ProjectService;
import com.freelancerk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class CommonVariableInterceptor implements HandlerInterceptor {

    @Value("${server.url}") String serverUrl;
    @Value("${iamport.merchantId}") String iamportMerchantId;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            modelAndView.addObject("serverUrl", serverUrl);
            modelAndView.addObject("iamportMerchantId", iamportMerchantId);
            if (auth == null || !(auth instanceof User)) {
                modelAndView.addObject("totalUserCount", userRepository.count());
                modelAndView.addObject("totalFKProjectCount", projectRepository.countByStatusNotIn(Arrays.asList(Project.Status.VOLATILE, Project.Status.TEMP)));
                modelAndView.addObject("isLoggedIn", false);
                modelAndView.addObject("loginAsClient", false);
                return;
            }

            boolean isLoggedIsAsClient = ((User) SecurityContextHolder.getContext().getAuthentication()).isLoginAsClient();
            User user = userRepository.getOne(((User) auth).getId());
            user.setPoints(userService.getPoints(user.getId(), isLoggedIsAsClient?User.Role.ROLE_CLIENT:User.Role.ROLE_FREELANCER));

            if (isLoggedIsAsClient) {
                modelAndView.addObject("remainEscrow", projectService.getRemainEscrow(((User) auth).getId()));
            }
            modelAndView.addObject("totalUserCount", userRepository.count());
            modelAndView.addObject("totalFKProjectCount", projectRepository.count());
            modelAndView.addObject("user", user);
            modelAndView.addObject("isLoggedIn", true);
            modelAndView.addObject("loginAsClient", ((User) SecurityContextHolder.getContext().getAuthentication()).isLoginAsClient());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
