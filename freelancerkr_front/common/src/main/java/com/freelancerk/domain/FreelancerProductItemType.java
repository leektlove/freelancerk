package com.freelancerk.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Data
@Entity
public class FreelancerProductItemType {
	// 상품 기본 정보
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    
	@Enumerated(EnumType.STRING)
    private Type type;
	@Enumerated(EnumType.STRING)
    private Code code;
    /**
     * STANDARD : BASIC 상품들을 ON/OFF 할 수 있는 상품들. 18.10 기준 '픽미업 공개' 하나임.
     * BASIC	: 기본 상품들
     * PACKAGE	: 패키지 상품들. 기본 상품들의 조합으로 구성됩니다.
     */
    public enum Type {
        STANDARD, BASIC, PACKAGE
    }
    
    
    private String usageType;
    
    /**
     * PICK_ME_UP : 픽미업 결제에 사용할 상품정보
     * CONTEST_ENTRY : 컨테스트 참여 결제에 사용할 상품정보
     */   
    public enum UsageType {
        PICK_ME_UP, CONTEST_ENTRY
    }
    private int seq;
    private String name;			// 상품 이름
    private String subName;			// 상품의 다른 이름이 필요한 경우
    private String shortDescription;// 짧은 설명
    private String description;		// 설명
    private Integer unitPrice;		// 단위 가격
    private Integer	unitDuration;	// 단위 기간

    private boolean usedInContestEntry;

    // 상품 패키지 정보
    private String combinationOfBasicProduct;	// 기본 상품 조합 정보. ('basic_id, basic_id, basic_id ...') 형태로 저장.
        
    // 할인 정보
    private Integer discountPrice;			// 특정 금액으로 할인을 할 경우 사용
    private Double discountPercent;			// 기준 금액의 비율로 할일은 할 경우 사용
    private LocalDate discountStartDate;	// 할인 시작일
    private LocalDate discountEndDate;		// 할인 종료일

    private boolean priority;
    private boolean valid;

    @Transient
    private boolean usedInPickMeUp;

    public enum Code {
        PICK_ME_UP, FEATURED, CREATIVE, PRIORITY, HIGH_QUALITY, DIRECT_DEAL
    }
}
