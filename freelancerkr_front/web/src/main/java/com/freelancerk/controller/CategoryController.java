package com.freelancerk.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freelancerk.domain.Category;
import com.freelancerk.domain.Keyword;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.KeywordRepository;
import com.freelancerk.vo.CategoryCellVO;

@RestController
public class CategoryController {

    private CategoryRepository categoryRepository;
    private KeywordRepository keywordRepository;
  
    @Autowired
    public CategoryController(CategoryRepository categoryRepository, KeywordRepository keywordRepository) {
        this.categoryRepository = categoryRepository;
        this.keywordRepository = keywordRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/categories")
    public List<Category> getCategories(@RequestParam(value = "parentCategoryId", required = false) Long parentCategoryId) {
        if (parentCategoryId == null) {
            return categoryRepository.findByParentCategoryIsNullOrderBySeqAsc();
        }

        return categoryRepository.findByParentCategoryIdOrderBySeqAsc(parentCategoryId);
    }
    
    // 카테고리 팝업을 위한 용도
    @RequestMapping(method = RequestMethod.GET, value = "/allCategories")
    public List<CategoryCellVO> getAllCategories() {
        List<Category> parentCategories = categoryRepository.findByParentCategoryIsNullOrderBySeqAsc();

        List<CategoryCellVO> resultLsit = new ArrayList<>();
        for(Category c : parentCategories) {
        	CategoryCellVO vo = new CategoryCellVO();
        	vo.setParentCategoryId(c.getId());
        	vo.setParentCategoryName(c.getName());
        	List<Category> childrenCategories = categoryRepository.findByParentCategoryIdOrderBySeqAsc(c.getId());
        	vo.setChildCategories(childrenCategories);
   
        	resultLsit.add(vo);     	
        }
        
        return resultLsit;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/categories/{categoryId}/popular-keywords")
    public List<Keyword> getPopularKeywords(@PathVariable("categoryId") Long categoryId) {
        List<Keyword> returnList = new ArrayList<>();
        List<String> returnSet = new ArrayList<>();
        for (Keyword keyword: keywordRepository.findTop30ByCategoryParentCategoryIdOrderByUsageCountDesc(categoryId)) {
            if (returnSet.contains(keyword.getName())) {
                continue;
            }
            returnSet.add(keyword.getName());
            returnList.add(keyword);
        }

        return returnList;
    }
}
