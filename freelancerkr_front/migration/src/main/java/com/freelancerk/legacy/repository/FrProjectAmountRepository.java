package com.freelancerk.legacy.repository;

import com.freelancerk.legacy.domain.FrProject;
import com.freelancerk.legacy.domain.FrProjectAmount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrProjectAmountRepository extends JpaRepository<FrProjectAmount, Integer> {
}
