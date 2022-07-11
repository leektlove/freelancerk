package com.freelancerk.controller.view;

import com.freelancerk.domain.Inquiry;
import com.freelancerk.domain.repository.InquiryAnswerRepository;
import com.freelancerk.domain.repository.InquiryRepository;
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

@RequestMapping("qna")
@Controller
public class QnaViewController {

    private InquiryRepository inquiryRepository;
    private InquiryAnswerRepository inquiryAnswerRepository;

    @Autowired
    public QnaViewController(InquiryRepository inquiryRepository, InquiryAnswerRepository inquiryAnswerRepository) {
        this.inquiryRepository = inquiryRepository;
        this.inquiryAnswerRepository = inquiryAnswerRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", "/list"})
    public String getListView(Model model,
                              @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                              @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize) {

        Page<Inquiry> page = inquiryRepository.findAll(PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

        for (Inquiry item: page) {
            item.setNumberOfInquiries(inquiryAnswerRepository.countByInquiryId(item.getId()));
        }

        model.addAttribute("page", page);

        return "qna/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/add")
    public String qnaAddView() {

        return "qna/add";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/form")
    public String qnaAnswerView(@PathVariable("id") Long id, Model model) {
        Inquiry inquiry = inquiryRepository.getOne(id);
        inquiry.setAnswers(inquiryAnswerRepository.findByInquiryId(id));

        model.addAttribute("item", inquiry);

        return "qna/answer";
    }
}
