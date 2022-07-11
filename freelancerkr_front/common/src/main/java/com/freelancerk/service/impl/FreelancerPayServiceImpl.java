package com.freelancerk.service.impl;

import com.freelancerk.domain.*;
import com.freelancerk.domain.FreelancerPayProduct.Type;
import com.freelancerk.domain.repository.*;
import com.freelancerk.policy.PaymentPolicy;
import com.freelancerk.service.FreelancerPayService;
import com.freelancerk.service.PointService;
import com.freelancerk.util.PointCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class FreelancerPayServiceImpl implements FreelancerPayService {

	private PointService pointService;
	private PickMeUpRepository pickMeUpRepository;
	private PickMeUpTicketRepository pickMeUpTicketRepository;
	private PickMeUpTicketLogRepository pickMeUpTicketLogRepository;
	private FreelancerPointLogRepository freelancerPointLogRepository;
	private PickMeUpFreeChargeRepository pickMeUpFreeChargeRepository;
	private FreelancerPayProductRepository freelancerPayProductRepository;
	private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;

	@Autowired
	public FreelancerPayServiceImpl(PointService pointService, PickMeUpRepository pickMeUpRepository,
									PickMeUpTicketRepository pickMeUpTicketRepository,
									PickMeUpTicketLogRepository pickMeUpTicketLogRepository,
									FreelancerPointLogRepository freelancerPointLogRepository,
									PickMeUpFreeChargeRepository pickMeUpFreeChargeRepository,
									FreelancerPayProductRepository freelancerPayProductRepository,
									FreelancerProductItemTypeRepository freelancerProductItemTypeRepository) {
		this.pointService = pointService;
		this.pickMeUpRepository = pickMeUpRepository;
		this.pickMeUpTicketRepository = pickMeUpTicketRepository;
		this.pickMeUpTicketLogRepository = pickMeUpTicketLogRepository;
		this.freelancerPointLogRepository = freelancerPointLogRepository;
		this.pickMeUpFreeChargeRepository = pickMeUpFreeChargeRepository;
		this.freelancerPayProductRepository = freelancerPayProductRepository;
		this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
	}

	@Override
	public void insertOptionTemp(PickMeUp pickMeUp, List<FreelancerPayProduct> payProductList) {

		if (pickMeUp.getPayProductList() != null) {
			pickMeUp.getPayProductList().clear();
		}
		for (FreelancerPayProduct payProduct: payProductList) {

			payProduct.setFreelancerProductItemType(freelancerProductItemTypeRepository.getOne(payProduct.getFreelancerProductItemTypeId()));
			payProduct.setPickMeUp(pickMeUp);
		}

		freelancerPayProductRepository.saveAll(payProductList);
	}

	@Transactional
	@Override
	public void purchasePickMeUp(Long userId, Long pickMeUpId, int usedPoints, User user, Purchase purchase) {
		int freeChargeProductCount = countFreeProductCountOfPickMeUp(userId);
		PickMeUp pickMeUp = pickMeUpRepository.getOne(pickMeUpId);

		int point = (purchase == null || purchase.getSupplyAmountOfMoney() == 0)?0:PointCalculator.getPoint(purchase.getSupplyAmountOfMoney(), User.Role.ROLE_FREELANCER);

		pointService.getRidOfPointsFromFreelancerForPickMeUp(userId, usedPoints, purchase);
		pointService.givePointsToFreelancerForPickMeUp(user, usedPoints, point, "픽미업 결제 포인트 지급", pickMeUp, purchase);

		List<PickMeUpTicket> pickMeUpTickets = new ArrayList<>();

		List<FreelancerPayProduct> payProducts = freelancerPayProductRepository.findByPickMeUpIdAndPurchaseIdIsNull(pickMeUpId);
		for (FreelancerPayProduct payProduct: payProducts) {
			PickMeUpTicket ticket = pickMeUpTicketRepository.findTop1ByPickMeUpIdAndFreelancerProductItemTypeIdAndUserIdOrderByEndAtDesc(
					pickMeUpId, payProduct.getFreelancerProductItemType().getId(), pickMeUp.getUser().getId());
			int count = payProduct.getCount() == null?1:payProduct.getCount();
			if (ticket == null) {
				ticket = new PickMeUpTicket();
				ticket.setFreelancerProductItemType(payProduct.getFreelancerProductItemType());
				ticket.setPickMeUp(pickMeUp);
				ticket.setUser(pickMeUp.getUser());
				ticket.setStartAt(LocalDateTime.now());
				ticket.setEndAt(LocalDateTime.now().plusMonths(count));
			} else {
				if (ticket.getEndAt().isBefore(LocalDateTime.now())) {
					ticket.setStartAt(LocalDateTime.now());
					ticket.setEndAt(LocalDateTime.now().plusMonths(count));
				} else if (ticket.getStartAt().isBefore(LocalDateTime.now())) {
					ticket.setEndAt(ticket.getEndAt().plusMonths(count));
				} else {
					ticket.setStartAt(LocalDateTime.now());
					ticket.setEndAt(LocalDateTime.now().plusMonths(count));
				}
			}
			pickMeUpTickets.add(ticket);

			PickMeUpTicketLog pickMeUpTicketLog = new PickMeUpTicketLog();
			pickMeUpTicketLog.setFreelancerProductItemType(ticket.getFreelancerProductItemType());
			pickMeUpTicketLog.setExpiredAt(ticket.getEndAt());
			pickMeUpTicketLog.setPickMeUp(pickMeUp);
			pickMeUpTicketLog.setPurchase(purchase);
			pickMeUpTicketLog.setOptionCount(count);
			if (ticket.getFreelancerProductItemType().getCode().equals(FreelancerProductItemType.Code.PICK_ME_UP)
					&& freeChargeProductCount < PaymentPolicy.PICK_ME_UP_FREE_CHARGE_COUNT) {
				int chargeProductCount = count;
				if (chargeProductCount < freeChargeProductCount ) {
					chargeProductCount = 0;
				} else {
					chargeProductCount -= freeChargeProductCount;
				}
				pickMeUpTicketLog.setOptionPrice(ticket.getFreelancerProductItemType().getUnitPrice() * chargeProductCount);

				PickMeUpFreeCharge pickMeUpFreeCharge = new PickMeUpFreeCharge();
				pickMeUpFreeCharge.setPickMeUp(pickMeUp);
				pickMeUpFreeCharge.setUser(user);
				pickMeUpFreeCharge.setCount(chargeProductCount);
				pickMeUpFreeChargeRepository.save(pickMeUpFreeCharge);

				pickMeUp.setFreeCharge(true);
				ticket.setEndAt(LocalDateTime.now().plusYears(1000));
				pickMeUpTicketLog.setExpiredAt(ticket.getEndAt());
			} else {
				pickMeUpTicketLog.setOptionPrice(count * ticket.getFreelancerProductItemType().getUnitPrice());
			}
			pickMeUpTicketLogRepository.save(pickMeUpTicketLog);
		}

		pickMeUp.setTickets(new LinkedHashSet<>(pickMeUpTickets));
		pickMeUp.setInvalid(false);
		pickMeUp.setTemp(false);
		pickMeUpRepository.save(pickMeUp);

		updatePurchase(pickMeUpId, payProducts, purchase);
	}
	

	/**
	 * 상품 정보 ID로 상품 정보 전체를 가져온다.
	 */
	@Override
	public FreelancerProductItemType getProductItemById(Long itemId) {
		return freelancerProductItemTypeRepository.getOne(itemId);
	}
	
	/**
	 * 결제한 상품 전체를 DB에 저장한다.
	 */
	@Override
	public void savePayProductList(List<FreelancerPayProduct> payProductList) {
		for (FreelancerPayProduct product : payProductList) {
			freelancerPayProductRepository.save(product);
		}
	}

	/**
	 * 결제한 상품 리스트를 종류에 따라 가져온다.
	 */
	@Override
	public List<FreelancerPayProduct> getUnPurchasedPayProductList(Type type, Long pickMeUpId) {
		if(type == FreelancerPayProduct.Type.FOR_PICK_ME_UP) {
			return freelancerPayProductRepository.findByPickMeUpIdAndPurchaseIdIsNull(pickMeUpId);
		} else {
			return freelancerPayProductRepository.findByContestEntryIdAndPurchaseIdIsNull(pickMeUpId);
		}
	}

	@Override
	public List<FreelancerProductItemType> getProductItemTypeList(String usageType) {
		return freelancerProductItemTypeRepository.findByUsageTypeContainingAndValidTrue(
				usageType, Sort.by(Sort.Direction.ASC, "seq"));
	}

	@Override
    public void updatePurchase(Long pickMeUpId, List<FreelancerPayProduct> payProducts, Purchase purchase) {
		if (purchase == null) return;
		for (FreelancerPayProduct payProduct: payProducts) {
			payProduct.setPurchase(purchase);
			freelancerPayProductRepository.save(payProduct);
		}
	}


	@Override
	public void deleteUnPurchasedProductList(Long pickMeUpId) {
		freelancerPayProductRepository.deleteByPickMeUpIdAndPurchaseIdIsNull(pickMeUpId);
	}

	@Override
	public int countFreeProductCountOfPickMeUp(Long userId) {
		return pickMeUpFreeChargeRepository.countByUserId(userId);
	}
}
