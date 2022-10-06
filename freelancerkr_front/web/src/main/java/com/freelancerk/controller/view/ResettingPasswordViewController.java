package com.freelancerk.controller.view;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.freelancerk.domain.ResettingPassword;
import com.freelancerk.domain.repository.ResettingPasswordRepository;

@Controller
public class ResettingPasswordViewController {

    private ResettingPasswordRepository resettingPasswordRepository;

    @Autowired
    public ResettingPasswordViewController(ResettingPasswordRepository resettingPasswordRepository) {
        this.resettingPasswordRepository = resettingPasswordRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/public/resetting-for-password")
    public String getResettingPasswordView(@RequestParam("token") String token, Model model) {
        ResettingPassword resettingPassword = resettingPasswordRepository.findByTokenAndInvalidFalseAndUsedFalseAndExpiredAtAfter(token, LocalDateTime.now());
        if (resettingPassword == null) {
            return "expired";
        }
        model.addAttribute("token", token);

        return "resetting-password";
    }
}
