package com.freelancerk.domain.repository;

import com.freelancerk.domain.DanalCertificationResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DanalCertificationResultRepository extends JpaRepository<DanalCertificationResult, Long> {

    DanalCertificationResult findByUniqueKeyAndSignedTrue(String uniqueKey);
}
