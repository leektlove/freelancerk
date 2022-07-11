package com.freelancerk.domain.repository;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AligoKakaoMessageTemplateRepository extends JpaRepository<AligoKakaoMessageTemplate, Long> {
    AligoKakaoMessageTemplate findByCode(AligoKakaoMessageTemplate.Code code);
}
