package com.freelancerk.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.RobotsTxt;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.PickMeUpRepository;
import com.freelancerk.domain.repository.RobotsTxtRepository;

@RestController
public class RobotController {

    private PickMeUpRepository pickMeUpRepository;
    private RobotsTxtRepository robotsTxtRepository;

    @Autowired
    public RobotController(PickMeUpRepository pickMeUpRepository, RobotsTxtRepository robotsTxtRepository) {
        this.pickMeUpRepository = pickMeUpRepository;
        this.robotsTxtRepository = robotsTxtRepository;
    }

    @GetMapping("/robots.txt")
    public String robots() {
        List<RobotsTxt> allRobotTxts = robotsTxtRepository.findAll();
        List<String> list = allRobotTxts.stream().filter(r -> StringUtils.isNotEmpty(r.getContent())).map(RobotsTxt::getContent).collect(Collectors.toList());
        List<User> users = allRobotTxts.stream().filter(r -> r.getUser() != null).map(RobotsTxt::getUser).collect(Collectors.toList());
        List<PickMeUp> pickMeUps = pickMeUpRepository.findByUserIdIn(users.stream().map(User::getId).collect(Collectors.toList()));
        String contents = "User-agent: *\n";
        contents += String.join("\n", list);
        List<String> pickMeUpDisallows = new ArrayList<>();
        for (PickMeUp pickMeUp: pickMeUps) {
            pickMeUpDisallows.add(String.format("Disallow: /portfolios/%d/details", pickMeUp.getId()));
        }
        contents += String.join("\n", pickMeUpDisallows);
        List<String> userProfileDisallows = new ArrayList<>();
        for (User user: users) {
            userProfileDisallows.add(String.format("Disallow: /freelancer/profile/%d", user.getId()));
        }
        contents += String.join("\n", userProfileDisallows);
        return contents;
    }

    @GetMapping("/google04e668c95b3d5739.html")
    public String googleVerification() {
        return "google-site-verification: google04e668c95b3d5739.html";
    }
}
