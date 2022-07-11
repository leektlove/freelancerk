package com.freelancerk.domain.repository;

import com.freelancerk.domain.PickMeUpComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PickMeUpCommentRepository extends JpaRepository<PickMeUpComment, Long> {
    List<PickMeUpComment> findByPickMeUpIdOrderByCreatedAtDesc(Long pickMeUpId);
}
