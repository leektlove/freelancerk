package com.freelancerk.controller.view;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.FreelancerProductItemType;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.PickMeUpTicket;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.FreelancerProductItemTypeRepository;
import com.freelancerk.domain.repository.PickMeUpTicketRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.specification.PickMeUpTicketSpecifications;
import com.freelancerk.domain.specification.ProjectSpecifications;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

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
