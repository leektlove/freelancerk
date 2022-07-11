package com.freelancerk.controller;

import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.service.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.freelancerk.domain.FreelancerPayProduct.Type.FOR_PICK_ME_UP;

@RestController
public class MigrationController {

    private PickMeUpRepository pickMeUpRepository;
    private MigrationService migrationService;
    private PickMeUpTicketRepository pickMeUpTicketRepository;
    private PickMeUpTicketLogRepository pickMeUpTicketLogRepository;
    private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;
    private FreelancerPayProductRepository freelancerPayProductRepository;

    @Autowired
    public MigrationController(PickMeUpRepository pickMeUpRepository,
                               MigrationService migrationService, FreelancerProductItemTypeRepository freelancerProductItemTypeRepository,
                               PickMeUpTicketRepository pickMeUpTicketRepository, PickMeUpTicketLogRepository pickMeUpTicketLogRepository,
                               FreelancerPayProductRepository freelancerPayProductRepository) {
        this.migrationService = migrationService;
        this.pickMeUpRepository = pickMeUpRepository;
        this.pickMeUpTicketRepository = pickMeUpTicketRepository;
        this.pickMeUpTicketLogRepository = pickMeUpTicketLogRepository;
        this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
        this.freelancerPayProductRepository = freelancerPayProductRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/migrations")
    public void triggerMigration() {
//        migrationService.migrateNotice();
//        migrationService.migrateFaq();
//        migrationService.migrateClient();
//        migrationService.migrateFreelancer();
//        migrationService.migrateTemp();
//        migrationService.migrateCategory();
//        migrationService.migrateTag();
//        migrationService.migrateProject();
//        migrationService.migrateProjectFile();
//        migrationService.migratePickMeUp();
//        migrationService.migrateProjectRate();
//        migrationService.giveInitialPoint();

//        migrationService.successBidPrice();
//        migrationService.migrateCellphoneCertified();
//        migrationService.migrateCareerYear();

//        List<Long> ids = Arrays.asList(3534L,3533L,3478L,3469L,3455L,3445L,3438L,3420L,3405L,2819L,3366L,3358L,3338L,3364L,3359L,3341L,3333L,3323L,3220L,3199L,3134L,3120L,3107L,3072L,3030L,3003L,2988L,2503L,2919L,2868L,2805L,1616L,2741L,449L,2581L,2389L,2816L,2544L,1324L,117L,294L,1209L,3361L,808L,3439L,1815L,472L);
//        List<PickMeUp> pickMeUpList = pickMeUpRepository.findAllById(ids);
//        FreelancerProductItemType item = freelancerProductItemTypeRepository.findByCode(FreelancerProductItemType.Code.DIRECT_DEAL);
//        for (PickMeUp pickMeUp: pickMeUpList) {
//            FreelancerPayProduct freelancerPayProduct = new FreelancerPayProduct();
//            freelancerPayProduct.setFreelancerProductItemType(item);
//            freelancerPayProduct.setPickMeUp(pickMeUp);
//            freelancerPayProduct.setFree(true);
//            freelancerPayProduct.setAmount(0);
//            freelancerPayProduct.setCount(0);
//            freelancerPayProduct.setType(FOR_PICK_ME_UP);
//            freelancerPayProduct.setIncludedInPackage(false);
//            freelancerPayProductRepository.save(freelancerPayProduct);
//
//            PickMeUpTicket ticket = new PickMeUpTicket();
//            ticket.setStartAt(LocalDateTime.now());
//            ticket.setEndAt(LocalDateTime.now().plusMonths(3));
//            ticket.setPickMeUp(pickMeUp);
//            ticket.setUser(pickMeUp.getUser());
//            pickMeUpTicketRepository.save(ticket);
//
//            PickMeUpTicketLog ticketLog = new PickMeUpTicketLog();
//            ticketLog.setPickMeUp(pickMeUp);
//            ticketLog.setFreelancerProductItemType(item);
//            ticketLog.setExpiredAt(ticket.getEndAt());
//            pickMeUpTicketLogRepository.save(ticketLog);
//        }

//        migrationService.migrateClient();
        migrationService.migrateStrangeFreelancer();
    }
}
