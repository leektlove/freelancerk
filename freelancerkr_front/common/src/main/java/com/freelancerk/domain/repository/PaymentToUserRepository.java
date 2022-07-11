package com.freelancerk.domain.repository;

import com.freelancerk.domain.PaymentToUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public interface PaymentToUserRepository extends JpaRepository<PaymentToUser, Long>, JpaSpecificationExecutor<PaymentToUser> {
    PaymentToUser findTop1ByContestEntryRewardProjectBidIdOrderByCreatedAtDesc(Long projectBidId);

    List<PaymentToUser> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<PaymentToUser> findByUserId(Long userId);

    List<PaymentToUser> findByUserIdAndStatus(Long userId, PaymentToUser.Status status);

    PaymentToUser findTop1ByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<PaymentToUser> findByProjectPostingClientIdAndStatus(Long userId, PaymentToUser.Status status);

    List<PaymentToUser> findByStatus(PaymentToUser.Status status);

    List<PaymentToUser> findByProjectIdAndStatus(Long projectId, PaymentToUser.Status status);

    List<PaymentToUser> findByProjectIdOrderByPayedAtDesc(Long projectId);

    List<PaymentToUser> findByBankTypeIsNull();
}
