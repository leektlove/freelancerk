package com.freelancerk.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.FreelancerProductItemTypeRepository;
import com.freelancerk.domain.repository.PickMeUpTicketRepository;
import com.freelancerk.domain.repository.ProjectRepository;

@Controller
@RequestMapping("/client/pickMeUp")
public class ClientPickMeUpController extends RootController {

    private CategoryRepository categoryRepository;
    private PickMeUpTicketRepository pickMeUpTicketRepository;
    private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;
    private ProjectRepository projectRepository;

    @Autowired
    public ClientPickMeUpController(FreelancerProductItemTypeRepository freelancerProductItemTypeRepository,
                                    PickMeUpTicketRepository pickMeUpTicketRepository,
                                    CategoryRepository categoryRepository, ProjectRepository projectRepository) {
        this.pickMeUpTicketRepository = pickMeUpTicketRepository;
        this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
        this.categoryRepository = categoryRepository;
        this.projectRepository = projectRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/suggest")
    public String getClientPickMeUpSuggestView() {

        return "client/pickMeUp/suggest";
    }

    @RequestMapping(method = RequestMethod.GET,  value = "/directMarket")
    public String getDirectMarketView() {

        return "client/pickMeUp/directMarket";
    }
}
