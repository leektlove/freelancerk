package com.freelancerk.controller;

import com.freelancerk.domain.Category;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AdminCategoryController {

    private StorageService storageService;
    private CategoryRepository categoryRepository;

    @Autowired
    public AdminCategoryController(StorageService storageService, CategoryRepository categoryRepository) {
        this.storageService = storageService;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.DELETE, value = "/categories/{id}")
    public CommonResponse deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);

        return CommonResponse.ok();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/categories")
    public void updateCategory(@ModelAttribute Category form, HttpServletResponse response) throws IOException {
        if (form.getParentCategory() != null && form.getParentCategory().getId() != null) {
            form.setParentCategory(categoryRepository.getOne(form.getParentCategory().getId()));
        }
        if (form.getImageFileAtMenu() != null && !form.getImageFileAtMenu().isEmpty()) {
            form.setImageAtMenuUrl(storageService.storeFile(form.getImageFileAtMenu()));
        }
        if (form.getImageFileActiveAtMenu() != null && !form.getImageFileActiveAtMenu().isEmpty()) {
            form.setImageActiveAtMenuUrl(storageService.storeFile(form.getImageFileActiveAtMenu()));
        }
        categoryRepository.save(form);

        response.sendRedirect("/env/categoryList");
    }
}
