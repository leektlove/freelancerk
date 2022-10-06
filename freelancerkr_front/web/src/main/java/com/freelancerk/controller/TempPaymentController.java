package com.freelancerk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freelancerk.domain.TempPayment;
import com.freelancerk.domain.repository.TempPaymentRepository;

@RestController
public class TempPaymentController {

    private TempPaymentRepository tempPaymentRepository;

    @Autowired
    public TempPaymentController(TempPaymentRepository tempPaymentRepository) {
        this.tempPaymentRepository = tempPaymentRepository;
    }

    @PostMapping("/confirm/temp-purchases")
    public void tempPurchases(String imp_uid, String merchant_uid, Long price) {
        TempPayment tempPayment = new TempPayment();
        tempPayment.setMid(merchant_uid);
        tempPayment.setImpUid(imp_uid);
        tempPayment.setPrice(price);
        tempPaymentRepository.save(tempPayment);
    }
}
