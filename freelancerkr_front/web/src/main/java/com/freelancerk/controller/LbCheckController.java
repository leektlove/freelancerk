package com.freelancerk.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LbCheckController {

    @RequestMapping(method = RequestMethod.GET, value = "/lb-checks")
    public String lbCheck() {
        return "alive";
    }
}
