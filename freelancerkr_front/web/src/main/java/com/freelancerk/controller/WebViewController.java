package com.freelancerk.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api("정책")
@RestController
public class WebViewController {

    @ApiOperation("프리랜서 코리아 이용 약관")
    @RequestMapping(method = RequestMethod.GET, value = "/service-policy")
    public String getServicePolicyView() {

        return "service-policy";
    }

    @ApiOperation("개인정보취급방법")
    @RequestMapping(method = RequestMethod.GET, value = "/privacy-policy")
    public String getPrivacyPolicyView() {

        return "privacy-policy";
    }

    @ApiOperation("에스크로")
    @RequestMapping(method = RequestMethod.GET, value = "/escrow-policy")
    public String getEscrowPolicyView() {

        return "escrow-policy";
    }
}
