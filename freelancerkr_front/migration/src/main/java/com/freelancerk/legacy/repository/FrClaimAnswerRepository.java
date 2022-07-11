package com.freelancerk.legacy.repository;

import com.freelancerk.legacy.domain.FrClaim;
import com.freelancerk.legacy.domain.FrClaimAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrClaimAnswerRepository extends JpaRepository<FrClaimAnswer, Integer> {
}
