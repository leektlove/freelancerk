package com.freelancerk.exception;

import lombok.Data;

@Data
public class PayGatewayFailException extends RuntimeException {

    Integer code;
    String desc;

    public PayGatewayFailException(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
