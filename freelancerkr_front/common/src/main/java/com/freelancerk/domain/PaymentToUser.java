package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freelancerk.util.FeeCalculator;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Entity
public class PaymentToUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Type type;
    private long amount;
    private long depositAmount;
    private long accumulatedIncomeAmount;
    private String description;
    @Column(columnDefinition = "TEXT")
    private String acceptDescription;
    @Column(columnDefinition = "TEXT")
    private String paymentDescription;
    @Column(columnDefinition = "TEXT")
    private String memoFromAdmin;
    @ManyToOne
    private User user;
    @Enumerated(EnumType.STRING)
    private TaxType taxType;
    private String freelancerName;
    @ManyToOne
    private BankType bankType;
    private String bankAccountName;
    private String bankAccountForReceivingPayment;
    private String denyReason;
    @JsonIgnore
    @ManyToOne
    private Project project;
    @JsonIgnore
    @ManyToOne
    private ContestEntryReward contestEntryReward;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime deniedAt;
    private LocalDateTime payedAt;

    public int getFee() {
        if (project == null) return 0;
        if (Project.Type.CONTEST.equals(project.getProjectType())) {
            if (project.getPayForFeeToFreelancer() != null && project.getPayForFeeToFreelancer()) {
                return 0;
            } else {
                return (int) (amount * 0.1);
            }
        } else {
            if (!project.isUseEscrow()) {
                return 0;
            }
            return (int) (amount * FeeCalculator.getFeeRate(project.getPostingStartAt()));
        }
    }

    public int getBusinessIncomeTax() {
        if (TaxType.COLLECTION_IN_ADVANCE.equals(taxType)) {
            int result = (int) ((amount - getFee()) * 0.03f);
            return (result/10)*10;
        }
        return 0;
    }

    public int getLocalIncomeTax() {
        if (TaxType.COLLECTION_IN_ADVANCE.equals(taxType)) {
            int result = (int) (getBusinessIncomeTax()*0.1);
            return (result/10)*10;
        }
        return 0;
    }

    public int getDepositVat() {
        if (TaxType.VAT.equals(taxType)) {
            return (int) (depositAmount*0.1);
        }

        return 0;
    }

    @Transient
    public int getDepositSupplyAmount() {
        return (int) (depositAmount - getDepositVat());
    }

    public int getVat() {
        if (TaxType.VAT.equals(taxType)) {
            return (int) ((amount - getFee())*0.1);
        }

        return 0;
    }

    @Transient
    public int getSupplyAmount() {
        return (int) (amount - getVat());
    }

    public int getTotalDeductedAmount() {
        if (TaxType.COLLECTION_IN_ADVANCE.equals(taxType)) {
            return getFee() + getBusinessIncomeTax() + getLocalIncomeTax();
        } else if (TaxType.VAT.equals(taxType)) {
            return getFee();
        }

        return 0;
    }

    public long getRealAmount() {
        if (TaxType.COLLECTION_IN_ADVANCE.equals(taxType)) {
            return amount - getTotalDeductedAmount();
        } else if (TaxType.VAT.equals(taxType)) {
            return amount - getTotalDeductedAmount() + getVat();
        }

        return 0;
    }

    public boolean isRerequestable() {
        return Arrays.asList(Status.DENIED, Status.PAYED).contains(status);
    }

    public enum Type {
        PROJECT, CONTEST_REWARD
    }

    public enum Status {
        REQUESTED("프리랜서 입금요청"), ACCEPTED("클라이언트 결제승인"), DENIED("승인 거절"), PAYED("지급 완료");

        @Getter
        private String desc;

        Status(String desc) {
            this.desc = desc;
        }
    }
}
