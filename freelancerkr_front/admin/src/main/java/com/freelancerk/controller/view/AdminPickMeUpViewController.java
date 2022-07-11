package com.freelancerk.controller.view;

import com.freelancerk.controller.BaseController;
import com.freelancerk.domain.DirectDeal;
import com.freelancerk.domain.FreelancerProductItemType;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.DirectDealRepository;
import com.freelancerk.domain.repository.FreelancerProductItemTypeRepository;
import com.freelancerk.domain.repository.PickMeUpRepository;
import com.freelancerk.domain.specification.PickMeUpSpecifications;
import com.freelancerk.service.PickMeUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping(value = "/pickMeUp")
@Controller
public class AdminPickMeUpViewController extends BaseController {

    private PickMeUpService pickMeUpService;
    private PickMeUpRepository pickMeUpRepository;
    private CategoryRepository categoryRepository;
    private DirectDealRepository directDealRepository;
    private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;

    @Autowired
    public AdminPickMeUpViewController(PickMeUpService pickMeUpService, PickMeUpRepository pickMeUpRepository,
                                       CategoryRepository categoryRepository, DirectDealRepository directDealRepository,
                                       FreelancerProductItemTypeRepository freelancerProductItemTypeRepository) {
        this.pickMeUpService = pickMeUpService;
        this.pickMeUpRepository = pickMeUpRepository;
        this.categoryRepository = categoryRepository;
        this.directDealRepository = directDealRepository;
        this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/list", "/"})
    public String getAdminPickMeUpListView(Model model,
                                           @RequestParam(value = "keyword", required = false) String keyword,
                                           @RequestParam(value = "category1stId", required = false) Long category1stId,
                                           @RequestParam(value = "category2ndId", required = false) String category2ndId,
                                           @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                           @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {

        Page<PickMeUp> page = pickMeUpRepository.findAll(
                PickMeUpSpecifications.filterForAdmin(keyword, category1stId, category2ndId),
                PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

        for (PickMeUp pickMeUp: page) {
            pickMeUpService.setValidTicketItem(pickMeUp);
        }

        model.addAttribute("allCategory1st", categoryRepository.findByParentCategoryIsNullOrderBySeqAsc());
        model.addAttribute("productItems", freelancerProductItemTypeRepository.findByUsageTypeContainingAndValidTrue(
                FreelancerProductItemType.UsageType.PICK_ME_UP.name(),
                Sort.by(Sort.Direction.ASC, "seq")));
        model.addAttribute("page", page);

        setPaginationModelData(model, pageNumber, page);

        return "pickmeup/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public String getAdminPickMeUpDetailView(Model model, @PathVariable long id) {
        PickMeUp pickMeUp = pickMeUpRepository.getOne(id);
        List<DirectDeal> directDeals = directDealRepository.findByPickMeUpId(id);

        model.addAttribute("item", pickMeUp);
        model.addAttribute("directDeals", directDeals);

        return "pickmeup/pickmeupDetail";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/add")
    public String getAdminPickMeUpAddView() {

        return "pickmeup/pickmeupAdd";
    }
}
