package com.freelancerk.service;

import com.freelancerk.domain.Project;

public interface KeywordOrSectorAlarmService {

    void sendMail(Project project);

    String makeProjectPostedEmailContent(Project project, String email);
}
