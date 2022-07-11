package com.freelancerk.gateway;

public interface EmailService {

    void sendEmail(String to, String subject, String content);
}
