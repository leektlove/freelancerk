package com.freelancerk.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TempPaymentViewController {

    @GetMapping("/views/temp-payment")
    public String tempPaymentView() {

        return "temp-payment";
    }
}
