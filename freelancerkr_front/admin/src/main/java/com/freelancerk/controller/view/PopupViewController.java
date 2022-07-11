package com.freelancerk.controller.view;

import com.freelancerk.domain.Popup;
import com.freelancerk.domain.repository.PopupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("popup")
@Controller
public class PopupViewController {

    private PopupRepository popupRepository;

    @Autowired
    public PopupViewController(PopupRepository popupRepository) {
        this.popupRepository = popupRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", "/list"})
    public String getListView(Model model,
                              @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                              @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize) {

        Page<Popup> page = popupRepository.findAll(PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

        model.addAttribute("page", page);

        return "popup/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/add")
    public String getAddView() {

        return "popup/add";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/modify")
    public String getModifyView() {

        return "popup/modify";
    }
}
