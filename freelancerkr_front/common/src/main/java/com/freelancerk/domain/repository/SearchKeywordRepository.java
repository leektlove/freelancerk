package com.freelancerk.domain.repository;

import com.freelancerk.domain.SearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {
    SearchKeyword findTop1ByKeyword(String keyword);

    List<SearchKeyword> findTop10ByOrderByCountDesc();
}
