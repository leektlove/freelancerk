package com.freelancerk.domain.repository;

import com.freelancerk.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {
    Page<Message> findByUserId(Long userId, Pageable pageable);

    List<Message> findByContentContainingAndUserId(String keyword, Long userId, Pageable pageable);

    Page<Message> findByContentContainingAndUserIdAndCreatedAtGreaterThanAndCreatedAtLessThan(String keyword, Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
