package com.freelancerk.legacy.repository;

import com.freelancerk.legacy.domain.FrPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrPickMeUpRepository extends JpaRepository<FrPortfolio, Integer> {
}
