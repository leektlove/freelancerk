package com.freelancerk.interceptor;

import com.freelancerk.domain.DailyAccessLog;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.DailyAccessLogRepository;
import com.freelancerk.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Component
public class AccessRecordInterceptor implements HandlerInterceptor {

    private UserRepository userRepository;
    private DailyAccessLogRepository dailyAccessLogRepository;

    @Autowired
    public AccessRecordInterceptor(UserRepository userRepository, DailyAccessLogRepository dailyAccessLogRepository) {
        this.userRepository = userRepository;
        this.dailyAccessLogRepository = dailyAccessLogRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && StringUtils.isNotEmpty(modelAndView.getViewName()) && !modelAndView.getViewName().startsWith("redirect")) {
            boolean isLoggedIn = SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                    //when Anonymous Authentication is enabled
                    !(SecurityContextHolder.getContext().getAuthentication()
                            instanceof AnonymousAuthenticationToken);
            if (isLoggedIn) {
                Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();

            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
