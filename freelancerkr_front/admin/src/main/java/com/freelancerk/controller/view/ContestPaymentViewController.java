package com.freelancerk.controller.view;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.Purchase;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@RequestMapping("contestPayment")
@Controller
public class ContestPaymentViewController {

    private ProjectRepository projectRepository;
    private PurchaseRepository purchaseRepository;

    @Autowired
    public ContestPaymentViewController(ProjectRepository projectRepository, PurchaseRepository purchaseRepository) {
        this.projectRepository = projectRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", "/contestList"})
    public String getContestListView(Model model,
                                     @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                     @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize) {

        Page<Project> page = projectRepository.findByProjectTypeAndStatusIn(
                Project.Type.CONTEST,
                Arrays.asList(Project.Status.IN_PROGRESS, Project.Status.COMPLETED, Project.Status.CANCELLED),
                PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

        for (Project item: page) {
            List<Purchase> purchaseList = purchaseRepository.findByProjectIdAndStatus(item.getId(), Purchase.Status.COMPLETED);
            item.setTotalAmountOfMoney(purchaseList.stream().mapToLong(Purchase::getTotalAmountOfMoney).sum());
        }

        model.addAttribute("page", page);

        return "payment/contestList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/contest/{id}/details")
    public String getContestDetailView() {

        return "payment/contestDetail";
    }
}
