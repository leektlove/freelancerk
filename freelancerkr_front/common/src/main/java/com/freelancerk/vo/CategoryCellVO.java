package com.freelancerk.vo;

import java.util.ArrayList;
import java.util.List;

import com.freelancerk.domain.Category;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryCellVO {
	private Long parentCategoryId;
	private String parentCategoryName;
	private List<Category> childCategories;
}
