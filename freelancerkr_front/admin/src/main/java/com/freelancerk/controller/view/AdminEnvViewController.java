package com.freelancerk.controller.view;

import com.freelancerk.domain.Category;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(value = "/env")
@Controller
public class AdminEnvViewController {

    private KeywordRepository keywordRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public AdminEnvViewController(KeywordRepository keywordRepository, CategoryRepository categoryRepository) {
        this.keywordRepository = keywordRepository;
        this.categoryRepository = categoryRepository;
    }

    @RequestMapping(method = GET, value = {"/basic", "/"})
    public String getEnvBasicView() {

        return "env/basic";
    }

    @RequestMapping(method = GET, value = "/categoryList")
    public String getEnvCategoryList(Model model) {
        List<Category> topCategoryList = categoryRepository.findByParentCategoryIsNullOrderBySeqAsc();
        for (Category category: topCategoryList) {
            category.setChildren(categoryRepository.findByParentCategoryIdOrderBySeqAsc(category.getId()));
        }

        model.addAttribute("topCategoryList", topCategoryList);

        return "env/categoryList";
    }

    @RequestMapping(method = GET, value = "/categoryAdd")
    public String getEnvCategoryAddView(@RequestParam(value = "parentCategoryId", required = false) Long parentCategoryId,
                                        Model model) {

        model.addAttribute("form", new Category());
        model.addAttribute("parentCategory", parentCategoryId != null?
                categoryRepository.getOne(parentCategoryId):null);

        return "env/categoryAdd";
    }

    @RequestMapping(method = GET, value = "/categoryModify")
    public String getEnvCategoryModifyView(Long categoryId, Model model) {

        model.addAttribute("category", categoryRepository.getOne(categoryId));

        return "env/categoryModify";
    }

    @RequestMapping(method = GET, value = "/contestList")
    public String getEnvContestListView() {

        return "env/contestList";
    }

    @RequestMapping(method = GET, value = "/contestAdd")
    public String getEnvContestAddView() {

        return "env/contestAdd";
    }

    @RequestMapping(method = GET, value = "/staffList")
    public String getStaffList() {

        return "env/staffList";
    }

    @RequestMapping(method = GET, value = "/keywordList")
    public String getKeywordList(Model model) {
        List<Category> topCategoryList = categoryRepository.findByParentCategoryIsNullOrderBySeqAsc();
        List<Category> secondTopCategoryList = new ArrayList<>();
        for (Category category: topCategoryList) {
            secondTopCategoryList.addAll(categoryRepository.findByParentCategoryIdOrderBySeqAsc(category.getId()));
        }

        for (Category category: secondTopCategoryList) {
            category.setPopularKeywords(keywordRepository.findTop30ByCategoryParentCategoryIdOrderByUsageCountDesc(category.getId()));
        }
        model.addAttribute("secondTopCategoryList", secondTopCategoryList);

        return "env/keywordList";
    }
}
