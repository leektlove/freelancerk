package com.freelancerk.controller.view;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.domain.specification.ContestEntryTicketSpecifications;
import com.freelancerk.domain.specification.PickMeUpSpecifications;
import com.freelancerk.domain.specification.ProjectBidSpecifications;
import com.freelancerk.model.SortBy;
import com.freelancerk.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Controller
@RequestMapping("/freelancer/payment")
public class FreelancerPaymentViewController extends RootController {

	private UserService userService;
	private PickMeUpRepository pickMeUpRepository;
	private PurchaseRepository purchaseRepository;
	private ProjectBidRepository projectBidRepository;
	private PickMeUpTicketRepository pickMeUpTicketRepository;
	private PickMeUpTicketLogRepository pickMeUpTicketLogRepository;
	private ContestEntryTicketRepository contestEntryTicketRepository;
	private FreelancerPointLogRepository freelancerPointLogRepository;
	private FreelancerPayProductRepository freelancerPayProductRepository;
	private ContestEntryTicketLogRepository contestEntryTicketLogRepository;
	private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;

	@Autowired
	public FreelancerPaymentViewController(UserService userService, PickMeUpRepository pickMeUpRepository,
										   PurchaseRepository purchaseRepository, ProjectBidRepository projectBidRepository,
										   PickMeUpTicketRepository pickMeUpTicketRepository, PickMeUpTicketLogRepository pickMeUpTicketLogRepository,
										   ContestEntryTicketRepository contestEntryTicketRepository, FreelancerPointLogRepository freelancerPointLogRepository,
										   FreelancerPayProductRepository freelancerPayProductRepository,
										   ContestEntryTicketLogRepository contestEntryTicketLogRepository,
										   FreelancerProductItemTypeRepository freelancerProductItemTypeRepository) {
		this.userService = userService;
		this.pickMeUpRepository = pickMeUpRepository;
		this.purchaseRepository = purchaseRepository;
		this.projectBidRepository = projectBidRepository;
		this.pickMeUpTicketRepository = pickMeUpTicketRepository;
		this.pickMeUpTicketLogRepository = pickMeUpTicketLogRepository;
		this.contestEntryTicketRepository = contestEntryTicketRepository;
		this.freelancerPointLogRepository = freelancerPointLogRepository;
		this.freelancerPayProductRepository = freelancerPayProductRepository;
		this.contestEntryTicketLogRepository = contestEntryTicketLogRepository;
		this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
	}

	@GetMapping("/index")
	public String index(Model model) {

		Page<PickMeUp> pickMeUps = pickMeUpRepository.findAll(
				PickMeUpSpecifications.filterForFreelancerPaymentView(getSessionUserId()), PageRequest.of(0, 20, new Sort(Sort.Direction.DESC, "id")));

		for (PickMeUp pickMeUp: pickMeUps) {
			List<PickMeUpTicketLog> logs = pickMeUpTicketLogRepository.findByPickMeUpIdAndFreelancerProductItemTypeCodeAndInvalidFalseOrderByCreatedAtAsc(pickMeUp.getId(), FreelancerProductItemType.Code.PICK_ME_UP);
			int totalMinutes = 0;

			PickMeUpTicketLog priorLog = null;
			for (PickMeUpTicketLog log: logs) {
				if (log.getCreatedAt().isAfter(LocalDateTime.now())) {
					continue;
				}
				LocalDateTime endAt = LocalDateTime.now();
				if (log.getExpiredAt().isBefore(LocalDateTime.now())) {
					endAt = log.getExpiredAt();
				}
				if (priorLog != null && priorLog.getExpiredAt().isAfter(log.getCreatedAt())) {
					if (priorLog.getExpiredAt().until(endAt, ChronoUnit.MINUTES) < 0) continue;
					totalMinutes += priorLog.getExpiredAt().until(endAt, ChronoUnit.MINUTES);

				} else {
					if (log.getCreatedAt().until(endAt, ChronoUnit.MINUTES) < 0) continue;
					totalMinutes += log.getCreatedAt().until(endAt, ChronoUnit.MINUTES);
				}

				priorLog = log;
			}

			List<Purchase> purchases = purchaseRepository.findByTypeAndPickMeUpIdAndStatus(Purchase.Type.PICK_ME_UP, pickMeUp.getId(), Purchase.Status.COMPLETED);

			pickMeUp.setTotalSupplyAmount(purchases.stream().mapToInt(Purchase::getSupplyAmountOfMoney).sum());
			pickMeUp.setTotalPurchaseAmount(purchases.stream().mapToInt(Purchase::getTotalAmountOfMoney).sum());
			pickMeUp.setTotalDiscountAmount(
					purchases.stream().mapToInt(Purchase::getUsedPoints).sum()
							+ purchases.stream().mapToInt(Purchase::getTotalDiscountOptionPrice).sum()
			);
			pickMeUp.setTicketLogs(pickMeUpTicketLogRepository.findByPickMeUpIdAndInvalidFalse(pickMeUp.getId()));
			pickMeUp.setTotalPostingMinutes(totalMinutes);
			pickMeUp.setAllTickets(pickMeUpTicketRepository.findByPickMeUpIdAndInvalidFalse(pickMeUp.getId()));
		}

		Page<ProjectBid> contestEntries = projectBidRepository.findAll(
				ProjectBidSpecifications.filterForPayment(getSessionUserId(),  ProjectBid.BidType.CONTEST_BID),
				PageRequest.of(0, 20, new Sort(Sort.Direction.DESC, "id")));

		for (ProjectBid contestEntry: contestEntries) {
			contestEntry.setOptionItemIds(
					contestEntryTicketRepository.findAll(ContestEntryTicketSpecifications.filterForActiveTicketByContestEntryId(contestEntry.getId()))
							.stream()
							.map(ContestEntryTicket::getFreelancerProductItemType)
							.map(FreelancerProductItemType::getId)
							.collect(Collectors.toList()));

			List<ContestEntryTicketLog> logs = contestEntryTicketLogRepository.findByProjectBidIdAndInvalidFalse(contestEntry.getId());
			contestEntry.setTicketLogs(logs);
			List<Purchase> purchases = purchaseRepository.findByProjectBidIdAndStatus(contestEntry.getId(), Purchase.Status.COMPLETED);

			contestEntry.setTotalSupplyAmount(purchases.stream().mapToInt(Purchase::getSupplyAmountOfMoney).sum());
			contestEntry.setTotalPurchaseAmount(purchases.stream().mapToInt(Purchase::getTotalAmountOfMoney).sum());
			contestEntry.setTotalDiscountAmount(
					purchases.stream().mapToInt(Purchase::getTotalDiscountOptionPrice).sum()
							+ purchases.stream().mapToInt(Purchase::getUsedPoints).sum()
			);
		}

		List<FreelancerPointLog> pointLogs = freelancerPointLogRepository.findByUserIdOrderByCreatedAtDesc(getSessionUserId());
		for (FreelancerPointLog pointLog: pointLogs) {
			if (pointLog.getPurchase() != null) {
				pointLog.setPurchaseOptionDescription(
						freelancerPayProductRepository.findByPurchaseId(pointLog.getPurchase().getId()).stream()
								.map(FreelancerPayProduct::getFreelancerProductItemType)
								.map(FreelancerProductItemType::getSubName)
								.collect(Collectors.joining(", "))
				);
			}
		}

		List<FreelancerProductItemType> options = freelancerProductItemTypeRepository.findByTypeNotAndValidTrue(FreelancerProductItemType.Type.PACKAGE);

		model.addAttribute("pickMeUps", pickMeUps);
		model.addAttribute("contestEntries", contestEntries);
		model.addAttribute("options", options);
		model.addAttribute("user", userService.getCurrentUser());
		model.addAttribute("pointLogs", pointLogs);

		return "freelancer/payment/view";
	}
}
