package com.freelancerk.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginViewController {

    @RequestMapping("/login")
    public String getLoginView() {
        return "login";
    }
}
