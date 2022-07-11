package com.freelancerk.domain.repository;

import com.freelancerk.domain.ReferenceFile;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferenceFileRepository extends JpaRepository<ReferenceFile, Long> {
	List<ReferenceFile> findByReferenceId(Long referenceId);
}
