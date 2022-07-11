package com.freelancerk.domain;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class MenuHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private MenuCode menuCode;
    @ManyToOne
    private User user;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ParentMenuCode {
        CLIENT_PAYMENT_ADMIN, CLIENT_DIRECT_MARKET, CLIENT_BID, CLIENT_PROJECT,
        FREELANCER_PAYMENT, FREELANCER_PORTFOLIO, FREELANCER_PROJECT, FREELANCER_BID, FREELANCER_MY_PROJECT,
        COMMON
    }

    public enum MenuCode {
        CLIENT_PAYMENT("/client/payment/list", ParentMenuCode.CLIENT_PAYMENT_ADMIN),
        CLIENT_ESCROW("/client/payment/escrowList", ParentMenuCode.CLIENT_PAYMENT_ADMIN),
        CLIENT_DIRECT_MARKET("/client/directMarket/index", ParentMenuCode.CLIENT_DIRECT_MARKET),
        CLIENT_PROPOSITION("/client/bid/suggestList", ParentMenuCode.CLIENT_BID),
        CLIENT_POSTING("/client/bid/processingList", ParentMenuCode.CLIENT_BID),
        CLIENT_CANCELLED("/client/bid/cancelList", ParentMenuCode.CLIENT_BID),
        CLIENT_TEMP_SAVED("/client/bid/autoSave", ParentMenuCode.CLIENT_BID),
        CLIENT_IN_PROGRESS("/client/project/processingList", ParentMenuCode.CLIENT_PROJECT),
        CLIENT_COMPLETED("/client/project/doneList", ParentMenuCode.CLIENT_PROJECT),
        FREELANCER_PAYMENT("/freelancer/payment/index", ParentMenuCode.FREELANCER_PAYMENT),
        FREELANCER_PORTFOLIO("/freelancer/pickMeUp/list", ParentMenuCode.FREELANCER_PORTFOLIO),
        FREELANCER_PROPOSED("/freelancer/bid/suggestedList", ParentMenuCode.FREELANCER_PROJECT),
        FREELANCER_FAVORITED("/freelancer/project/pickList", ParentMenuCode.FREELANCER_PROJECT),
        FREELANCER_POSTING("/freelancer/bid/onGoingList", ParentMenuCode.FREELANCER_BID),
        FREELANCER_SUCCESS("/freelancer/bid/successList", ParentMenuCode.FREELANCER_BID),
        FREELANCER_FAILED("/freelancer/bid/failList", ParentMenuCode.FREELANCER_BID),
        FREELANCER_IN_PROGRESS("/freelancer/project/onGoingList", ParentMenuCode.FREELANCER_MY_PROJECT),
        FREELANCER_COMPLETED("/freelancer/project/completedList", ParentMenuCode.FREELANCER_MY_PROJECT),
        NOTICE("/common/notice", ParentMenuCode.COMMON), REFERENCE("/common/reference", ParentMenuCode.COMMON);

        @Getter
        String uri;
        @Getter
        ParentMenuCode parentMenuCode;

        MenuCode(String uri, ParentMenuCode parentMenuCode) {
            this.uri = uri;
            this.parentMenuCode = parentMenuCode;
        }
    }
}
