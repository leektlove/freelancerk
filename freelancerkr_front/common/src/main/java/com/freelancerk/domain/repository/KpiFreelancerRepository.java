package com.freelancerk.domain.repository;

import com.freelancerk.domain.KpiFreelancer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KpiFreelancerRepository extends JpaRepository<KpiFreelancer, Long>, JpaSpecificationExecutor<KpiFreelancer> {
    KpiFreelancer findByUserId(Long userId);
}
