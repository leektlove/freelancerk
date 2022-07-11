package com.freelancerk.service.impl;

import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.ContestEntryTicketLogRepository;
import com.freelancerk.domain.repository.ContestEntryTicketRepository;
import com.freelancerk.domain.repository.FreelancerPayProductRepository;
import com.freelancerk.domain.repository.FreelancerProductItemTypeRepository;
import com.freelancerk.domain.specification.ContestEntryTicketSpecifications;
import com.freelancerk.service.ContestEntryTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContestEntryTicketServiceImpl implements ContestEntryTicketService {

    private ContestEntryTicketRepository contestEntryTicketRepository;
    private FreelancerPayProductRepository freelancerPayProductRepository;
    private ContestEntryTicketLogRepository contestEntryTicketLogRepository;
    private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;

    @Autowired
    public ContestEntryTicketServiceImpl(ContestEntryTicketRepository contestEntryTicketRepository, FreelancerPayProductRepository freelancerPayProductRepository,
                                         ContestEntryTicketLogRepository contestEntryTicketLogRepository, FreelancerProductItemTypeRepository freelancerProductItemTypeRepository) {
        this.contestEntryTicketRepository = contestEntryTicketRepository;
        this.freelancerPayProductRepository = freelancerPayProductRepository;
        this.contestEntryTicketLogRepository = contestEntryTicketLogRepository;
        this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
    }

    @Override
    public void makeTicket(ProjectBid projectBid, Purchase purchase, List<FreelancerPayProduct> freelancerPayProducts) {
        for (FreelancerPayProduct payProduct: freelancerPayProducts) {

            Integer weeks = payProduct.getCount();
            payProduct.setType(FreelancerPayProduct.Type.FOR_CONTEST_ENTRY);
            payProduct.setFreelancerProductItemType(freelancerProductItemTypeRepository.getOne(payProduct.getFreelancerProductItemTypeId()));
            payProduct.setPurchase(purchase);
            freelancerPayProductRepository.save(payProduct);

            ContestEntryTicket contestEntryTicket = contestEntryTicketRepository.findTop1ByFreelancerProductItemTypeIdAndProjectBidId(
                    payProduct.getFreelancerProductItemTypeId(), projectBid.getId());

            if (contestEntryTicket == null) {
                contestEntryTicket = new ContestEntryTicket();
                contestEntryTicket.setEndAt(LocalDateTime.now().plusMonths(weeks));
            } else {
                contestEntryTicket.setEndAt(contestEntryTicket.getEndAt().plusMonths(weeks));
            }
            contestEntryTicket.setPrice(payProduct.isUsedInPickMeUp()?0:payProduct.getFreelancerProductItemType().getUnitPrice() * weeks);
            contestEntryTicket.setUser(projectBid.getParticipant());
            contestEntryTicket.setProjectBid(projectBid);
            contestEntryTicket.setFreelancerProductItemType(payProduct.getFreelancerProductItemType());
            contestEntryTicketRepository.save(contestEntryTicket);

            ContestEntryTicketLog log = new ContestEntryTicketLog();
            log.setProjectBid(projectBid);
            log.setPrice(contestEntryTicket.getPrice());
            log.setFreelancerProductItemType(contestEntryTicket.getFreelancerProductItemType());
            log.setExpiredAt(LocalDateTime.now().plusWeeks(weeks));
            log.setPurchase(purchase);
            contestEntryTicketLogRepository.save(log);
        }
    }

    @Override
    public List<ContestEntryTicket> getActiveTickets(Long contestId, Long userId) {
        return contestEntryTicketRepository.findAll(ContestEntryTicketSpecifications.filterForActiveTicketByUserIdAndProjectId(userId, contestId));
    }
}
