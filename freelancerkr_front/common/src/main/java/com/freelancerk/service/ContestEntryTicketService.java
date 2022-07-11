package com.freelancerk.service;

import com.freelancerk.domain.ContestEntryTicket;
import com.freelancerk.domain.FreelancerPayProduct;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.Purchase;

import java.util.List;

public interface ContestEntryTicketService {

    void makeTicket(ProjectBid projectBid, Purchase purchase, List<FreelancerPayProduct> freelancerPayProducts);

    List<ContestEntryTicket> getActiveTickets(Long contestId, Long userId);
}
