package com.freelancerk.domain;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
public class ProjectContractFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private Project project;
	@ManyToOne
	private User user;
	
	private String fileName;
	private String fileUrl;
	@CreationTimestamp
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	private boolean invalid;
		
	@Enumerated(EnumType.STRING)
	private User.Role userRole;
}
