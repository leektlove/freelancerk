package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.freelancerk.TimeUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ToString(exclude = {"postingClient", "contractedFreelancer", "projectCategories", "contestSectors", "projectItemTickets", "pickedContestEntries", "projectBids", "priorityProjectBids", "myProjectBid", "pickedProjectBid", "pickedContestEntries", "ticketLogs", "escrowLogs"})
@EqualsAndHashCode(exclude = {"postingClient", "contractedFreelancer", "projectCategories", "contestSectors", "projectItemTickets", "pickedContestEntries", "projectBids", "priorityProjectBids", "myProjectBid", "pickedProjectBid", "pickedContestEntries", "ticketLogs", "escrowLogs"})
@Data
@Entity
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String uuid;

	// 일반 정보 필수!
	@Enumerated(EnumType.STRING)
	private Type projectType; 	// 프로젝트, 컨테스트 구분

	private boolean useEscrow;	// 에스크로

	@OneToMany
	@ManyToOne
	private User postingClient; // 포스팅한 클라이언트
	private String title; 		// 제목
	@Column(columnDefinition = "TEXT")
	private String description; // 설명
	private long hits; 			// 조회수

	private Integer usedPoint; // 포스팅시 사용한 포인트
	private String process;
	private Boolean receiveEmailAtBid;
	private Boolean receiveSmsAtBid;
	private int contractPrice;

	private boolean startFromDirectMarket;

	private String projectDescriptionFileName;	// 관련 문서 파일 이름
	private String projectDescriptionFileUrl;	// 관련 문서 파일 URL

	@Column(columnDefinition = "TEXT")
	private String memo;
	
	private Integer budgetFrom;
	private Integer budgetTo;
	private Integer defaultMoney;
	private Float incentiveFrom;
	private Float incentiveTo;
	@Enumerated(EnumType.STRING)
	private PayCriteria payCriteria;	// 대금지급기준
	@Enumerated(EnumType.STRING)
	private PayMean payMean;
	@Enumerated(EnumType.STRING)
	private WorkPlace workPlace;
	private String workPlaceAddress1;
	private String workPlaceAddress2;

	// 일정 정보
	@Enumerated(EnumType.STRING)
	private ExpectedPeriod expectedPeriod;	// 예상 프로젝트 기간
	private String expectedPeriodEtc;

	@CreationTimestamp
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	private LocalDateTime postingStartAt;
	private LocalDateTime postingEndAt;
	private LocalDateTime completedAt;
	private LocalDateTime cancelAt;

	private boolean forcedCompleted;

	private LocalDateTime contractAt;		// 계약일
	private LocalDateTime contractCompletedAt; // 계약만료일

	// 입찰 정보
	private Integer successBidPrice; // 낙찰금액 todo 중복제거?
	private LocalDateTime successBidAt; // 낙찰일자
	private String cancelReason; // 취소사유

	// 계약 후 정보
	@ManyToOne
	private User contractedFreelancer; // 계약된 프리랜서
	private LocalDateTime startAt; // 프로젝트 시작
	private LocalDateTime endAt; // 프로젝트 종료

	private LocalDateTime lastPurchasedAt;

	private LocalDateTime prizeLastModifiedAt;
	// 생명주기
	@Enumerated(EnumType.STRING)
	private Status status;

	// 컨테스트 기본정보
	private String categoryOfBusiness;
	private String majorProduct;
	private Integer quantityPage; // 분량
	private String descriptionPerPage;
	@Column(columnDefinition = "TEXT")
	private String referenceWebUrl;	// 참고소재 또는 도메인 주소
	private String workingWebUrl; // 운영중인 웹사이트
	@Column(columnDefinition = "TEXT")
	private String corporateNameOrCatchPhrase;	// 상호명 또는 슬로건
	private String priorityTone;
	private String priorityFeelings; // 느낌
	private String contestOptions;
	private String contestOptionsCount;
	private String contestPackOptions;
	private String contestPackOptionsCount;
	private Integer minPrize;	// 최소상금
	private Integer incentive;	// 인센티브
	private Integer prizeFor1st;
	private Integer prizeFor2nd;
	private Integer prizeFor3rd;
	private Integer depositMoney;
	private Integer fee;
	private Integer totalPrice;
	private Boolean payForFeeToFreelancer;
	private boolean proceedProjectAfterContest;
	private boolean purchaseRecordDeleted;
	private int numberOfPersons;

	@ManyToOne
	private BankType bankForReceivingPayment;
	private String bankAccountForReceivingPayment;
	private String bankAccountName;

	private int keywordDuplicatedMailTargets;

	private boolean sendKeywordDuplicatedMail;
	private boolean sendNeedPickMail;
	private boolean sendEndInTwoDaysMail;
	private boolean invalid;

	@JsonManagedReference
	@OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
	private Set<ProjectCategory> projectCategories;

    @JsonManagedReference
	@OneToMany(mappedBy = "contest", fetch = FetchType.EAGER)
	private Set<ContestSector> contestSectors;

    @JsonIgnore
	@JsonManagedReference
	@Where(clause = "created_at <= now() and expired_at >= now() and invalid = 0")
	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
	private Set<ProjectItemTicket> projectItemTickets;

	@Formula("(select coalesce(count(b.id), 0) from project_bid as b where b.project_id = id and b.invalid = 0)")
	private Integer numberOfApplyCount;

	@Formula("(select coalesce(max(b.amount_of_money), 0) from project_bid as b where b.project_id = id and b.invalid = 0)")
	private Integer maxBidAmount;

	@Formula("(select coalesce(min(b.amount_of_money), 0) from project_bid as b where b.project_id = id and b.invalid = 0)")
	private Integer minBidAmount;

    @Formula("(select coalesce(count(b.id), 0) from project_favorite as b where b.project_id = id)")
    private Long favoriteCount;

	@Formula("(select coalesce(count(*), 0) from project_item_ticket as pt left join project_product_item_type as ppt on pt.project_product_item_type_id  = ppt.id where ppt.category = 'INTERNAL' AND ppt.priority = 1 and pt.project_id = id and pt.created_at <= now() and pt.expired_at >= now() )")
	private boolean internalPriority;

	@Formula("(select coalesce(count(*), 0) from project_item_ticket as pt left join project_product_item_type as ppt on pt.project_product_item_type_id  = ppt.id where ppt.category = 'EXTERNAL' AND ppt.priority = 1 and pt.project_id = id and pt.created_at <= now() and pt.expired_at >= now() )")
	private boolean externalPriority;

	@Formula("(SELECT coalesce((CASE WHEN project_type = 'PROJECT' THEN budget_to WHEN project_type = 'CONTEST' THEN min_prize ELSE 0 END), 0) FROM project as p where p.id = id)")
	private int amountSortingCriteria;

	@Transient
	private long messageCountByClient;

	@Transient
	private long messageCountByFreelancer;

	@Transient
	private int projectPropositionCount;

	@Transient
	private Feeling[] feelings;

	@Transient
    private List<ProjectBid> pickedContestEntries;

	@Transient
	private List<ProjectBid> projectBids;
	@Transient
	private List<ProjectBid> priorityProjectBids;

	@Transient
	private List<ContestEntryFile> allPrimaryContestEntryFiles;

	@Transient
	private ProjectBid pickedProjectBid;

	@Transient
	private ProjectBid myProjectBid;

	@Transient
	private long totalIncomeAmount;

	@Transient
	private long totalEscrowAmount;
	@Transient
	private long totalEscrowSupplyAmount;
	@Transient
	private long totalEscrowVatAmount;
	@Transient
	private List<EscrowLog> escrowLogs;

	@Transient
	private List<Long> projectOptionItemIds;
	@Transient
	private Map<Long, String> projectOptionTicketValidationSpansMap;
	@Transient
	private Map<Long, String> projectOptionTicketEndAtMap = new LinkedHashMap<>();
	@Transient
	private List<Long> sectorMetaIds;
	@Transient
	private List<Long> sectorIds;

	@Transient
	private Boolean premium;	// 필수! Premium, Sponsored 구분
	@Transient
	private boolean urgency;	// 긴급
	@Transient
	private boolean useNdaIp;

	@Transient
	private List<String> entryFileThumbnailUrl = new ArrayList<>(); // 지원작품
	@Transient
	private List<PaymentToUser> paymentToUsers;
	@Transient
	private List<PaymentToUser> payedPaymentToUsers;
	@Transient
	private PaymentToUser lastPaymentToUser;
	@Transient
	private ProjectComplete projectComplete;
	@Transient
	private int myPrizeMoney;
	@Transient
	private int remainEscrowAmount;

	@Transient
	private long totalChangedOptionMoney;
	@Transient
	private long totalAmountOfMoney;
	@Transient
	private long currentEscrowAmount;
	@Transient
	private boolean rePurchase;
	@Transient
	private boolean denyable;
	@Transient
	private boolean liked;
	@Transient
	private int numberOfEntryFiles;

	@Transient
	private String avgCareerYear;

	@Transient
	private int totalUsePoint;
	@Transient
	private int totalSupplyAmount;
	@Transient
	private int totalVatAmount;
	@Transient
	private int totalDiscountAmount;
	@Transient
	private int totalPurchaseAmount;
	@Transient
	private LocalDateTime firstPurchasedAt;
	@Transient
	private boolean pickMeUpRegistered;
	@Transient
	private long commentCountVisibleToMe;

	@Transient
	private List<ProjectItemTicketLog> ticketLogs;

	public enum Type {
		PROJECT("프로젝트"), CONTEST("컨테스트"), CONTEST_TO_PROJECT("프로젝트(컨테스트에서 시작)");

		@Getter
		private String name;

		Type(String name) {
			this.name = name;
		}
	}

	public enum Status {
		VOLATILE("최초 진입"), TEMP("임시저장"), POSTED("입찰 중"), CANCELLED("취소 됨"), IN_PROGRESS("진행 중"), COMPLETED("완료 됨");

		@Getter
		private String desc;

		Status(String desc) {
			this.desc = desc;
		}
	}

	public enum ExpectedPeriod {
		NO_IDEA("잘 모르겠음"), UNDER_AWEEK("1주일 이내"), UNDER_TWO_WEEK("2주일 이내"), UNDER_THREE_WEEK("3주일 이내"), UNDER_AMONTH("1개월 이내"), UNDER_2MONTH("1개월 ~ 2개월"),
		UNDER_3MONTH("2개월 ~ 3개월"), UNDER_4MONTH("3개월 ~ 4개월"), UNDER_5MONTH("4개월 ~ 5개월"),
		UNDER_6MONTH("5개월 ~ 6개월"), OVER_6MONTH("6개월 이상"), DIRECT("직접입력");

		final private String name;

		private ExpectedPeriod(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		public static ExpectedPeriod getEnum(String value) {
		    for(ExpectedPeriod v : values())
		        if(v.getName().equalsIgnoreCase(value)) return v;
		    throw new IllegalArgumentException();
		}		
	}

	@Transient
	public LocalDateTime getGiveRewardEndDateTime() {
		if (successBidAt == null) return null;
		return successBidAt.plusDays(7);
	}

	public String getExpectedPeriodTxt() {
		if (ExpectedPeriod.DIRECT.equals(expectedPeriod)) {
			return String.format("%s개월 이상", expectedPeriodEtc);
		}
		if (this.expectedPeriod == null || this.expectedPeriod.getName().isEmpty()) {
			return "";
		} else {
			return this.expectedPeriod.getName();
		}
	}

	public String getBudget() {
		if (budgetFrom == null && budgetTo == null && !PayMean.PER_INCENTIVE.equals(payMean)) return "가격을 제안해주세요";

		if (budgetFrom != null && budgetFrom == 0 && budgetTo != null && budgetTo == 0) return "가격을 제안해주세요";

		if (budgetFrom != null && budgetTo == null) {
			return String.format("%,d만원~", budgetFrom);
		}

		if (budgetFrom == null && budgetTo != null) {
			return String.format("~%,d만원", budgetTo);
		}

		if (payMean == null) {
			return String.format("%,d만원~%,d만원", budgetFrom, budgetTo);
		} else if (PayMean.PER_WORD.equals(payMean)) {
			return String.format("%,d원~%,d원", budgetFrom, budgetTo);
		} else if (!PayMean.PER_INCENTIVE.equals(payMean)) {
			return String.format("%,d만원~%,d만원", budgetFrom, budgetTo);
		} else {
			return String.format("%,d만원 (%.0f%%~%.0f%%)", defaultMoney, incentiveFrom==null?0:incentiveFrom, incentiveTo==null?0:incentiveTo);
		}
	}

	public enum PayMean {
		PER_CASE("건별지급", "건별"), PER_HOUR("시급제", "시급"), PER_WEEK("주급제", "주급"),
		PER_MONTH("월급제", "월급"), PER_WORD("단어당지급(번역)", "단어당"),
		PER_UNIT("프로젝트단위 계약제", "프로젝트"),
		PER_INCENTIVE("성과급제", "성과급");

		final private String name;
		final private String shortName;

		private PayMean(String name, String shortName) {
			this.name = name;
			this.shortName = shortName;
		}

		public String getName() {
			return name;
		}

		public String getShortName() {return shortName; }

		public static PayMean getEnum(String value) {
			for(PayMean v : values())
				if(v.getName().equalsIgnoreCase(value)) return v;
			throw new IllegalArgumentException();
		}
	}

	public enum PayCriteria {
		PERCENTAGE("완성도에 따라 지급"), WORK_OVER("100% 완성 후 지급"), SALARY("매월 급여형으로 지급"), CONTRACT_AND_REMAIN("계약금+잔금지급"), AGREEMENT("협의");

		final private String name;

		private PayCriteria(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		public static PayCriteria getEnum(String value) {
		    for(PayCriteria v : values())
		        if(v.getName().equalsIgnoreCase(value)) return v;
		    throw new IllegalArgumentException();
		}
	}

	public String getPayCriteria() {
		if (this.payCriteria == null || this.payCriteria.getName().isEmpty()) {
			return "";
		} else {
			return this.payCriteria.getName();
		}
	}

	public enum WorkPlace {
		NO_IDEA("무관"), ON_LINE_AFTER_MEET("1~2회 미팅 후 On-Line 근무 가능"), ON_LINE("100% On-Line 근무 가능"), OFF_LINE("Off-Line 근무 필수");

		final private String name;

		private WorkPlace(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		public static WorkPlace getEnum(String value) {
		    for(WorkPlace v : values())
		        if(v.getName().equalsIgnoreCase(value)) return v;
		    throw new IllegalArgumentException();
		}
	}

	public String getWorkPlace() {
		if (this.workPlace == null || this.workPlace.getName().isEmpty()) {
			return "";
		} else if (WorkPlace.OFF_LINE.equals(workPlace)) {
			if (StringUtils.isNotEmpty(workPlaceAddress2)) {
				return String.format("오프라인 근무(%s %s)", workPlaceAddress1, workPlaceAddress2);
			} else if (StringUtils.isNotEmpty(workPlaceAddress1)) {
				return String.format("오프라인 근무(%s)", workPlaceAddress1);
			}
			return "오프라인 근무";
		}
		return this.workPlace.getName();
	}

	public WorkPlace getRawWorkPlace() {
		return workPlace;
	}
	
	public enum Feeling {
		MODERN("모던함"), CLEAN("깔끔함"), POLISHING("세련됨"), CLASSIC("클래식함"), STABLE("안정감"), LIGHT("밝음"), CHEERFUL("유쾌"),
		CHALLENGING("도전"), DEFIANCE("반항"), YOUNG("젊음"), FOREIGN("외국느낌"), LUXURY("럭셔리"), SMALL_AND_CUTE("아기자기"),
		EASY("쉬움"), CRAZY("코미디");

		@Getter
		private String desc;

		Feeling(String desc) {
			this.desc = desc;
		}
	}

	@Transient
	public boolean isProject() {
		return Type.PROJECT.equals(projectType);
	}

	@Transient
	public String getPriorityFeelingDescriptions() {
		List<String> result = new ArrayList<>();
		if (StringUtils.isEmpty(priorityFeelings)) return null;
		for (String fooAsString : Splitter.on(',').split(priorityFeelings)) {
			result.add(Feeling.valueOf(fooAsString).desc);
		}
		return Joiner.on(",").join(result);
	}

	@Transient
	@JsonIgnore
	public boolean isContainFeeling(String feeling) {
		List<Feeling> result = new ArrayList<>();
		if (StringUtils.isEmpty(priorityFeelings)) return false;
		for (String fooAsString : Splitter.on(',').split(priorityFeelings)) {
			result.add(Feeling.valueOf(fooAsString));
		}
		for (Feeling item: result) {
			if (feeling.equalsIgnoreCase(item.name())) {
				return true;
			}
		}
		return false;
	}

	@Transient
	@JsonIgnore
	public EnumSet<Feeling> getFeelings() {
		EnumSet<Feeling> result = EnumSet.noneOf(Feeling.class);
		if (StringUtils.isEmpty(priorityFeelings)) return result;
		for (String fooAsString : Splitter.on(',').split(priorityFeelings)) {
			result.add(Feeling.valueOf(fooAsString));
		}
		return result;
	}

	@Transient
	public void setFeelings(EnumSet<Feeling> foos) {
		if (foos == null) return;
		this.priorityFeelings = Joiner.on(",").join(foos);
	}

	@Transient
	public String getWorkPlaceDescription() {
		if (!WorkPlace.OFF_LINE.equals(workPlace)) return null;
		if (StringUtils.isEmpty(workPlaceAddress1) && StringUtils.isEmpty(workPlaceAddress2)) {
			return null;
		} else if (StringUtils.isNotEmpty(workPlaceAddress1) && StringUtils.isEmpty(workPlaceAddress2)) {
			return String.format("(%s)", workPlaceAddress1);
		} else if (StringUtils.isNotEmpty(workPlaceAddress2) && StringUtils.isEmpty(workPlaceAddress1)) {
			return String.format("(%s)", workPlaceAddress2);
		} else {
			return String.format("(%s, %s)", workPlaceAddress1, workPlaceAddress2);
		}
	}

	@JsonIgnore
	public int getTotalPrize() {
		if (prizeFor1st == null) {
			prizeFor1st = 0;
		}

		if (prizeFor2nd == null) {
			prizeFor2nd = 0;
		}

		if (prizeFor3rd == null) {
			prizeFor3rd = 0;
		}
		return prizeFor1st + prizeFor2nd + prizeFor3rd;
	}

	@JsonIgnore
	public int getExpectedCancelDividend() {
		int totalPrize = getTotalPrize();
		int expectedDividend = 0;

		int expectedNumberOfApplyCount = numberOfApplyCount;

		if (expectedNumberOfApplyCount == 0) {
			expectedNumberOfApplyCount = 1;
		}

		if (expectedNumberOfApplyCount <= 2) {
			expectedDividend = (20000 + (int) (totalPrize*0.2)) / expectedNumberOfApplyCount;
		} else if (expectedNumberOfApplyCount< 10) {
			expectedDividend = (50000 + (int) (totalPrize*0.4)) / expectedNumberOfApplyCount;
		} else {
			expectedDividend = (80000 + (int) (totalPrize*0.7)) / expectedNumberOfApplyCount;
		}

		return 1000*(expectedDividend/1000);
	}

	@JsonIgnore
	public int getRefundablePriceWhVat() {
		return (int) (1.1*getRefundablePrice());
	}

	@JsonIgnore
	public int getRefundablePrize() {
		int refundablePrize = 0;
		if (numberOfApplyCount == 0) {
			refundablePrize = getTotalPrize();
		} else if (numberOfApplyCount <= 2) {
			refundablePrize = (int) (0.7*getTotalPrize());
		} else if (numberOfApplyCount < 6) {
			refundablePrize = (int) (0.5*getTotalPrize());
		} else {
			refundablePrize = (int) (0.2*getTotalPrize());
		}
		return  refundablePrize;
	}

	@JsonIgnore
	public int getRefundablePrice() {
		int depositMoney = 100000;

		int refundableDepositMoney = 0;
		int refundablePrize = 0;
		if (numberOfApplyCount == 0) {
			refundableDepositMoney = depositMoney;
			refundablePrize = getTotalPrize();
		} else if (numberOfApplyCount <= 2) {
			refundableDepositMoney = (int) (depositMoney*0.8);
			refundablePrize = (int) (0.7*getTotalPrize());
		} else if (numberOfApplyCount < 6) {
			refundableDepositMoney = (int) (depositMoney*0.5);
			refundablePrize = (int) (0.5*getTotalPrize());
		} else {
			refundableDepositMoney = (int) (depositMoney*0.2);
			refundablePrize = (int) (0.2*getTotalPrize());
		}
		return refundableDepositMoney + refundablePrize;
	}

	@JsonIgnore
	@Transient
	public boolean isPostingEnd() {
		if (postingEndAt == null) return false;
		return LocalDateTime.now().isAfter(postingEndAt);
	}

	@JsonIgnore
	@Transient
	public boolean isPickEnd() {
		if (postingEndAt == null) return false;
		return LocalDateTime.now().isAfter(postingEndAt.plusDays(7));
	}

	@JsonIgnore
	@Transient
	public LocalDateTime getPickEndAt() {
		if (postingEndAt == null) return null;
		return postingEndAt.plusDays(7);
	}

	@JsonIgnore
	@Transient
	public LocalDateTime getPostingEndOrPickEndAt() {
		if (isPostingEnd() && numberOfApplyCount > 0 && Status.POSTED.equals(status)) return getPickEndAt();
		return postingEndAt;
	}

	@JsonIgnore
	@Transient
	public LocalDateTime getPostingEndOrCancelAt() {
		if (postingEndAt != null && cancelAt != null) {
			if (postingEndAt.isBefore(cancelAt)) {
				return postingEndAt;
			}

			return cancelAt;
		}

		if (cancelAt != null) return cancelAt;
		return postingEndAt;
	}
		
	@JsonIgnore
	@Transient
	public int getRemainDays() {
		if (postingEndAt == null) return 7; // todo
		int days = (int)LocalDateTime.from(LocalDateTime.now()).until(this.postingEndAt, ChronoUnit.DAYS) + 1;
		if (days < 0) {
			days = 0;
		}
		return days;
	}

	@JsonIgnore
	@Transient
	public String getPostingStartDate() {
		return TimeUtil.convertLocalDateTimeToStrWithTime(postingStartAt);
	}

	@JsonIgnore
	@Transient
	public String getPostingEndDate() {
		return TimeUtil.convertLocalDateTimeToStrWithTime(postingEndAt);
	}

	@JsonIgnore
	@Transient
	public int getPrizeTargetPersons() {
		if (prizeFor3rd == null || prizeFor3rd == 0) {
			if (prizeFor2nd == null || prizeFor2nd == 0) {
				return 1;
			}
			return 2;
		}

		return 3;
	}

	@JsonIgnore
	@Transient
	public String getPostingPastDayTimes() {
		if (postingStartAt == null) return "정보 없음";
		int postHours = 0;
		if (Status.CANCELLED.equals(status)) {
			postHours = (int)LocalDateTime.from(this.postingStartAt).until(cancelAt, ChronoUnit.HOURS);
		} else if (!Status.POSTED.equals(status) && successBidAt != null){
			postHours = (int)LocalDateTime.from(this.postingStartAt).until(successBidAt, ChronoUnit.HOURS);
		} else {
			postHours = (int) LocalDateTime.from(this.postingStartAt).until(LocalDateTime.now(), ChronoUnit.HOURS);
		}

		return String.format("%d 일 %d 시간", postHours / 24, postHours % 24);
	}

	@JsonIgnore
	@Transient
	public int getRemainRate() {
		if (postingEndAt == null) return 100;
		LocalDateTime startDateTime = postingStartAt==null?this.createdAt:postingStartAt;
		LocalDateTime endDate = this.postingEndAt;
		LocalDateTime period = startDateTime;
		long wholeDays = period.until(endDate, ChronoUnit.DAYS);
		return (int) ((double) getRemainDays() / wholeDays * 100);
	}

	@JsonIgnore
	@Transient
	public String getContestPaymentStatus() {
		if (Status.POSTED.equals(status)) return null;
		if (Status.IN_PROGRESS.equals(status)) {
			return "1차선청완료";
		} else if (Status.COMPLETED.equals(status)) {
			return forcedCompleted?"강제선정":"최종선정완료";
		} else if (Status.CANCELLED.equals(status)) {
			return "취소환불";
		}

		return null;
	}

	public Integer getIncentive() {
		if (minPrize == null) return 0;
		return getTotalPrize() - minPrize;
	}

	public boolean isContainProjectOption(Long optionId) {
		return projectOptionItemIds != null && projectOptionItemIds.contains(optionId);
	}

	@Transient
	public int getOptionPurchasePrice(Long optionId) {
		if (ticketLogs == null) return 0;

		int optionTotalPrice = 0;
		for (ProjectItemTicketLog ticketLog: ticketLogs) {
			if (ticketLog.getProjectProductItemType().getId().equals(optionId)) {
				optionTotalPrice += ticketLog.getOptionPrice();
			}
		}

		return optionTotalPrice;
	}

	public String getProjectOptionValidationSpan(Long optionId) {
		return projectOptionTicketValidationSpansMap.get(optionId);
	}

	public boolean isContainContestMetaOption(Long metaId) {
		return sectorMetaIds != null && sectorMetaIds.contains(metaId);
	}

	public boolean isContainContestOption(Long optionId) {
		return sectorIds != null && sectorIds.contains(optionId);
	}

	public String getPreviousEndAt(Long optionId) {
		return projectOptionTicketEndAtMap.get(optionId);
	}

	@JsonIgnore
	public String getContestSectorMetaTypeImageUrl() {
		if (contestSectors != null && !contestSectors.isEmpty()) {
			return contestSectors.iterator().next().getContestSectorType().getContestSectorMetaType().getImageUrl();
		}

		return null;
	}

	@Transient
	public String getMainContestMetaType() {
		if (contestSectors == null || contestSectors.size() == 0) return null;

		return contestSectors.iterator().next().getContestSectorType().getContestSectorMetaType().getName();
	}

	@Transient
	public String getMainContestName() {
		if (contestSectors == null || contestSectors.size() == 0) return null;

		return contestSectors.iterator().next().getContestSectorType().getName();
	}

	@Transient
	@JsonIgnore
	public Integer getMinBidAmount() {
		if (projectBids == null || projectBids.size() == 0) return 0;
		projectBids.sort((o1, o2) -> o1.getAmountOfMoney() - o2.getAmountOfMoney());

		return projectBids.get(0).getAmountOfMoney();
	}

	@Transient
	@JsonIgnore
	public Integer getMaxBidAmount() {
		if (projectBids == null || projectBids.size() == 0) return 0;
		projectBids.sort((o1, o2) -> o2.getAmountOfMoney() - o1.getAmountOfMoney());

		return projectBids.get(0).getAmountOfMoney();
	}

	@Transient
	@JsonIgnore
	public boolean isExistsRequestPaymentToUser() {
		return lastPaymentToUser != null && PaymentToUser.Status.REQUESTED.equals(lastPaymentToUser.getStatus());
	}

	@Transient
	@JsonIgnore
	public String getPickedFreelancerNames() {
		if (Type.CONTEST.equals(projectType)) {
			if (pickedContestEntries == null) return null;
			return String.join(", ", pickedContestEntries.stream().map(ProjectBid::getParticipant).map(User::getName).collect(Collectors.toList()));
		} else if (Arrays.asList(Type.PROJECT, Type.CONTEST_TO_PROJECT).contains(projectType)) {
			if (pickedProjectBid == null) return null;
			return pickedProjectBid.getParticipant().getName();
		}

		return null;
	}

	@Transient
	@JsonIgnore
	public List<String> getReferenceWebUrlList() {
		if (StringUtils.isEmpty(referenceWebUrl)) {
			return new ArrayList<>();
		}
		return Arrays.asList(referenceWebUrl.split(","));
	}

	@Transient
	@JsonIgnore
	public LocalDateTime getPostingEndAtPlus7() {
		if (postingEndAt == null) return null;
		return postingEndAt.plusDays(7);
	}

	@Transient
	@JsonIgnore
	public LocalDateTime getGiveRewardEndAt() {
		if (startAt == null) return null;
		return startAt.plusDays(7);
	}


	@Transient
	public String getOnlyTextDescription() {
		if (StringUtils.isEmpty(description)) return description;
		try {
			Document document = Jsoup.parse(description);
			return document.body().text();
		} catch (Exception e) {
			log.error("<<< error at parsing html content. description: {}", description, e);
		}
		return description;
	}


	@Transient
	public Long getFirstTopCategoryId() {
		if (projectCategories == null || projectCategories.size() == 0) return null;
		Category category = projectCategories.iterator().next().getCategory();
		Category parentCategory = category.getParentCategory();
		while (parentCategory != null) {
			category = parentCategory;
			parentCategory = parentCategory.getParentCategory();
		}
		return category.getId();
	}
}
