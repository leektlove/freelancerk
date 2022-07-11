package com.freelancerk.legacy.repository;

import com.freelancerk.legacy.domain.FreelancerMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreelancerMemberRepository extends JpaRepository<FreelancerMember, Long> {
    List<FreelancerMember> findByFmNoGreaterThan(int i);

    FreelancerMember findByFmId(String id);
}
