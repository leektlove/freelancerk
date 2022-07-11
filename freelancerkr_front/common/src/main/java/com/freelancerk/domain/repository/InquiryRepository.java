package com.freelancerk.domain.repository;

import com.freelancerk.domain.Inquiry;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
	Page<Inquiry> findByUserIdAndTitleContainingOrContentContaining(Long userId,
			String titleSearchWord, String contentSearchWord, Pageable pageable);
	
	int countByUserIdAndTitleContainingOrContentContaining(Long userId, String titleSearchWord,
			String contentSearchWord);
	
	Page<Inquiry> findByUserIdAndTitleContainingOrContentContainingAndCreatedAtAfterAndCreatedAtBefore(Long userId,
			String titleSearchWord, String contentSearchWord, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	int countByUserIdAndTitleContainingOrContentContainingAndCreatedAtAfterAndCreatedAtBefore(Long userId, String titleSearchWord,
			String contentSearchWord, LocalDateTime startDate, LocalDateTime endDate);

	Page<Inquiry> findByUserId(Long userId, Pageable pageable);

	Page<Inquiry> findByUserIdAndContentContaining(Long userId, String keyword, Pageable pageable);

	Page<Inquiry> findByContentContaining(String keyword, Pageable pageable);
}
