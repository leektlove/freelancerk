package com.freelancerk.domain.repository;

import com.freelancerk.domain.FreelancerPayProduct;
import com.freelancerk.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FreelancerPayProductRepository extends JpaRepository<FreelancerPayProduct, Long>, JpaSpecificationExecutor<FreelancerPayProduct> {
	List<FreelancerPayProduct> findByPickMeUpId(Long pickMeUpId);
	List<FreelancerPayProduct> findByContestEntryId(Long contestEntryId);
	
    List<FreelancerPayProduct> findByPurchaseId(Long purchaseId);

    @Transactional
    void deleteByPickMeUpIdAndPurchaseIdIsNull(Long pickMeUpId);

    List<FreelancerPayProduct> findByPickMeUpIdAndPurchaseIdIsNull(Long pickMeUpId);

    List<FreelancerPayProduct> findByContestEntryIdAndPurchaseIdIsNull(Long pickMeUpId);

    List<FreelancerPayProduct> findByContestEntryProjectIdAndPurchaseStatusOrderByIdAsc(Long contestEntryId, Purchase.Status purchaseStatus);
}
