package com.freelancerk.controller.view;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.PickMeUpRepository;
import com.freelancerk.domain.specification.PickMeUpSpecifications;
import com.freelancerk.service.PickMeUpService;
import com.freelancerk.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/client/freelancer/profile")
public class ClientFreelancerProfileViewController extends RootController {

    private UserService userService;
    private PickMeUpService pickMeUpService;
    private PickMeUpRepository pickMeUpRepository;

    @Autowired
    public ClientFreelancerProfileViewController(UserService userService, PickMeUpService pickMeUpService,
                                                 PickMeUpRepository pickMeUpRepository) {
        this.userService = userService;
        this.pickMeUpService = pickMeUpService;
        this.pickMeUpRepository = pickMeUpRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public String getFreelancerProfileByIdView(@PathVariable("id") Long userId,
                                               @RequestParam(value = "referer", required = false) String referer, Model model) {

        Page<PickMeUp> page = pickMeUpRepository.findAll(
                PickMeUpSpecifications.filter(null, null,
                        userId, null, null, null, null),
                PageRequest.of(0, 10));

        boolean directDealAvailable = false;
        Long directDetalPickMeUpId = null;
        for (PickMeUp pickMeUp : page) {
            pickMeUpService.setValidTicketItem(pickMeUp);
            if (pickMeUp.isDirectDealAvailable()) {
                directDealAvailable = true;
                directDetalPickMeUpId = pickMeUp.getId();
            }
        }

        model.addAttribute("directDealAvailable", !"bids".equalsIgnoreCase(referer) && directDealAvailable);
        model.addAttribute("directDealPickMeUpId", directDetalPickMeUpId);
        model.addAttribute("portfolios", page.getContent());

        model.addAttribute("freelancer", userService.getById(userId));
        model.addAttribute("showContact", StringUtils.isNotEmpty(referer)&&referer.endsWith("bids"));
        model.addAttribute("readonly", true);


        model.addAttribute("isLoggedIn", isLoggedIn());
        if (isLoggedIn()) {
            model.addAttribute("loginAsClient", ((User) SecurityContextHolder.getContext().getAuthentication()).isLoginAsClient());
        }

        return "freelancer/freelancerProfile";
    }
}
