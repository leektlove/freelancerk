package com.freelancerk.controller.view;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.freelancerk.domain.User;

@Controller
@PropertySource("thirdparty.properties")
public class FreelancerAuthViewController {

    @Value("${kakao.appid}") String kakaoAppId;
    @Value("${facebook.appid}") String facebookAppId;
    @Value("${naver.clientid}") String naverClientId;
    @Value("${naver.clientsecret}") String naverClientSecret;

    @RequestMapping(method = RequestMethod.GET, value = "/join/freelancer")
    public String getClientJoinView(Model model) {
        setDataModel(model);
        return "auth/joinFreelancer";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/join/fplus")
    public String getClientJoinFPlus(Model model,
    		 			HttpServletRequest request) {
        setDataModel(model);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("loggedIn", authentication instanceof User);
        return "auth/joinFPlus";
    }
    
//    @RequestMapping(method = RequestMethod.GET, value = "/join/fplus")
//    public String getClientJoinFPlus(Model model,
//    		 			HttpServletRequest request,
//    		 			@RequestParam(value = "projectId", required = false) Long projectId,
//						 @RequestParam(value = "contentType", defaultValue = "IMAGE") PickMeUp.ContentType contentType) {
//    	
//    	PickMeUp pickMeUp = new PickMeUp();
// 		pickMeUp.setHits(0);
// 		pickMeUp.setContentType(contentType);
//
// 		model.addAttribute("mode", "OPEN");
// 		model.addAttribute("remainFreeChargeProductCount",1);
// 		model.addAttribute("pickMeUp", pickMeUp);
// 		model.addAttribute("productList", freelancerPayService.getProductItemTypeList(FreelancerProductItemType.UsageType.PICK_ME_UP.name()));
// 		model.addAttribute("projectId", projectId);
// 		
//        setDataModel(model);	
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		model.addAttribute("loggedIn", authentication instanceof User);
//        return "auth/joinFPlus";
//    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/login/fplus")
    public String loginFPlus(Model model,HttpServletRequest request) {
        setDataModel(model);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("loggedIn", authentication instanceof User);
        return "auth/loginFPlus";
    }

    private void setDataModel(Model model) {
        model.addAttribute("kakaoAppId", kakaoAppId);
        model.addAttribute("facebookAppId", facebookAppId);
        model.addAttribute("naverClientId", naverClientId);
        model.addAttribute("naverClientSecret", naverClientSecret);
    }
}
