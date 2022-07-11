package com.freelancerk.controller.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@PropertySource("thirdparty.properties")
public class ClientAuthViewController {

    @Value("${kakao.appid}") String kakaoAppId;
    @Value("${facebook.appid}") String facebookAppId;
    @Value("${naver.clientid}") String naverClientId;
    @Value("${naver.clientsecret}") String naverClientSecret;

    @RequestMapping(method = RequestMethod.GET, value = "/join/client")
    public String getClientAuthJoinView(Model model) {
        setDataModel(model);
        return "auth/joinClient";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/client/mypage")
    public String getClientMyPageView(Model model) {

        return "client/mypage";
    }

    private void setDataModel(Model model) {
        model.addAttribute("kakaoAppId", kakaoAppId);
        model.addAttribute("facebookAppId", facebookAppId);
        model.addAttribute("naverClientId", naverClientId);
        model.addAttribute("naverClientSecret", naverClientSecret);
    }
}
