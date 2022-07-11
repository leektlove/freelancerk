package com.freelancerk.type;

import lombok.Getter;

public enum PaymentMethod {
    CARD("카드결제"), TRANS("계좌이체");

    @Getter
    private String desc;

    PaymentMethod(String desc) {
        this.desc = desc;
    }
}
