package com.freelancerk.domain.repository;

import com.freelancerk.domain.LalaFreelancer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LalaFreelancerRepository extends JpaRepository<LalaFreelancer, Long> {
    List<LalaFreelancer> findTop6ByOrderByIdDesc();
}
