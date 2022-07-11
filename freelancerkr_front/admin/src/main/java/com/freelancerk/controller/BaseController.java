package com.freelancerk.controller;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

public class BaseController {

    protected void setPaginationModelData(Model model, int pageNumber, Page page) {
        int displayPageIndex = 0;
        int displayPageIndexStart = 0;
        int displayPageIndexEnd = 0;

        if (pageNumber >= 2 && pageNumber <= page.getTotalPages() - 3) {
            displayPageIndexStart = pageNumber - 1;
            displayPageIndexEnd = pageNumber + 3;
        } else if (pageNumber < 2) {
            displayPageIndexStart = 0;
            displayPageIndexEnd = page.getTotalPages() <= 5?page.getTotalPages():5;
        } else {
            displayPageIndexStart = pageNumber - 1;
            displayPageIndexEnd = (pageNumber + 3 <= page.getTotalPages())?(pageNumber + 3):page.getTotalPages();
        }
        displayPageIndex = displayPageIndexStart;

        model.addAttribute("displayPageIndex", displayPageIndex);
        model.addAttribute("displayPageIndexEnd", displayPageIndexEnd);
        model.addAttribute("displayPageIndexStart", displayPageIndexStart);
        model.addAttribute("page", page);
        model.addAttribute("pageNumber", pageNumber);
    }
}
