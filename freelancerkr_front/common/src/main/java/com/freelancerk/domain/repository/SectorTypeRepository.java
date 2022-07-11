package com.freelancerk.domain.repository;

import com.freelancerk.domain.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorTypeRepository extends JpaRepository<Sector, Long> {
    Sector findByName(String sector);
}
