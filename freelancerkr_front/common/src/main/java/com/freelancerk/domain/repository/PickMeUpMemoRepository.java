package com.freelancerk.domain.repository;

import com.freelancerk.domain.PickMeUpMemo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickMeUpMemoRepository extends JpaRepository<PickMeUpMemo, Long> {
	PickMeUpMemo findByUserIdAndPickMeUpId(Long sessionUserId, Long pickMeUpId);
}
