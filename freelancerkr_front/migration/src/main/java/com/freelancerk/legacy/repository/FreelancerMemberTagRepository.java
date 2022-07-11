package com.freelancerk.legacy.repository;

import com.freelancerk.legacy.domain.FreelancerMemberTag;
import com.freelancerk.legacy.domain.MemberTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreelancerMemberTagRepository extends JpaRepository<FreelancerMemberTag, MemberTagId> {
    List<FreelancerMemberTag> findByFmId(String fmId);
}
