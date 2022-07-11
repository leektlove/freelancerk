package com.freelancerk.controller;

import com.freelancerk.domain.User;
import com.freelancerk.exception.NotLoggedInException;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

public class RootController {

    protected Long getSessionUserId() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            throw NotLoggedInException.getInstance();
        }
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        return userId;
    }

    protected boolean isLoggedIn() {
        return  SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                //when Anonymous Authentication is enabled
                !(SecurityContextHolder.getContext().getAuthentication()
                        instanceof AnonymousAuthenticationToken);
    }

    protected boolean isLoggedIsAsClient() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            throw NotLoggedInException.getInstance();
        }
        return ((User) SecurityContextHolder.getContext().getAuthentication()).isLoginAsClient();
    }

    protected void setPaginationModelData(Model model, int pageNumber, Page page) {
        int displayPageIndex = 0;
        int displayPageIndexStart = 0;
        int displayPageIndexEnd = 0;

        if (pageNumber >= 2 && pageNumber <= page.getTotalPages() - 3) {
            displayPageIndexStart = pageNumber - 1;
            displayPageIndexEnd = pageNumber + 3;
        } else if (pageNumber < 2) {
            displayPageIndexStart = 0;
            displayPageIndexEnd = page.getTotalPages() <= 5?page.getTotalPages():5;
        } else {
            displayPageIndexStart = pageNumber - 1;
            displayPageIndexEnd = (pageNumber + 3 <= page.getTotalPages())?(pageNumber + 3):page.getTotalPages();
        }
        displayPageIndex = displayPageIndexStart;

        model.addAttribute("displayPageIndex", displayPageIndex);
        model.addAttribute("displayPageIndexEnd", displayPageIndexEnd);
        model.addAttribute("displayPageIndexStart", displayPageIndexStart);
        model.addAttribute("page", page);
        model.addAttribute("pageNumber", pageNumber);
    }
}
