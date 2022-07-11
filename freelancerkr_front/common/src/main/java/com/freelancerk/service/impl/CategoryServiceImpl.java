package com.freelancerk.service.impl;

import com.freelancerk.domain.Category;
import com.freelancerk.domain.Keyword;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.KeywordRepository;
import com.freelancerk.model.SelectedKeywordModel;
import com.freelancerk.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

	private KeywordRepository keywordRepository;
	private CategoryRepository categoryRepository;

	@Autowired
	public CategoryServiceImpl(KeywordRepository keywordRepository, CategoryRepository categoryRepository) {
		this.keywordRepository = keywordRepository;
		this.categoryRepository = categoryRepository;
	}

	@Override
	public List<Category> createCategoryByParentIdNameJsons(List<SelectedKeywordModel> keywordModels) {
		if (keywordModels == null) return new ArrayList<>();
		List<Category> categories = new ArrayList<>();
		for (SelectedKeywordModel model: keywordModels) {
			Category category = categoryRepository.findByParentCategoryIdAndName(model.getId(), model.getKeyword());
			if (category == null) {
				category = new Category();
				category.setParentCategory(categoryRepository.getOne(model.getId()));
				category.setHidden(false);
				category.setCreatedAt(LocalDateTime.now());
				category.setName(model.getKeyword());
				category = categoryRepository.save(category);
			}
			categories.add(category);

			Keyword keyword = keywordRepository.findTop1ByCategoryId(category.getId());
			if (keyword == null) {
				keyword = new Keyword();
				keyword.setUsageCount(0L);
				keyword.setCategory(category);
				keyword.setName(category.getName());
			}
			keyword.setUsageCount(keyword.getUsageCount() + 1);
			keywordRepository.save(keyword);

		}
		return categories;
	}
}
