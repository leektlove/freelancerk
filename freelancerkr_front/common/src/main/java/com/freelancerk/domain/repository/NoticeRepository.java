package com.freelancerk.domain.repository;

import com.freelancerk.domain.Notice;
import com.freelancerk.type.NoticeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findByTitleContainingOrContentContaining(String titleSearchWord, String contentSearchWord, Pageable pageable);
    int countByTitleContainingOrContentContaining(String titleSearchWord, String contentSearchWord);

    Notice findTop1ByOrderByCreatedAtDesc();

    List<Notice> findByTypeOrderByIdDesc(NoticeType type);

    List<Notice> findByHiddenFalseOrderByIdDesc();
}
