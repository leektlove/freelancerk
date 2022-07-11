package com.freelancerk.domain.repository;

import com.freelancerk.domain.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {
    int countByInquiryId(Long inquiryId);

    List<InquiryAnswer> findByInquiryId(Long inquiryId);
}
