package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@EqualsAndHashCode(exclude = {"freelancerPayProducts", "primaryContestEntryFile", "allContestEntryFiles", "ticketLogs"})
@ToString(exclude = {"freelancerPayProducts", "primaryContestEntryFile", "allContestEntryFiles", "ticketLogs"})
@Data
@Entity
public class ProjectBid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User participant;
    
    @ManyToOne
    private Project project;
        
    private TaxType taxType;
    @ManyToOne
    private BankType bankForReceivingPayment; // todo 필요?
    private String bankAccountForReceivingPayment; // todo 필요?

    private LocalDateTime applyAt;	// 프로젝트 입찰일, 컨테스트 지원일
    private LocalDateTime successBidAt; // 낙찰에 성공했을 때 낙찰일 지정(프리랜서쪽 낙찰일로 정렬할 때)
    private LocalDateTime failedAt;

    private int amountOfMoney;
    private int pickedAmountOfMoney;
    @Column(columnDefinition = "TEXT")
    private String commentByClient;    
    private int numberOfModifications;
    private int usedPoint;
    private int totalPayment;

    private int pointsForRefund;

    // Contest Entry

    private Integer pickedRank;
    private boolean isReceivedPrize;   
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    private BidType bidType; // todo 삭제 필요. project의 type 을 보면 됨
     
    @Enumerated(EnumType.STRING)
    private BidStatus bidStatus;

    @Formula("(SELECT coalesce(count(r.id), 0) FROM project_bid as pb left join contest_entry_reward as r on pb.id = r.project_bid_id where pb.id = id)")
    private boolean rewardGivenOrRequested;
    private boolean purchaseRecordDeleted;
    private boolean invalid;
    private LocalDateTime invalidAt;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Where(clause = "representative = 1")
    @OneToMany(mappedBy = "contestEntry", fetch = FetchType.EAGER)
    private Set<ContestEntryFile> primaryContestEntryFile;

    @OrderBy(" id asc ")
    @OneToMany(mappedBy = "contestEntry", fetch = FetchType.EAGER, cascade = ALL, orphanRemoval = true)
    private Set<ContestEntryFile> allContestEntryFiles;

    @OrderBy(" id asc ")
    @OneToMany(mappedBy = "projectBid", fetch = FetchType.EAGER)
    private List<ProjectBidComment> comments;

    @OneToMany(mappedBy = "contestEntry", fetch = FetchType.EAGER)
    private Set<FreelancerPayProduct> freelancerPayProducts;

    @Transient
    private List<Long> optionItemIds;

    @Transient
    private int numberOfActiveTickets;
    @Transient
    private String avgCareerYear;
    @Transient
    private int averageBidMoney;
    @Transient
    private int numberOfBids;
    @Transient
    private int myPrizeMoney;
    @Transient
    private int myRank;
    @Transient
    private long participantCompletedProjectCount;
    @Transient
    private long participantAccumulatedProjectMoney;
    @Transient
    private List<Category> matchedCategories;
    @Transient
    private ContestEntryReward contestEntryReward;
    @Transient
    private PaymentToUser lastPayment;

    @JsonIgnore
    @Transient
    private boolean featured;
    @JsonIgnore
    @Transient
    private boolean creative;
    @JsonIgnore
    @Transient
    private boolean highQuality;
    @JsonIgnore
    @Transient
    private boolean directDealAvailable;

    @Transient
    private int totalSupplyAmount;
    @Transient
    private int totalVatAmount;
    @Transient
    private int totalDiscountAmount;
    @Transient
    private int totalPurchaseAmount;
    @Transient
    private List<ContestEntryTicketLog> ticketLogs;

    public enum BidType {
    	PROJECT_BID, CONTEST_BID
    }
                
    public enum BidStatus {
        APPLY, CANCEL, FAILED, PICKED
    }

    @Transient
    public int getNumberOfActiveTickets() {
        if (optionItemIds == null) return 0;
        return optionItemIds.size();
    }

    @Transient
    public boolean isContainOption(Long optionId) {
        return optionItemIds != null && optionItemIds.contains(optionId);
    }

    @JsonIgnore
    @Transient
    public ContestEntryFile getPrimaryContestEntryFile() {
        if (primaryContestEntryFile != null && primaryContestEntryFile.size() > 0) {
            return primaryContestEntryFile.iterator().next();
        }
        return null;
    }

    @Transient
    public String getPrimaryContestEntryFileUrl() {
        if (primaryContestEntryFile != null && primaryContestEntryFile.size() > 0) {
            return primaryContestEntryFile.iterator().next().getFileUrl();
        }
        return null;
    }

    @Transient
    public List<ContestEntryFile> getDetailFiles() {
        List<ContestEntryFile> detailFiles = new ArrayList<>();

        for (ContestEntryFile file: allContestEntryFiles) {
            if (file.isRepresentative()) continue;
            detailFiles.add(file);
        }
        return detailFiles;
    }

    @Transient
    public int getOptionPurchasePrice(Long optionId) {
        if (ticketLogs == null) return 0;

        int optionTotalPrice = 0;
        for (ContestEntryTicketLog ticketLog: ticketLogs) {
            if (ticketLog.getFreelancerProductItemType().getId().equals(optionId)) {
                optionTotalPrice += ticketLog.getPrice();
            }
        }

        return optionTotalPrice;
    }

}
