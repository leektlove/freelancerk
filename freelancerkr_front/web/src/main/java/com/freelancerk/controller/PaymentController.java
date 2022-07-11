package com.freelancerk.controller;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.Purchase;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.PurchaseService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "결제관리", description = "결제 관리 조회 등")
@RestController
public class PaymentController extends RootController {

    private PurchaseService purchaseService;
    private ProjectRepository projectRepository;

    @Autowired
    public PaymentController(PurchaseService purchaseService, ProjectRepository projectRepository) {
        this.purchaseService = purchaseService;
        this.projectRepository = projectRepository;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/projects/{projectId}/purchase-record")
    public CommonResponse deletePurchase(@PathVariable("projectId") Long projectId) {
        Project project = projectRepository.getOne(projectId);
        project.setPurchaseRecordDeleted(true);
        projectRepository.save(project);

        return CommonResponse.ok();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/purchases/administration-ids")
    public String createAdministrationId(@RequestParam(value = "projectId", required = false) Long projectId,
                                         @RequestParam(value = "pickMeUpId", required = false) Long pickMeUpId,
                                         @RequestParam(value = "type") Purchase.Type type,
                                         @RequestParam(value = "totalDiscountOptionPrice", required = false) Integer totalDiscountOptionPrice,
                                         @RequestParam(value = "supplyAmountOfMoney") Integer supplyAmountOfMoney,
                                         @RequestParam(value = "vatAmountOfMoney") Integer vatAmountOfMoney,
                                         @RequestParam(value = "totalAmountOfMoney") Integer totalAmountOfMoney,
                                         @RequestParam("optionIdsCountMap") String optionIdsCountMap,
                                         @RequestParam(value = "optionAmountOfMoney", required = false) Integer optionAmountOfMoney,
                                         @RequestParam(value = "mode", required = false) String mode,
                                         @RequestParam(value = "usePoint", required = false) Integer usePoint) {
        return purchaseService.makeAdministrationId(getSessionUserId(), projectId, optionAmountOfMoney, supplyAmountOfMoney,
                totalDiscountOptionPrice, vatAmountOfMoney, totalAmountOfMoney, type, optionIdsCountMap, usePoint, mode);
    }
}
