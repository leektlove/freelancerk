package com.freelancerk.gateway;

import lombok.Data;

@Data
public class KakaoMessageTokenResponse extends KakaoMessageCommonResponse {

    private String token;
}
