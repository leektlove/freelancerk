package com.freelancerk.domain.repository;

import com.freelancerk.domain.PickMeUpLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickMeUpLikeRepository extends JpaRepository<PickMeUpLike, Long> {
    int countByPickMeUpIdAndUserId(Long pickMeUpId, Long userId);

    PickMeUpLike findByPickMeUpIdAndUserId(Long pickMeUpId, Long sessionUserId);
}
