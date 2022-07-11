package com.freelancerk.gateway;

import lombok.Data;

@Data
public class PaymentsCancelRequestParam {
    private String imp_uid;
    private String reason;
}
