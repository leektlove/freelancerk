package com.freelancerk.controller.view;

import com.freelancerk.TimeUtil;
import com.freelancerk.controller.BaseController;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.Purchase;
import com.freelancerk.domain.repository.*;
import com.freelancerk.domain.specification.ProjectSpecifications;
import com.freelancerk.domain.specification.UserSpecifications;
import com.freelancerk.io.ContestOrdering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/contest")
public class AdminContestViewController extends BaseController  {

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private PurchaseRepository purchaseRepository;
    private CategoryRepository categoryRepository;
    private ProjectBidRepository projectBidRepository;
    private ProjectItemTicketRepository projectItemTicketRepository;

    @Autowired
    public AdminContestViewController(UserRepository userRepository, ProjectRepository projectRepository, PurchaseRepository purchaseRepository,
                                      ProjectBidRepository projectBidRepository, CategoryRepository categoryRepository,
                                      ProjectItemTicketRepository projectItemTicketRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.purchaseRepository = purchaseRepository;
        this.categoryRepository = categoryRepository;
        this.projectBidRepository = projectBidRepository;
        this.projectItemTicketRepository = projectItemTicketRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", "/list"})
    public String getListView(Model model,
                              @RequestParam(value = "ordering", required = false) ContestOrdering ordering,
                              @RequestParam(value = "direction", required = false) Sort.Direction direction,
                              @RequestParam(value = "createdFrom", required = false) String createdAtFrom,
                              @RequestParam(value = "createdTo", required = false) String createdAtTo,
                              @RequestParam(value = "categoryId", required = false) Long categoryId,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "status", required = false) Project.Status status,
                              @RequestParam(value = "bidCountFrom", required = false) Integer bidCountFrom,
                              @RequestParam(value = "bidCountTo", required = false) Integer bidCountTo,
                              @RequestParam(value = "totalPrizeFrom", required = false) Integer totalPrizeFrom,
                              @RequestParam(value = "totalPrizeTo", required = false) Integer totalPrizeTo,
                              @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                              @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize) {

        Page<Project> page = projectRepository.findAll(ProjectSpecifications.filterContestForAdmin(ordering, direction, keyword, categoryId, TimeUtil.convertStrToLocalDateTime(createdAtFrom),
                TimeUtil.convertStrToLocalDateTime(createdAtTo), status, bidCountFrom, bidCountTo, totalPrizeFrom, totalPrizeTo), PageRequest.of(pageNumber, pageSize));

        for (Project item: page) {
            List<Purchase> purchaseList = purchaseRepository.findByProjectIdAndStatus(item.getId(), Purchase.Status.COMPLETED);
            item.setTotalChangedOptionMoney(purchaseList.stream().mapToLong(Purchase::getChargedOptionsAmountOfMoney).sum());
            item.setTotalAmountOfMoney(purchaseList.stream().mapToLong(Purchase::getTotalAmountOfMoney).sum());
            item.setPickedContestEntries(projectBidRepository.findByProjectIdAndBidStatusOrderByPickedRank(item.getId(), ProjectBid.BidStatus.PICKED));
        }

        setPaginationModelData(model, pageNumber, page);
        model.addAttribute("category1st", categoryRepository.findByParentCategoryIsNullOrderBySeqAsc());
        model.addAttribute("page", page);

        return "contest/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/details")
    public String getDetailsView() {

        return "contest/contestDetailBasic";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/add")
    public String getAddView(@RequestParam(value = "clientNameKeyword", required = false) String clientNameKeyword,
                             @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                             @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize,
                             Model model) {

        model.addAttribute("users", userRepository.findAll(UserSpecifications.filterForClient(clientNameKeyword, false), PageRequest.of(pageNumber, pageSize)));
        return "contest/contestAdd";
    }
}
