package com.freelancerk.domain.repository;

import com.freelancerk.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>, JpaSpecificationExecutor<Purchase> {
    Purchase findByImpUid(String impUid);

    Purchase findByAdministrationId(String administrationId);

    List<Purchase> findByProjectIdAndStatus(Long projectId, Purchase.Status status);

    List<Purchase> findByProjectPostingClientIdAndStatus(Long userId, Purchase.Status status);

    Purchase findTop1ByUserIdOrderByCreatedAtDesc(Long userId);

    List<Purchase> findByProjectIdAndStatusInOrderByCreatedAtDesc(Long projectId, List<Purchase.Status> statuses);

    List<Purchase> findByTypeAndPickMeUpIdAndStatus(Purchase.Type type, Long pickMeUpId, Purchase.Status status);

    List<Purchase> findByProjectBidIdAndStatus(Long projectBidId, Purchase.Status status);

    int countByProjectId(Long projectId);

    int countByProjectIdAndStatusAndType(Long projectId, Purchase.Status status, Purchase.Type type);

    List<Purchase> findByProjectIdAndStatusAndType(Long projectId, Purchase.Status status, Purchase.Type type);

    Purchase findByProjectIdAndType(long projectId, Purchase.Type type);

    Purchase findByProjectIdAndStatusAndTypeAndUserId(long projectId, Purchase.Status status, Purchase.Type type, Long userId);

    int countByPickMeUpIdAndStatusAndCreatedAt(Long id, Purchase.Status completed, LocalDateTime createdAt);

    Purchase findByPickMeUpIdAndStatusAndCreatedAt(Long id, Purchase.Status completed, LocalDateTime createdAt);

    List<Purchase> findByStatus(Purchase.Status status);

    List<Purchase> findByTypeAndSupplyAmountOfMoney(Purchase.Type type, int criteria);

    List<Purchase> findByProjectId(Long projectId);

    List<Purchase> findByProjectPostingClientId(Long postingClientId);

}
