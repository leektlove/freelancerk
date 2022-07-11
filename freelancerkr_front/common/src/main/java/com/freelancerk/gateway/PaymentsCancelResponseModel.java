package com.freelancerk.gateway;

import lombok.Data;

@Data
public class PaymentsCancelResponseModel extends IamportCommonResponse {

    private Response response;

    @Data
    public class Response {
        private String imp_uid;
        private String merchant_uid;
    }
}
