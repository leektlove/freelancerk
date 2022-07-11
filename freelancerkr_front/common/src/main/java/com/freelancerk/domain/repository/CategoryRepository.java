package com.freelancerk.domain.repository;

import com.freelancerk.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Category findByNameAndParentCategoryIdIsNull(String name);
    Category findByNameAndParentCategoryIsNotNull(String name);

    Category findByOriginalCode(String substring);

    List<Category> findByParentCategoryId(Long parentCategoryId);

    List<Category> findByParentCategoryIsNullOrderBySeqAsc();

    List<Category> findByParentCategoryIdOrderBySeqAsc(Long id);

    Category findByParentCategoryIdAndName(Long parentCategoryId, String name);

    Category findByParentCategoryOriginalCodeAndName(String fcId2, String categoryStr);

    Category findTopByName(String name);
}
