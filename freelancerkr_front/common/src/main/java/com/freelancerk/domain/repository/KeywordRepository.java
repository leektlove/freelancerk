package com.freelancerk.domain.repository;

import com.freelancerk.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByCategoryIdOrderByUsageCountDesc(Long categoryId);

    Keyword findTop1ByCategoryId(Long categoryId);

    List<Keyword> findByCategoryParentCategoryIdOrderByUsageCountDesc(Long parentCategoryId);

    List<Keyword> findTop30ByCategoryParentCategoryIdOrderByUsageCountDesc(Long parentCategoryId);
}
