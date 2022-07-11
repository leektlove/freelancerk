package com.freelancerk.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("statistics")
@Controller
public class StatisticsViewController {

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String getListView() {
        return "statistics/statistics";
    }
}
