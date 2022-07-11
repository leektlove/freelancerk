package com.freelancerk.controller.view;

import com.freelancerk.TimeUtil;
import com.freelancerk.domain.repository.DailyAccessLogRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class AdminViewController {

    private DailyAccessLogRepository dailyAccessLogRepository;

    @Autowired
    public AdminViewController(DailyAccessLogRepository dailyAccessLogRepository) {
        this.dailyAccessLogRepository = dailyAccessLogRepository;
    }

    @RequestMapping(value = "/")
    public String getMainView(@RequestParam(value = "dateFrom", required = false) String dateFromParam,
                              @RequestParam(value = "dateTo", required = false) String dateToParam,
                              Model model) {

        List<Object> couponUsagesHeaderByDay = Arrays.asList("일자", "방문 수");

        List<List<Object>> accessDaily = new ArrayList<>();

        accessDaily.add(couponUsagesHeaderByDay);

        LocalDate dateFrom = StringUtils.isEmpty(dateFromParam)?LocalDate.now().minusDays(15): TimeUtil.convertStrToLocalDate(dateFromParam);
        LocalDate dateTo = StringUtils.isEmpty(dateToParam)?LocalDate.now(): TimeUtil.convertStrToLocalDate(dateToParam);
        dateTo = dateTo.plusDays(1);

        while (dateFrom.isBefore(dateTo)) {
            List<Object> accessByDate = new ArrayList<>();
            accessByDate.add(String.format("%d-%d" ,dateFrom.getMonth().getValue(), dateFrom.getDayOfMonth()));
            accessByDate.add(dailyAccessLogRepository.countByDate(dateFrom));
            dateFrom = dateFrom.plusDays(1);
            accessDaily.add(accessByDate);
        }

        model.addAttribute("accessDaily", accessDaily);

        return "admin";
    }
}
