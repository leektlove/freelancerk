package com.freelancerk.controller.view;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.PickMeUpTicket;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.SearchKeyword;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.PickMeUpTicketRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.SearchKeywordRepository;
import com.freelancerk.domain.specification.PickMeUpTicketSpecifications;
import com.freelancerk.domain.specification.ProjectSpecifications;
import com.freelancerk.service.PickMeUpService;

@Controller
public class PickMeUpViewController extends RootController  {

    private PickMeUpService pickMeUpService;
    private ProjectRepository projectRepository;
    private CategoryRepository categoryRepository;
    private SearchKeywordRepository searchKeywordRepository;
    private PickMeUpTicketRepository pickMeUpTicketRepository;

    @Autowired
    public PickMeUpViewController(PickMeUpService pickMeUpService, ProjectRepository projectRepository,
                                  CategoryRepository categoryRepository,
                                  SearchKeywordRepository searchKeywordRepository,
                                  PickMeUpTicketRepository pickMeUpTicketRepository) {
        this.pickMeUpService = pickMeUpService;
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.searchKeywordRepository = searchKeywordRepository;
        this.pickMeUpTicketRepository = pickMeUpTicketRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/view/pick-me-ups")
    public String getPickMeUpListView(@RequestParam(value = "category1stId", required = false) Long category1stId,
                                      @RequestParam(value = "category2ndId", required = false) Long category2ndId,
                                      @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "24") Integer pageSize,
                                      @RequestParam(value = "directMarketAvailable", required = false, defaultValue = "false") boolean directMarketAvailable,
                                      @RequestParam(value = "keyword", required = false) String keyword,
                                      @RequestParam(value = "afterPosting", required = false, defaultValue = "false") Boolean afterPosting,
                                      Model model) {
        Page<PickMeUpTicket> page = pickMeUpTicketRepository.findAll(
                PickMeUpTicketSpecifications.filter(
                        category1stId != null ? categoryRepository.getOne(category1stId) : null,
                        category2ndId != null ? categoryRepository.getOne(category2ndId) : null,
                        keyword, null, directMarketAvailable),
                PageRequest.of(pageNumber, pageSize));
        List<PickMeUp> pickMeUps
                = page.getContent()
                .stream()
                .map(PickMeUpTicket::getPickMeUp)
                .collect(Collectors.toList());

        long totalCount = pickMeUpTicketRepository.count(PickMeUpTicketSpecifications.filter(
                category1stId != null ? categoryRepository.getOne(category1stId) : null,
                category2ndId != null ? categoryRepository.getOne(category2ndId) : null,
                keyword, null, directMarketAvailable));
        for (PickMeUp pickMeUp : pickMeUps) {
            pickMeUpService.setValidTicketItem(pickMeUp);
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("directMarketAvailable", directMarketAvailable);
        model.addAttribute("category1stId", category1stId);
        model.addAttribute("category2ndId", category2ndId);
        model.addAttribute("totalPages", (totalCount / pageSize) + 1);

        if (StringUtils.isNotEmpty(keyword)) {
            SearchKeyword searchKeyword = searchKeywordRepository.findTop1ByKeyword(keyword);
            if (searchKeyword == null) {
                searchKeyword = new SearchKeyword();
                searchKeyword.setKeyword(keyword);
            }
            searchKeyword.setCount(searchKeyword.getCount() + 1);
            searchKeywordRepository.save(searchKeyword);
        }

        model.addAttribute("pickMeUpIds", StringUtils.join(pickMeUps.stream().map(PickMeUp::getId).collect(Collectors.toList()), ","));
        model.addAttribute("pickMeUps", pickMeUps);
        model.addAttribute("page", page);
        model.addAttribute("topKeywords", searchKeywordRepository.findTop10ByOrderByCountDesc().stream().map(SearchKeyword::getKeyword).collect(Collectors.toList()));

        model.addAttribute("category1stList", categoryRepository.findByParentCategoryIsNullOrderBySeqAsc());
        if (isLoggedIn() && isLoggedIsAsClient()) {
            model.addAttribute("postedProjectList", projectRepository.findAll(ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.POSTED, null, null)));
        }

        model.addAttribute("afterPosting", afterPosting);

        return "client/pickMeUp/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/pick-me-ups")
    public String getPickMeUp(@RequestParam(value = "category1stId", required = false) Long category1stId,
                              @RequestParam(value = "category2ndId", required = false) Long category2ndId,
                              @RequestParam(value = "directMarketAvailable", required = false, defaultValue = "false") boolean directMarketAvailable,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "removeFirstPage", required = false) Boolean removeFirstPage,
                              @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "24") Integer pageSize,
                              Model model) {
        Page<PickMeUpTicket> page = pickMeUpTicketRepository.findAll(
                PickMeUpTicketSpecifications.filter(
                        category1stId != null ? categoryRepository.getOne(category1stId) : null,
                        category2ndId != null ? categoryRepository.getOne(category2ndId) : null,
                        keyword, null, directMarketAvailable),
                PageRequest.of(pageNumber, pageSize));
        List<PickMeUp> pickMeUps
                = page.getContent()
                .stream()
                .map(PickMeUpTicket::getPickMeUp)
                .collect(Collectors.toList());

        if (removeFirstPage != null && removeFirstPage) {
            pickMeUps = pickMeUps.subList(24, pickMeUps.size());
        }

        for (PickMeUp pickMeUp : pickMeUps) {
            pickMeUpService.setValidTicketItem(pickMeUp);
        }
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pickMeUpIds", StringUtils.join(pickMeUps.stream().map(PickMeUp::getId).collect(Collectors.toList()), ","));
        model.addAttribute("pickMeUps", pickMeUps);

        return "portfolios/portfolios";
    }
}