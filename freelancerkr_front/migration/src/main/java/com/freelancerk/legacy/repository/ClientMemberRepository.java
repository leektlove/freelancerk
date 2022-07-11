package com.freelancerk.legacy.repository;

import com.freelancerk.legacy.domain.ClientMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientMemberRepository extends JpaRepository<ClientMember, Long> {
}
