package com.freelancerk.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.freelancerk.domain.MenuHit;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.MenuHitRepository;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MenuHitInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private MenuHitRepository menuHitRepository;
    @Autowired
    private ProjectBidRepository projectBidRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof User)) {
            return true;
        }

        if ("POST".equalsIgnoreCase(request.getMethod())) return true;

        String uri = request.getRequestURI();

        for (MenuHit.MenuCode menuCode: MenuHit.MenuCode.values()) {
            if (menuCode.getUri().startsWith(uri)) {
                MenuHit menuHit = new MenuHit();
                menuHit.setMenuCode(menuCode);
                menuHit.setUser(userRepository.getOne(((User) auth).getId()));
                menuHitRepository.save(menuHit);
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) return;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof User)) {
            return;
        }

        long userId = ((User) auth).getId();

        if (((User) SecurityContextHolder.getContext().getAuthentication()).isLoginAsClient()) {
            try {
                Project lastPostedProject = projectRepository.findTop1ByPostingClientIdAndStatusAndInvalidFalseOrderByPostingStartAtDesc(userId, Project.Status.POSTED);
                Project lastInProgressProject = projectRepository.findTop1ByPostingClientIdAndStatusAndInvalidFalseOrderByStartAtDesc(userId, Project.Status.IN_PROGRESS);

                boolean existAlarm = false;
                boolean existsUnreadPostedProject = false;
                boolean existsUnreadInProgressProject = false;

                existsUnreadPostedProject = lastPostedProject != null;
                modelAndView.addObject("existsUnreadPostedProject", existsUnreadPostedProject);

                existsUnreadInProgressProject = lastInProgressProject != null;
                modelAndView.addObject("existsUnreadInProgressProject", existsUnreadInProgressProject);

                existAlarm = existsUnreadPostedProject || existsUnreadInProgressProject;
                modelAndView.addObject("existAlarm", existAlarm);
            } catch (Exception e) {
                log.error("<<< menu noti error", e);
            }
        } else {
            try {
                ProjectBid lastSuccessBid = projectBidRepository.findTop1ByParticipantIdAndBidStatusOrderByCreatedAtDesc(userId, ProjectBid.BidStatus.PICKED);
                MenuHit lastInProgressProjectMenuHit = menuHitRepository.findTop1ByUserIdAndMenuCodeOrderByCreatedAtDesc(userId, MenuHit.MenuCode.FREELANCER_IN_PROGRESS);

                boolean existAlarm = false;
                boolean existsUnreadInProgressProject = false;
                if (lastSuccessBid != null && lastSuccessBid.getSuccessBidAt() != null) {
                    existsUnreadInProgressProject
                            = lastInProgressProjectMenuHit == null || lastSuccessBid.getSuccessBidAt().isAfter(lastInProgressProjectMenuHit.getCreatedAt());
                }
                modelAndView.addObject("existsUnreadInProgressProject", existsUnreadInProgressProject);

                existAlarm = existsUnreadInProgressProject;
                modelAndView.addObject("existAlarm", existAlarm);
            } catch (Exception e) {
                log.error("<<< menu noti error", e);
            }
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof User)) {
            return;
        }

        if ("POST".equalsIgnoreCase(request.getMethod())) return;

        String uri = request.getRequestURI();

        for (MenuHit.MenuCode menuCode: MenuHit.MenuCode.values()) {
            if (menuCode.getUri().startsWith(uri)) {
                MenuHit menuHit = new MenuHit();
                menuHit.setMenuCode(menuCode);
                menuHit.setUser(userRepository.getOne(((User) auth).getId()));
                menuHitRepository.save(menuHit);
            }
        }
    }
}
