package com.freelancerk.service;


import com.freelancerk.domain.Category;
import com.freelancerk.model.SelectedKeywordModel;

import java.util.List;

public interface CategoryService {

    List<Category> createCategoryByParentIdNameJsons(List<SelectedKeywordModel> keywordModels);
}
