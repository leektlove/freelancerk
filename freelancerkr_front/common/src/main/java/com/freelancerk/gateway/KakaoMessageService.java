package com.freelancerk.gateway;

public interface KakaoMessageService {

    KakaoMessageSendResponse sendMessage(String templateCode, String receiver, String title, String content);
}
