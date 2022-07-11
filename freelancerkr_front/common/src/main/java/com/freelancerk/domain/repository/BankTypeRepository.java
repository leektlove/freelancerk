package com.freelancerk.domain.repository;

import com.freelancerk.domain.BankType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTypeRepository extends JpaRepository<BankType, Long> {
    BankType findByName(String name);
}
