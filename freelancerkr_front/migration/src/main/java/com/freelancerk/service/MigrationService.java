package com.freelancerk.service;

public interface MigrationService {

    String portfolioImagePrefix = "https://www.freelancerk.com/data/file/portfolio/";
    String portfolioDefaultImage = "/static/images/default_portfolio.png";
    String clientFilePrefix = "https://www.freelancerk.com/data/file/client/";
    String freelancerFilePrefix = "https://www.freelancerk.com/data/file/freelancer/";
    String projectFilePrefix = "https://www.freelancerk.com/data/file/project/";
    String noticeFilePrefix = "https://www.freelancerk.com/data/file/notice/";

    void migrateNotice();

    void migratePopup();

    void migrateFaq();

    void migrateClient();

    void migrateFreelancer();

    void migrateCategory();

    void migrateTag();

    void migrateClaim();

    void migrateClaimAnswer();

    void migrateProject();

    void migrateProjectFile();

    void migrateProjectRate();

    void migratePickMeUp();

    void migrateTemp();

    void giveInitialPoint();

    void successBidPrice();

    void migrateCellphoneCertified();

    void migrateCareerYear();

    void migratePickMeUpEtc();

    void migrateStrangeFreelancer();
}
