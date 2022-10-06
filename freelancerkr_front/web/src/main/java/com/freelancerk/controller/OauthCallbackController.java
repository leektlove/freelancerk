package com.freelancerk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.freelancerk.domain.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class OauthCallbackController {

    @RequestMapping(method = RequestMethod.GET, value = "/callback/oauth2/naver")
    public String getNaverOauth2Callback(Model model, @RequestParam("role") User.Role role,
                                         @RequestParam("type") String type) {

        model.addAttribute("role", role.name());
        model.addAttribute("type", type);

        return "login".equalsIgnoreCase(type)?"auth/oauthLoginCallbackNaver":"auth/oauthSignUpCallbackNaver";
    }
}
