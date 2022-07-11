package com.freelancerk.controller.view;

import com.freelancerk.controller.BaseController;
import com.freelancerk.domain.Notice;
import com.freelancerk.domain.repository.NoticeRepository;
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

@RequestMapping("notice")
@Controller
public class NoticeViewController extends BaseController {

    private NoticeRepository noticeRepository;

    @Autowired
    public NoticeViewController(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", "/list"})
    public String getListView(Model model,
                              @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                              @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize) {

        Page<Notice> page = noticeRepository.findAll(PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

        model.addAttribute("page", page);
        setPaginationModelData(model, pageNumber, page);

        return "notice/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/form")
    public String getForm(Model model) {

        model.addAttribute("item", new Notice());

        return "notice/form";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/form")
    public String getDetailForm(@PathVariable("id") Long id, Model model) {

        model.addAttribute("item", noticeRepository.getOne(id));

        return "notice/form";

    }
}
