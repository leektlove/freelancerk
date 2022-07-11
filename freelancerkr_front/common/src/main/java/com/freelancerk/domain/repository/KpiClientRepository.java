package com.freelancerk.domain.repository;

import com.freelancerk.domain.KpiClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KpiClientRepository extends JpaRepository<KpiClient, Long>, JpaSpecificationExecutor<KpiClient> {
    KpiClient findByUserId(Long id);
}
