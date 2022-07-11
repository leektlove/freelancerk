package com.freelancerk.service;

public interface SmsService {

    String sendMessageAndReturnId(String message, String to);
}
