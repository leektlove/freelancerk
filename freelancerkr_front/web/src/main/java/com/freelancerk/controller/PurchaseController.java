package com.freelancerk.controller;

import com.freelancerk.domain.Purchase;
import com.freelancerk.domain.repository.PurchaseRepository;
import com.freelancerk.io.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseController extends RootController {

    private PurchaseRepository purchaseRepository;

    @Autowired
    public PurchaseController(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @GetMapping("/purchases/projects/{projectId}")
    public CommonResponse<Purchase> get(@PathVariable long projectId) {

        return new CommonResponse.Builder<Purchase>()
                .data(purchaseRepository.findByProjectIdAndStatusAndType(
                        projectId, Purchase.Status.COMPLETED, Purchase.Type.CONTEST).iterator().next()).build();

    }
}
