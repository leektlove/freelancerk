package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.freelancerk.TimeUtil;
import com.freelancerk.model.ContactAvailableDayTimeModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Where;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@ToString(exclude = {"tickets", "payProductList", "ticketLogs", "allTickets"})
@EqualsAndHashCode(exclude = {"tickets", "payProductList", "ticketLogs", "allTickets"})
@Data
@Entity
public class PickMeUp {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	private String title;
	@Column(columnDefinition = "LONGTEXT")
	private String description;

	@Column(columnDefinition = "LONGTEXT")
	private String mainImageUrl;
	private String compressedImageUrl;
	@Column(columnDefinition = "LONGTEXT")
	private String croppedMainImageUrl;

	private long hits;
	private LocalDate workStartAt;
	private LocalDate workEndAt;
	private Boolean exposeAdmin;
	private boolean freeCharge;
	private boolean invalid;
	private boolean purchaseRecordDeleted;
	private int minimumPay;
	@Enumerated(EnumType.STRING)
	private PayType payType;
	@Enumerated(EnumType.STRING)
	private Project.WorkPlace workPlace;
	private String workPlaceAddress1;
	private String workPlaceAddress2;

	@Enumerated(EnumType.STRING)
	private ContentType contentType;

	private boolean sendExpirationExposeNoti;
	@CreationTimestamp
	private LocalDateTime createdAt;

	@ManyToOne
	private User user;
	@ManyToOne
	private Category category1st;
	@ManyToOne
	private Category category2nd;
	@ManyToOne
	private Project project;

	@JsonBackReference
	@OneToMany(mappedBy = "pickMeUp", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PickMeUpDetailFile> detailFiles;

	@JsonBackReference
	@Where(clause = "start_at <= now() and end_at >= now() and invalid = 0")
	@OneToMany(mappedBy = "pickMeUp", cascade = CascadeType.ALL)
	private Set<PickMeUpTicket> tickets;

	@JsonBackReference
	@OneToMany(mappedBy = "pickMeUp", cascade = CascadeType.ALL)
	private List<FreelancerPayProduct> payProductList;

	@JsonBackReference
	@OneToMany(mappedBy = "pickMeUp", cascade = CascadeType.REMOVE)
	private List<DirectDeal> directDeals;

	@Formula("(select coalesce(count(*), 0) from direct_deal as dd where dd.pick_me_up_id = id)")
	private Long directDealCount;
	@Formula("(select coalesce(count(*), 0) from project_proposition as pp where pp.pick_me_up_id = id)")
	private Long projectPropositionCount;

	private boolean temp;
	@Transient
	private List<Long> optionItemIds;
	@Transient
	private boolean advancedResource;
	@Transient
	private boolean unique;
	@Transient
	private Long directDealClickCount;
	@Transient
	private String memo;
	@Transient
	private List<PickMeUpTicket> allTickets;
	@Transient
	private List<PickMeUpTicketLog> ticketLogs;

	@Formula("(select coalesce(count(*), 0) from pick_me_up_ticket as pt left join freelancer_product_item_type as fpt on pt.freelancer_product_item_type_id  = fpt.id where pt.pick_me_up_id = id and fpt.priority = true and pt.start_at <= now() and pt.end_at >= now() )")
	private boolean priority;

	@Formula("(select coalesce(count(*), 0) from pick_me_up_ticket as pt left join freelancer_product_item_type as fpt on pt.freelancer_product_item_type_id  = fpt.id where pt.pick_me_up_id = id and fpt.code = 'DIRECT_DEAL' and pt.start_at <= now() and pt.end_at >= now() )")
	private boolean directDeal;

	@Formula("(select coalesce(count(*), 0) from pick_me_up_ticket as pt where pt.pick_me_up_id = id  )")
	private boolean existsTicketRecord;

	@Formula("(select coalesce(count(*), 0) from pick_me_up_like as pl where pl.pick_me_up_id = id  )")
	private int likes;

	@Formula("(select coalesce(count(*), 0) from pick_me_up_comment as pc where pc.pick_me_up_id = id  )")
	private int comments;

	@Transient
	private boolean expose;
	@Transient
	private boolean featured;
	@Transient
	private boolean creative;
	@Transient
	private boolean highQuality;
	@Transient
	private boolean directDealAvailable;

	@Transient
	private int totalSupplyAmount;
	@Transient
	private int totalVatAmount;
	@Transient
	private int totalDiscountAmount;
	@Transient
	private int totalUsedPoints;
	@Transient
	private int totalPurchaseAmount;
	@Transient
	private int totalPostingMinutes;
	@Transient
	private Map<Long, String> pickMeUpOptionTicketEndAtMap = new LinkedHashMap<>();

	@Transient
	private String editordata;

	@Transient
	private boolean liked;

	@Transient
    private List<ContactAvailableDayTimeModel> dayTimes;

	@Transient
	public String getOnlyTextDescription() {
		if (ContentType.BLOG.equals(contentType) && StringUtils.isNotEmpty(description)) {
			Document document = Jsoup.parse(description);
			return document.body().text();
		}
		return description;
	}

	@Transient
	public String getRepresentativeImageUrl() {
		if (StringUtils.isNotEmpty(compressedImageUrl)) return compressedImageUrl;
		if (StringUtils.isNotEmpty(croppedMainImageUrl)) return croppedMainImageUrl;
		return mainImageUrl;
	}

	@Transient
	public String getWorkPlaceDescription() {
		if (this.workPlace == null || this.workPlace.getName().isEmpty()) {
			return null;
		} else if (Project.WorkPlace.OFF_LINE.equals(workPlace)) {
			if (StringUtils.isNotEmpty(workPlaceAddress2)) {
				return String.format("오프라인 근무(%s %s)", workPlaceAddress1, workPlaceAddress2);
			} else if (StringUtils.isNotEmpty(workPlaceAddress1)) {
				return String.format("오프라인 근무(%s)", workPlaceAddress1);
			}
			return "오프라인 근무";
		}
		return this.workPlace.getName();
	}

	public boolean isFrkCertified() {
		return project != null;
	}

	public void setWorkStartAt(final String workStartAt) {
		this.workStartAt = TimeUtil.convertStrToLocalDate(workStartAt);
	}
	
	public String getWorkStartAt() {
		if(this.workStartAt != null) {
			return this.workStartAt.toString();
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        Calendar calendar = Calendar.getInstance();
	        return sdf.format(calendar.getTime());
		}
	}
	
	public void setWorkEndAt(final String workEndAt) {
		this.workEndAt = TimeUtil.convertStrToLocalDate(workEndAt);
	}

	public String getWorkEndAt() {
		if(this.workEndAt != null) {
			return this.workEndAt.toString();
		}
		else {
			return "2018-12-31";
		}
	}

	@Transient
	public String getPreviousEndAt(Long optionId) {
		return pickMeUpOptionTicketEndAtMap.get(optionId);
	}

	@Transient
	public int getOptionPurchasePrice(Long optionId) {
		if (ticketLogs == null) return 0;

		int optionTotalPrice = 0;
		for (PickMeUpTicketLog ticketLog: ticketLogs) {
			if (ticketLog.getFreelancerProductItemType().getId().equals(optionId)) {
				optionTotalPrice += ticketLog.getOptionPrice();
			}
		}

		return optionTotalPrice;
	}

	@Transient
	public String getAllOptionValidationSpan(Long optionId) {
		if (allTickets == null) return null;

		for (PickMeUpTicket ticket: allTickets) {
			if (ticket.getFreelancerProductItemType().getId().equals(optionId) && freeCharge &&
					ticket.getFreelancerProductItemType().getCode().name().equalsIgnoreCase(FreelancerProductItemType.Code.PICK_ME_UP.name())) {
				return "무제한";
			}
			if (ticket.getFreelancerProductItemType().getId().equals(optionId)) {
				return String.format("%s", TimeUtil.convertLocalDateTimeToStrWithTime(ticket.getEndAt()));
			}
		}

		return null;
	}

	@Transient
	public String getOptionValidationSpan(Long optionId) {
		if (tickets == null) return null;

		for (PickMeUpTicket ticket: tickets) {
			if (ticket.getFreelancerProductItemType().getId().equals(optionId) && freeCharge &&
					ticket.getFreelancerProductItemType().getCode().name().equalsIgnoreCase(FreelancerProductItemType.Code.PICK_ME_UP.name())) {
				return "무제한";
			}
			if (ticket.getFreelancerProductItemType().getId().equals(optionId)) {
				return String.format("%s", TimeUtil.convertLocalDateTimeToStrWithTime(ticket.getEndAt()));
			}
		}

		return null;
	}

	@Transient
	public String getOptionValidationSpan(String optionCode) {
		if (tickets == null) return null;

		for (PickMeUpTicket ticket: tickets) {
			if (ticket.getFreelancerProductItemType().getCode().name().equals(optionCode) && freeCharge &&
					ticket.getFreelancerProductItemType().getCode().name().equalsIgnoreCase(FreelancerProductItemType.Code.PICK_ME_UP.name())) {
				return "무제한";
			}
			if (ticket.getFreelancerProductItemType().getCode().name().equals(optionCode)) {
				return String.format("%s", TimeUtil.convertLocalDateTimeToStrWithTime(ticket.getEndAt()));
			}
		}

		return null;
	}

	@Transient
	public boolean isContainOption(Long optionId) {
		if (tickets == null) return false;

		for (PickMeUpTicket ticket: tickets) {
			if (ticket.getFreelancerProductItemType().getId().equals(optionId)) {
				return true;
			}
		}
		return false;
	}

	@JsonIgnore
	@Transient
	public String getPostingPastDayTimes() {
		if (totalPostingMinutes == 0) return "정보 없음";
		int totalPostingHours = totalPostingMinutes / 60;
		return String.format("%d 일 %d 시간", totalPostingHours / 24, totalPostingHours % 24);
	}

	@Transient
	public boolean isPickMeUpExpose() {
		return tickets != null && !tickets.isEmpty();
	}

	public enum PayType {
		PER_PAGE("장당"), PER_USE("건당(1회)"), PER_HOUR("시급"), PER_WEEK("주급"), PER_MONTH("월급"), PER_PROJECT("프로젝트"), AGREEMENT("클라이언트와 협의 후 결정");

		@Getter
		private String desc;

		PayType(String desc) {
			this.desc = desc;
		}
	}

	public String getPayDescription() {
		if (payType == null || PayType.AGREEMENT.equals(payType)) return PayType.AGREEMENT.desc;

		return String.format("%s %,d 만원 이상", payType.desc, minimumPay);
	}

	public enum ContentType {
		BLOG, IMAGE, VIDEO, AUDIO
	}
}
