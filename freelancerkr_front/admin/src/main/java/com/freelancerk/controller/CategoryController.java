package com.freelancerk.controller;

import com.freelancerk.controller.io.SelectResponse;
import com.freelancerk.controller.io.SelectResponseItem;
import com.freelancerk.domain.Category;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.specification.CategorySpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/categories/for-select")
    public SelectResponse categoryByKeyword(@RequestParam("term") String keyword) {
        List<Category> categories = categoryRepository.findAll(CategorySpecifications.filterForTopCategory(keyword));
        SelectResponse selectResponse = new SelectResponse();
        List<SelectResponseItem> items = new ArrayList<>();
        for (Category category: categories) {
            SelectResponseItem item = new SelectResponseItem();
            item.setId(category.getId());
            item.setText(String.format("%s > %s", category.getParentCategory().getName(), category.getName()));
            items.add(item);
        }
        selectResponse.setResults(items);
        return selectResponse;
    }
}
