package com.freelancerk.domain.repository;

import com.freelancerk.domain.PickMeUpFreeCharge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickMeUpFreeChargeRepository extends JpaRepository<PickMeUpFreeCharge, Long> {
    int countByUserId(Long userId);
}
