package com.freelancerk.domain.repository;

import com.freelancerk.domain.FreelancerProductItemType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreelancerProductItemTypeRepository extends JpaRepository<FreelancerProductItemType, Long> {
    FreelancerProductItemType findByName(String name);
    List<FreelancerProductItemType> findByUsageTypeContainingAndValidTrue(String usageType, Sort seq);

    List<FreelancerProductItemType> findByUsedInContestEntryTrueAndValidTrue();

    List<FreelancerProductItemType> findByTypeNot(FreelancerProductItemType.Type type);

    FreelancerProductItemType findByCode(FreelancerProductItemType.Code code);

    List<FreelancerProductItemType> findByTypeNotAndValidTrue(FreelancerProductItemType.Type type);
}
