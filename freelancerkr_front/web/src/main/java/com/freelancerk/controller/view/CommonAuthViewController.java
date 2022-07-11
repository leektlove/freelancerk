package com.freelancerk.controller.view;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth/")
@PropertySource("thirdparty.properties")
@Log
public class CommonAuthViewController {

	@Value("${server.url}") String serverUrl;
	@Value("${kakao.appid}") String kakaoAppId;
	@Value("${facebook.appid}") String facebookAppId;
	@Value("${naver.clientid}") String naverClientId;
	@Value("${naver.clientsecret}") String naverClientSecret;

	@GetMapping("/successLoginClient")
	public String successLoginClient(HttpServletRequest request) {
		if (request.isUserInRole("ROLE_CLIENT")) {
			log.info("클라이언트");
			return String.format("redirect:%s/client/profile", serverUrl);
		} else {
			log.info("비정상 접근");
			return String.format("redirect:%s", serverUrl);
		}
	}
	
	@GetMapping("/successLoginFreelancer")
	public String successLoginFreelancer(HttpServletRequest request) {
		if (request.isUserInRole("ROLE_FREELANCER")) {
			log.info("프리랜서");
			return String.format("redirect:%s/freelancer/profile", serverUrl);
		} else {
			log.info("비정상 접근");
			return String.format("redirect:%s", serverUrl);
		}
	}

	@GetMapping("join")
	public String join() {
		return "auth/join";
	}

	@GetMapping("joinClient")
	public String joinClient(Model model) {
		setDataModel(model);
		return "auth/joinClient";
	}

	@GetMapping("joinFreelancer")
	public String joinFreelancer(Model model) {
		setDataModel(model);
		return "auth/joinFreelnacer";
	}

	@GetMapping("login")
	public String login(Model model,
						@RequestParam(value = "redirectUrl", required = false) String redirectUrl) {

		model.addAttribute("redirectUrl", redirectUrl);
		return "auth/login";
	}
	
	@GetMapping("loginClient")
	public String loginClient(@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
							  Model model) {
		setDataModel(model);
		model.addAttribute("redirectUrl", redirectUrl);
		return "auth/loginClient";
	}

	@GetMapping("loginFreelancer")
	public String loginFreelancer(@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
								  Model model) {
		setDataModel(model);
		model.addAttribute("redirectUrl", redirectUrl);
		return "auth/loginFreelancer";
	}

	@GetMapping("whoAreYou")
	public String whoAreYou() {
		return "auth/whoAreYou";
	}

	@GetMapping("findPassword")
	public String findPassword() {
		return "auth/findPassword";
	}

	private void setDataModel(Model model) {
		model.addAttribute("kakaoAppId", kakaoAppId);
		model.addAttribute("facebookAppId", facebookAppId);
		model.addAttribute("naverClientId", naverClientId);
		model.addAttribute("naverClientSecret", naverClientSecret);
	}

}
