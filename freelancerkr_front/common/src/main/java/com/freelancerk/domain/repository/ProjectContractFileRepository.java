package com.freelancerk.domain.repository;

import com.freelancerk.domain.ProjectContractFile;

import java.util.List;

import com.freelancerk.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectContractFileRepository extends JpaRepository<ProjectContractFile, Long> {
	List<ProjectContractFile> findByProjectId(Long projectId);
	List<ProjectContractFile> findByProjectIdAndUserRoleAndInvalidFalse(Long projectId, User.Role role);

}
