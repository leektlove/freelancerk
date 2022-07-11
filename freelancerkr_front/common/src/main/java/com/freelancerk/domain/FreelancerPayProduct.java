package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.extern.java.Log;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 결제한 상품 하나 하나를 저장합니다
 */
@Log
@Data
@Entity
public class FreelancerPayProduct {	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private Type type;
	
	@ManyToOne
	private FreelancerProductItemType freelancerProductItemType;

	@JsonManagedReference
	@ManyToOne
	private PickMeUp pickMeUp;
	
	@ManyToOne
	private ProjectBid contestEntry;
	
	@ManyToOne
	private Purchase purchase;
		
	private Integer count;
	private Integer amount;
	private boolean free;
	private boolean usedInPickMeUp;

	private boolean includedInPackage;

	@Transient
	private long freelancerProductItemTypeId;

	@Transient
	public int getPoints() {
		return (int) (count < 3?amount*0.1:amount*0.2);
	}

	public enum Type {
		FOR_PICK_ME_UP, FOR_CONTEST_ENTRY
	}
}
