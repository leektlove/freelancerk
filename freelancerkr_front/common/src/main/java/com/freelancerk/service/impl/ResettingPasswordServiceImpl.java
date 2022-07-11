package com.freelancerk.service.impl;

import com.freelancerk.domain.ResettingPassword;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.ResettingPasswordRepository;
import com.freelancerk.gateway.EmailService;
import com.freelancerk.service.FrkEmailService;
import com.freelancerk.service.ResettingPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
public class ResettingPasswordServiceImpl implements ResettingPasswordService {

    @Value("${server.url}") String serverUrl;

    private EmailService emailService;
    private TemplateEngine templateEngine;
    private FrkEmailService frkEmailService;
    private ResettingPasswordRepository resettingPasswordRepository;

    @Autowired
    public ResettingPasswordServiceImpl(EmailService emailService, TemplateEngine templateEngine,
                                        FrkEmailService frkEmailService,
                                        ResettingPasswordRepository resettingPasswordRepository) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
        this.frkEmailService = frkEmailService;
        this.resettingPasswordRepository = resettingPasswordRepository;
    }

    @Transactional
    @Override
    public void resetPassword(User user, String email) {
        ResettingPassword resettingPassword = new ResettingPassword();
        resettingPassword.setEmail(email);
        resettingPassword.setUser(user);
        resettingPassword.setToken(UUID.randomUUID().toString());
        resettingPassword.setExpiredAt(LocalDateTime.now().plusHours(24));
        resettingPasswordRepository.save(resettingPassword);

        frkEmailService.sendEmailResetLink(user.getName(),
                String.format("%s/public/resetting-for-password?token=%s", serverUrl, resettingPassword.getToken()), email);
    }
}
