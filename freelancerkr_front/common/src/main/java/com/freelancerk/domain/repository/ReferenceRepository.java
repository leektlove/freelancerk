package com.freelancerk.domain.repository;

import com.freelancerk.domain.EscrowLog;
import com.freelancerk.domain.Notice;
import com.freelancerk.domain.Reference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferenceRepository extends JpaRepository<Reference, Long> {
	Page<Reference> findByTitleContainingOrContentContaining(String titleSearchWord, String contentSearchWord, Pageable pageable);
    int countByTitleContainingOrContentContaining(String titleSearchWord, String contentSearchWord);

    Reference findTop1ByOrderByCreatedAtDesc();
}
