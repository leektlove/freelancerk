package com.freelancerk.controller.view;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.FreelancerProductItemTypeRepository;
import com.freelancerk.domain.repository.PickMeUpTicketRepository;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.specification.PickMeUpTicketSpecifications;
import com.freelancerk.service.ContestApplyService;
import com.freelancerk.service.ContestEntryTicketService;
import com.freelancerk.service.UserService;
import com.freelancerk.util.OptionDiscountCalculator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/freelancer/contestDetail")
public class FreelancerContestDetailController extends RootController {

	private UserService userService;
	private ProjectRepository projectRepository;
	private ContestApplyService contestApplyService;
	private ProjectBidRepository projectBidRepository;
	private PickMeUpTicketRepository pickMeUpTicketRepository;
	private ContestEntryTicketService contestEntryTicketService;
	private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;

	@Autowired
	public FreelancerContestDetailController(UserService userService, PickMeUpTicketRepository pickMeUpTicketRepository,
											 ProjectBidRepository projectBidRepository,
											 FreelancerProductItemTypeRepository freelancerProductItemTypeRepository,
											 ContestEntryTicketService contestEntryTicketService,
											 ProjectRepository projectRepository, ContestApplyService contestApplyService) {
		this.userService = userService;
		this.projectRepository = projectRepository;
		this.contestApplyService = contestApplyService;
		this.projectBidRepository = projectBidRepository;
		this.pickMeUpTicketRepository = pickMeUpTicketRepository;
		this.contestEntryTicketService = contestEntryTicketService;
		this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
	}

	@GetMapping("/selectOption")
	public String getSelectOptionView(@RequestParam("contestId") Long contestId,
									  @RequestParam(value = "mainPiecesFileUrl", required = false) String mainPiecesFileUrl,
									  @RequestParam(value = "mode", required = false) String mode,
									  @RequestParam(value = "subPiecesFileUrl[]", required = false) String[] subPiecesFileUrls,
									  @RequestParam(value = "croppedSubPiecesFileUrl[]", required = false) String[] croppedSubPiecesFileUrls,
									  @RequestParam(value = "videoThumbnailImageUrl", required = false) String videoThumbnailImageUrl,
									  HttpServletRequest request, Model model) {

		Set<FreelancerProductItemType> validFreelancerProductItemTypes = pickMeUpTicketRepository.findAll(
				PickMeUpTicketSpecifications.filterForActiveTicketForUser(getSessionUserId()))
				.stream()
				.map(PickMeUpTicket::getFreelancerProductItemType)
				.collect(Collectors.toSet());

		List<FreelancerProductItemType> freelancerProductItemTypes = freelancerProductItemTypeRepository.findByUsedInContestEntryTrueAndValidTrue();

		for (FreelancerProductItemType type: freelancerProductItemTypes) {
			type.setUsedInPickMeUp(validFreelancerProductItemTypes.contains(type));
		}

		ProjectBid projectBid = projectBidRepository.findTop1ByParticipantIdAndProjectIdOrderByCreatedAtDesc(getSessionUserId(), contestId);
		if (StringUtils.isNotEmpty(mode) && projectBid != null) {

			List<ContestEntryTicket> activeContestEntryTickets = contestEntryTicketService.getActiveTickets(contestId, getSessionUserId());
			projectBid.setOptionItemIds(activeContestEntryTickets.stream().map(ContestEntryTicket::getFreelancerProductItemType).map(FreelancerProductItemType::getId).collect(Collectors.toList()));
			model.addAttribute("contestEntry", projectBid);
			model.addAttribute("mode", mode);
		}

		model.addAttribute("usingProductList", validFreelancerProductItemTypes);
		model.addAttribute("productList", freelancerProductItemTypes);

		model.addAttribute("contestId", contestId);
		model.addAttribute("mainPiecesFileUrl", mainPiecesFileUrl);
		model.addAttribute("croppedMainPiecesFileUrl", mainPiecesFileUrl);
		model.addAttribute("subPiecesFileUrls", subPiecesFileUrls);
		model.addAttribute("croppedSubPiecesFileUrls", croppedSubPiecesFileUrls);
		model.addAttribute("videoThumbnailImageUrl", videoThumbnailImageUrl);

		return "freelancer/details/applyContestSelectOptionView";
	}

	@GetMapping("/payConfirm")
	public String getPayConfirmView(
			@RequestParam("contestId") Long contestId,
			@RequestParam(value = "payment", required = false) Boolean payment,
 			@RequestParam(value = "selectProductList", required = false) String jsonSelectProductList,
			@RequestParam("mainPiecesFileUrl") String mainPiecesFileUrl,
			@RequestParam("croppedMainPiecesFileUrl") String croppedMainPiecesFileUrl,
			@RequestParam(value = "subPiecesFileUrl[]", required = false) String[] subPiecesFileUrls,
			@RequestParam(value = "croppedSubPiecesFileUrl[]", required = false) String[] croppedSubPiecesFileUrls,
			@RequestParam(value = "videoThumbnailImageUrl", required = false) String videoThumbnailImageUrl,
			HttpServletRequest request, Model model) {

		List<FreelancerPayProduct> selectProductList = new Gson().fromJson(jsonSelectProductList, new TypeToken<ArrayList<FreelancerPayProduct>>(){}.getType());

		int chargedOptionsAmountOfMoney = 0;
		int supplyAmountOfMoney = 0;
		int vatAmountOfMoney = 0;
		int totalAmountOfMoney = 0;
		int chargedOptionCount = 0;

		Project contest = projectRepository.getOne(contestId);

		if ((payment != null && !payment) || selectProductList == null || selectProductList.size() == 0) {

			contestApplyService.apply(contest, getSessionUserId(), mainPiecesFileUrl, croppedMainPiecesFileUrl, subPiecesFileUrls, croppedSubPiecesFileUrls, videoThumbnailImageUrl);

			return "freelancer/details/contestApplyDone";
		}

		for (FreelancerPayProduct item : selectProductList) {
			item.setFreelancerProductItemType(freelancerProductItemTypeRepository.getOne(item.getFreelancerProductItemTypeId()));
			if (item.isUsedInPickMeUp()) {
				continue;
			}
			chargedOptionsAmountOfMoney += (item.getAmount() * item.getCount());
			chargedOptionCount += item.getCount();
			item.setFreelancerProductItemType(freelancerProductItemTypeRepository.getOne(item.getFreelancerProductItemTypeId()));
		}

		if (chargedOptionsAmountOfMoney == 0) {
			ProjectBid projectBid = contestApplyService.apply(contest, getSessionUserId(), mainPiecesFileUrl, croppedMainPiecesFileUrl, subPiecesFileUrls, croppedSubPiecesFileUrls, videoThumbnailImageUrl);
			contestEntryTicketService.makeTicket(projectBid, null, selectProductList);
			return "freelancer/details/contestApplyDone";
		}

		int discountPrice = OptionDiscountCalculator.getOptionDiscountAmountForFreelancer(chargedOptionCount, chargedOptionsAmountOfMoney);
		supplyAmountOfMoney = chargedOptionsAmountOfMoney - discountPrice;
		vatAmountOfMoney = supplyAmountOfMoney / 10;
		totalAmountOfMoney = supplyAmountOfMoney + vatAmountOfMoney;

		Purchase purchase = new Purchase();
		purchase.setChargedOptionsAmountOfMoney(chargedOptionsAmountOfMoney);
		purchase.setSupplyAmountOfMoney(supplyAmountOfMoney);
		purchase.setTotalAmountOfMoney(totalAmountOfMoney);
		purchase.setVatAmountOfMoney(vatAmountOfMoney);
		purchase.setTotalDiscountOptionPrice(discountPrice);


		model.addAttribute("contest", contest);
		model.addAttribute("mainPiecesFileUrl", mainPiecesFileUrl);
		model.addAttribute("croppedMainPiecesFileUrl", croppedMainPiecesFileUrl);
		model.addAttribute("subPiecesFileUrls", subPiecesFileUrls);
		model.addAttribute("croppedSubPiecesFileUrls", croppedSubPiecesFileUrls);
		model.addAttribute("croppedSubPiecesFileUrls", croppedSubPiecesFileUrls);
		model.addAttribute("videoThumbnailImageUrl", videoThumbnailImageUrl);

		model.addAttribute("purchase", purchase);

		model.addAttribute("point", userService.getCurrentUser().getPoints());
		model.addAttribute("selectProductList", selectProductList);
		model.addAttribute("jsonSelectProductList", jsonSelectProductList);


		return "freelancer/details/contestApplyPaymentConfirm";
	}

	@GetMapping("/payDone")
	public String payDone() {
		return "freelancer/details/contestApplyDone";
	}
}
