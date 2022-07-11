package com.freelancerk.domain;

import lombok.Getter;

public enum TaxType {
    COLLECTION_IN_ADVANCE("원천징수"), VAT("부가세 납부");

    @Getter
    private String desc;

    TaxType(String desc) {
        this.desc = desc;
    }
}
