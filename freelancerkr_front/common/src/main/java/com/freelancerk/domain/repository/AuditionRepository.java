package com.freelancerk.domain.repository;

import com.freelancerk.domain.Audition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AuditionRepository extends JpaRepository<Audition, Long>, JpaSpecificationExecutor<Audition>  {
	List<Audition> findByStatus(String status);
	Audition findByAuditionId(String auditionId);
}
