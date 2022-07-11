package com.freelancerk.gateway;

import lombok.Data;

@Data
public class GetTokenResponseModel extends IamportResponse {

    Response response;

    @Data
    public class Response {
        String access_token;
        Long now;
        Long expired_at;
    }
}
