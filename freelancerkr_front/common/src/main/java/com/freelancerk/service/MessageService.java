package com.freelancerk.service;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.User;

import java.util.Map;

public interface MessageService {

    void sendMessage(User user, AligoKakaoMessageTemplate.Code code, Map<String, Object> variables);

    void sendMessage(User user, String cellphone, AligoKakaoMessageTemplate.Code code, Map<String, Object> variables);

}
