package com.freelancerk.domain.repository;

import com.freelancerk.domain.MenuHit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuHitRepository extends JpaRepository<MenuHit, Long> {
    List<MenuHit> findByUserIdOrderByCreatedAt(Long sessionUserId);

    MenuHit findTop1ByUserIdAndMenuCodeOrderByCreatedAtDesc(Long userId, MenuHit.MenuCode menuCode);
}
