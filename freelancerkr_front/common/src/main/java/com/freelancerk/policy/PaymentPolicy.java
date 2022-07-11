package com.freelancerk.policy;

import lombok.Getter;

public class PaymentPolicy {

    public static final int PICK_ME_UP_FREE_CHARGE_COUNT = 2;

    public enum PickMeUp {
        DEFAULT(10000, 1), DEFAULT_FEATURED(20000, 1), DEFAULT_CREATIVE(20000, 1), DEFAULT_PRIORITY(40000, 1),
        DEFAULT_HIGHT_QUALITY(40000, 1), DEFAULT_DEAL(40000, 1);

        @Getter
        int unitPrice;
        @Getter
        int unitPeriodMonth;
        PickMeUp(int unitPrice, int unitPeriodMonth) {
            this.unitPrice = unitPrice;
            this.unitPeriodMonth = unitPeriodMonth;
        }
    }

    public enum PickMeUpPack {
        JUNIOR, SENIOR, LUXURY, DEAL
    }

    public enum Project {
        ESCROW(0, 0, null),
        EXPOSE_INTERNAL(0, 0, null), EXPOSE_INTERNAL_URGENT(20000, 1, null), EXPOSE_INTERNAL_FEATURED(20000, 1, null), EXPOSE_INTERNAL_PRIORITY(40000, 1, null),
        EXPOSE_EXTERNAL(50000, 1, null), EXPOSE_EXTERNAL_URGENT(30000, 1, null), EXPOSE_EXTERNAL_FEATURED(30000, 1, null), EXPOSE_EXTERNAL_PRIORITY(50000, 1, null),
        NDA_IPA(20000, 1, null), HIGH_QUALITY_SMS(40000, null, 1);

        @Getter
        int unitPrice;
        @Getter
        Integer unitPeriodWeek;
        @Getter
        Integer count;
        Project(int unitPrice, Integer unitPeriodWeek, Integer count) {
            this.unitPrice = unitPrice;
            this.unitPeriodWeek = unitPeriodWeek;
            this.count = count;
        }
    }

    public enum ProjectPack {
        PRIORITY, EASY, LUXURY, SUPREME
    }

    public enum Contest {
        EXTEND(50000, 1, null),
        EXPOSE_INTERNAL(0, 0, null), EXPOSE_INTERNAL_URGENT(20000, 1, null), EXPOSE_INTERNAL_FEATURED(20000, 1, null), EXPOSE_INTERNAL_PRIORITY(40000, 1, null),
        EXPOSE_EXTERNAL(50000, 1, null), EXPOSE_EXTERNAL_URGENT(30000, 1, null), EXPOSE_EXTERNAL_FEATURED(30000, 1, null), EXPOSE_EXTERNAL_PRIORITY(50000, 1, null),
        NDA_IPA(20000, 1, null), HIGH_QUALITY_SMS(40000, null, 1);

        @Getter
        int unitPrice;
        @Getter
        Integer unitPeriodWeek;
        @Getter
        Integer count;
        Contest(int unitPrice, Integer unitPeriodWeek, Integer count) {
            this.unitPrice = unitPrice;
            this.unitPeriodWeek = unitPeriodWeek;
            this.count = count;
        }
    }

    public enum ContestPack {
        ECONOMY, PREMIUM, PLATINUM
    }

    public enum ContestBid {
        ONE_ADDITIONAL_BID(10000, 1), FEATURED(10000, 1), CREATIVE(10000, 1), PRIORITY(20000, 1), HIGH_QUALITY(20000, 1);

        @Getter
        int unitPrice;
        @Getter
        Integer count;

        ContestBid(int unitPrice, Integer count) {
            this.unitPrice = unitPrice;
            this.count = count;
        }
    }
}
