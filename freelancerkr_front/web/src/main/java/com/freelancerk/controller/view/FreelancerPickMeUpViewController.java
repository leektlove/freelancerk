package com.freelancerk.controller.view;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancerk.TimeUtil;
import com.freelancerk.controller.RootController;
import com.freelancerk.controller.io.SelectedPickMeUpOption;
import com.freelancerk.domain.Category;
import com.freelancerk.domain.ContactAvailableDayTime;
import com.freelancerk.domain.FreelancerPayProduct;
import com.freelancerk.domain.FreelancerProductItemType;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.PickMeUpTicket;
import com.freelancerk.domain.Purchase;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.ContactAvailableDayTimeRepository;
import com.freelancerk.domain.repository.FreelancerProductItemTypeRepository;
import com.freelancerk.domain.repository.PickMeUpFreeChargeRepository;
import com.freelancerk.domain.repository.PickMeUpRepository;
import com.freelancerk.domain.repository.PickMeUpTicketRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.domain.specification.PickMeUpSpecifications;
import com.freelancerk.domain.specification.PickMeUpTicketSpecifications;
import com.freelancerk.model.ContactAvailableDayTimeModel;
import com.freelancerk.policy.PaymentPolicy;
import com.freelancerk.service.FreelancerPayService;
import com.freelancerk.service.PickMeUpService;
import com.freelancerk.util.OptionDiscountCalculator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/freelancer/pickMeUp")
public class FreelancerPickMeUpViewController extends RootController {

	@Value("${server.url}") String serverUrl;

	private UserRepository userRepository;
	private PickMeUpService pickMeUpService;
	private CategoryRepository categoryRepository;
	private PickMeUpRepository pickMeUpRepository;
	private FreelancerPayService freelancerPayService;
	private PickMeUpTicketRepository pickMeUpTicketRepository;
	private PickMeUpFreeChargeRepository pickMeUpFreeChargeRepository;
	private ContactAvailableDayTimeRepository contactAvailableDayTimeRepository;
	private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;

	@Autowired
	public FreelancerPickMeUpViewController(PickMeUpService pickMeUpService, UserRepository userRepository,
											CategoryRepository categoryRepository, PickMeUpRepository pickMeUpRepository,
											FreelancerPayService freelancerPayService,
											PickMeUpTicketRepository pickMeUpTicketRepository,
											PickMeUpFreeChargeRepository pickMeUpFreeChargeRepository,
											ContactAvailableDayTimeRepository contactAvailableDayTimeRepository,
											FreelancerProductItemTypeRepository freelancerProductItemTypeRepository) {
		this.userRepository = userRepository;
		this.pickMeUpService = pickMeUpService;
		this.categoryRepository = categoryRepository;
		this.pickMeUpRepository = pickMeUpRepository;
		this.freelancerPayService = freelancerPayService;
		this.pickMeUpTicketRepository = pickMeUpTicketRepository;
		this.pickMeUpFreeChargeRepository = pickMeUpFreeChargeRepository;
		this.contactAvailableDayTimeRepository = contactAvailableDayTimeRepository;
		this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
	}
	
	@GetMapping("/choiceEnrollment")
	public String choiceEnrollment() {
		return "freelancer/pickMeUp/choiceEnrollment";
	}

	@GetMapping("/list")
	public String freelancerPickMeUpList(
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
			@RequestParam(value = "category1stId", required = false) Long category1stId,
			@RequestParam(value = "category2ndId", required = false) Long category2ndId,
			Model model) {
		int totalPortfolioCount = pickMeUpService.countPortfolio(getSessionUserId());

		Page<PickMeUp> page = pickMeUpRepository.findAll(
				PickMeUpSpecifications.filter(
						category1stId != null?categoryRepository.getOne(category1stId):null,
						category2ndId != null?categoryRepository.getOne(category2ndId):null,
						getSessionUserId(), null, null, null, null),
				PageRequest.of(pageNumber, 100));

		for (PickMeUp pickMeUp: page) {
			pickMeUpService.setValidTicketItem(pickMeUp);
		}

		model.addAttribute("category1stId", category1stId);
		model.addAttribute("category2ndId", category2ndId);
		model.addAttribute("pickMeUpIds", StringUtils.join(page.getContent().stream().map(PickMeUp::getId).collect(Collectors.toList()), ","));
		model.addAttribute("totalPortfolioCount", totalPortfolioCount);
		model.addAttribute("pickMeUpPages", page);
		model.addAttribute("pickMeUpPageNumber", pageNumber);
		model.addAttribute("totalPickMeUpPages", page.getTotalPages());

		return "freelancer/pickMeUp/list";
	}

	// 등록
	@GetMapping("/create")
	public String create(Model model, @RequestParam(value = "projectId", required = false) Long projectId,
						 @RequestParam(value = "contentType", defaultValue = "IMAGE") PickMeUp.ContentType contentType) {
		PickMeUp pickMeUp = new PickMeUp();
		pickMeUp.setHits(0);
		pickMeUp.setContentType(contentType);

		model.addAttribute("mode", "OPEN");
		model.addAttribute("remainFreeChargeProductCount",
				PaymentPolicy.PICK_ME_UP_FREE_CHARGE_COUNT - freelancerPayService.countFreeProductCountOfPickMeUp(getSessionUserId()));
		model.addAttribute("pickMeUp", pickMeUp);
		model.addAttribute("productList", freelancerPayService.getProductItemTypeList(FreelancerProductItemType.UsageType.PICK_ME_UP.name()));
		model.addAttribute("projectId", projectId);
		return "freelancer/pickMeUp/details";
	}

	@GetMapping("/createOption")
	public String createOption(Model model, HttpServletRequest request) {
		PickMeUp pickMeUp = pickMeUpRepository.getOne((Long) request.getSession().getAttribute("pickMeUpId"));

		FreelancerProductItemType pickMeUpDirectDealItem = freelancerProductItemTypeRepository.findByCode(FreelancerProductItemType.Code.DIRECT_DEAL);

		model.addAttribute("mode", "OPEN");
		model.addAttribute("remainFreeChargeProductCount",
				PaymentPolicy.PICK_ME_UP_FREE_CHARGE_COUNT - freelancerPayService.countFreeProductCountOfPickMeUp(getSessionUserId()));
		model.addAttribute("pickMeUp", pickMeUp);
		model.addAttribute("productList", freelancerPayService.getProductItemTypeList(FreelancerProductItemType.UsageType.PICK_ME_UP.name()));

		ContactAvailableDayTime contactAvailableDayTime = contactAvailableDayTimeRepository.findByPickMeUpId(pickMeUp.getId());
		if (contactAvailableDayTime != null) {
			List<ContactAvailableDayTimeModel> dayTimeModels = new ArrayList<>();
			for (String key: contactAvailableDayTime.getDayTimes().keySet()) {
				if (StringUtils.isEmpty(key)) continue;
				ContactAvailableDayTimeModel dayTimeModel = new ContactAvailableDayTimeModel();
				dayTimeModel.setDays(Arrays.asList(key.split(",")));
				dayTimeModel.setTimes(contactAvailableDayTime.getDayTimes().get(key));
				dayTimeModels.add(dayTimeModel);
			}
			model.addAttribute("dayTimes", dayTimeModels);
		}

		List<FreelancerProductItemType> pickMeUpPayProducts
				= pickMeUpTicketRepository.findByPickMeUpId(pickMeUp.getId())
				.stream()
				.map(PickMeUpTicket::getFreelancerProductItemType)
				.collect(Collectors.toList());
		pickMeUp.setDirectDealAvailable(pickMeUpPayProducts.contains(pickMeUpDirectDealItem));

		return "freelancer/pickMeUp/detailsOption";
	}
	
	@GetMapping("/paymentConfirm")
	public String paymentConfirm(
			@RequestParam(value = "mode", required = false) String mode,
			HttpServletRequest request, Model model) {
		Long pickMeUpId = (Long) request.getSession().getAttribute("pickMeUpId");

		List<FreelancerPayProduct> selectProductList = freelancerPayService.getUnPurchasedPayProductList(FreelancerPayProduct.Type.FOR_PICK_ME_UP, pickMeUpId);
		if (selectProductList.isEmpty()) {
			return String.format("redirect:%s/freelancer/pickMeUp/list", serverUrl);
		}

		model.addAttribute("selectProductList", selectProductList);

		int chargedOptionsAmountOfMoney = 0;
		int supplyAmountOfMoney = 0;
		int vatAmountOfMoney = 0;
		int totalAmountOfMoney = 0;
		int discountAmount = 0;

		Purchase purchase = new Purchase();
		purchase.setStatus(Purchase.Status.REQUESTED);

		int pickMeUpFreeChargeCount = 2 - pickMeUpFreeChargeRepository.countByUserId(getSessionUserId());
		int chargedOptionCount = 0;
		for (FreelancerPayProduct item : selectProductList) {
			if (item.isIncludedInPackage()) continue;
			String typeName = item.getFreelancerProductItemType().getType().name();
			if ("STANDARD".equals(typeName)) {
				if (FreelancerProductItemType.Code.PICK_ME_UP.equals(item.getFreelancerProductItemType().getCode())) {
					item.setAmount(item.getFreelancerProductItemType().getUnitPrice() * (item.getCount() - pickMeUpFreeChargeCount));
					if (item.getAmount() < 0) {
						item.setAmount(0);
					}
				}

                chargedOptionsAmountOfMoney += item.getAmount();
			} else {
				item.setAmount(item.getAmount() * item.getCount());
                chargedOptionsAmountOfMoney += (item.getFreelancerProductItemType().getUnitPrice() * item.getCount());
			}
			chargedOptionCount += item.getCount();
		}

		discountAmount = OptionDiscountCalculator.getOptionDiscountAmountForFreelancer(chargedOptionCount, chargedOptionsAmountOfMoney);

		supplyAmountOfMoney += (chargedOptionsAmountOfMoney - discountAmount);
		vatAmountOfMoney = supplyAmountOfMoney / 10;
		totalAmountOfMoney = supplyAmountOfMoney;

		purchase.setChargedOptionsAmountOfMoney(chargedOptionsAmountOfMoney);
		purchase.setSupplyAmountOfMoney(supplyAmountOfMoney);
		purchase.setTotalAmountOfMoney(totalAmountOfMoney);
		purchase.setVatAmountOfMoney(vatAmountOfMoney);
		purchase.setTotalDiscountOptionPrice(discountAmount);
		purchase.setType(Purchase.Type.PICK_ME_UP);
		purchase.setExtend("EXTEND".equalsIgnoreCase(mode));

		model.addAttribute("pickMeUp", pickMeUpRepository.getOne(pickMeUpId));
		model.addAttribute("purchase", purchase);
		model.addAttribute("newPoint", (chargedOptionsAmountOfMoney - discountAmount) * 0.10);

		model.addAttribute("serverUrl", serverUrl);
		model.addAttribute("mode", mode);

		return "freelancer/pickMeUp/paymentConfirm";
	}

	@GetMapping("/enrollmentDone")
	public String enrollmentDone(@RequestParam(value = "mode", required = false) String mode,
								 Model model) {
		model.addAttribute("mode", mode);
		return "freelancer/pickMeUp/enrollmentDone";
	}

	@Transactional
	@PostMapping("/options")
	public RedirectView open(
			@RequestParam(value = "pickMeUpId", required = false) Long pickMeUpId,
			@RequestParam(value = "selectProductList", required = false) String jsonSelectProductList,
			@RequestParam(value = "contactAvailableDayTime", required = false) String contactAvailableDayTimeJson,
			HttpServletRequest request) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<SelectedPickMeUpOption>> typeReference = new TypeReference<List<SelectedPickMeUpOption>>() {};
		List<SelectedPickMeUpOption> selectProductList = mapper.readValue(jsonSelectProductList, typeReference);

		PickMeUp pickMeUp = pickMeUpRepository.getOne(pickMeUpId);

		if (StringUtils.isNotEmpty(contactAvailableDayTimeJson)) {
			Map<String, List<String>> parsedMap = mapper.readValue(contactAvailableDayTimeJson, new TypeReference<Map<String, List<String>>>() {});
			contactAvailableDayTimeRepository.deleteByPickMeUpId(pickMeUpId);
			ContactAvailableDayTime contactAvailableDayTime = new ContactAvailableDayTime();
			contactAvailableDayTime.setPickMeUp(pickMeUp);
			contactAvailableDayTime.setDayTimes(parsedMap);
			contactAvailableDayTimeRepository.save(contactAvailableDayTime);
		}


		if (selectProductList!= null && selectProductList.size() > 0) {
			List<FreelancerPayProduct> payProductList = new ArrayList<>();
			for (SelectedPickMeUpOption item : selectProductList) {
				if (item.getCount() == 0 && item.getFreelancerProductItemTypeId() != 1L) continue;
				FreelancerProductItemType itemType = freelancerPayService.getProductItemById(item.getFreelancerProductItemTypeId());
				FreelancerPayProduct payProduct = new FreelancerPayProduct();
				payProduct.setType(FreelancerPayProduct.Type.FOR_PICK_ME_UP);
				payProduct.setPickMeUp(pickMeUp);
				payProduct.setAmount(itemType.getUnitPrice());
				payProduct.setCount(item.getCount());

				payProduct.setFreelancerProductItemType(itemType);
				payProductList.add(payProduct);
			}
			freelancerPayService.deleteUnPurchasedProductList(pickMeUpId);
			freelancerPayService.savePayProductList(payProductList);

			request.getSession().setAttribute("pickMeUpId", pickMeUp.getId());
			return new RedirectView("paymentConfirm?mode=EXTEND");
		}

		return new RedirectView("list");
	}

	@GetMapping("/extend")
	public String getExtendView(@RequestParam(value = "pickMeUpId", required = false) Long pickMeUpId, Model model) {
		PickMeUp pickMeUp = pickMeUpRepository.getOne(pickMeUpId);
		Map<Long, PickMeUpTicket> productTypeIdTicketMap = new LinkedHashMap<>();
		Map<Long, String> pickMeUpOptionTicketEndAtMap = new LinkedHashMap<>();

		FreelancerProductItemType pickMeUpDirectDealItem = freelancerProductItemTypeRepository.findByCode(FreelancerProductItemType.Code.DIRECT_DEAL);
		List<PickMeUpTicket> tickets = pickMeUpTicketRepository.findByPickMeUpIdAndEndAtAfterOrderByEndAtAsc(pickMeUpId, LocalDateTime.now());

		for (PickMeUpTicket ticket: tickets) {
			productTypeIdTicketMap.put(ticket.getFreelancerProductItemType().getId(), ticket);
			pickMeUpOptionTicketEndAtMap.put(ticket.getFreelancerProductItemType().getId(), TimeUtil.convertLocalDateToStr(ticket.getEndAt().toLocalDate()));
			List<Long> productOptionItemIds = tickets
					.stream().map(PickMeUpTicket::getFreelancerProductItemType).map(FreelancerProductItemType::getId).collect(Collectors.toList());
			pickMeUp.setOptionItemIds(productOptionItemIds);
			pickMeUp.setPickMeUpOptionTicketEndAtMap(pickMeUpOptionTicketEndAtMap);
		}

		ContactAvailableDayTime contactAvailableDayTime = contactAvailableDayTimeRepository.findByPickMeUpId(pickMeUpId);
		if (contactAvailableDayTime != null) {
			List<ContactAvailableDayTimeModel> dayTimeModels = new ArrayList<>();
			for (String key: contactAvailableDayTime.getDayTimes().keySet()) {
				if (StringUtils.isEmpty(key)) continue;
				ContactAvailableDayTimeModel dayTimeModel = new ContactAvailableDayTimeModel();
				dayTimeModel.setDays(Arrays.asList(key.split(",")));
				dayTimeModel.setTimes(contactAvailableDayTime.getDayTimes().get(key));
				dayTimeModels.add(dayTimeModel);
			}
			model.addAttribute("dayTimes", dayTimeModels);
		}

		List<FreelancerProductItemType> pickMeUpPayProducts
				= pickMeUpTicketRepository.findByPickMeUpId(pickMeUp.getId())
				.stream()
				.map(PickMeUpTicket::getFreelancerProductItemType)
				.collect(Collectors.toList());
		pickMeUp.setDirectDealAvailable(pickMeUpPayProducts.contains(pickMeUpDirectDealItem));

		model.addAttribute("remainFreeChargeProductCount",
				PaymentPolicy.PICK_ME_UP_FREE_CHARGE_COUNT - freelancerPayService.countFreeProductCountOfPickMeUp(getSessionUserId()));
		model.addAttribute("mode", "EXTEND");
		model.addAttribute("pickMeUp", pickMeUp);
		model.addAttribute("productList", freelancerPayService.getProductItemTypeList(FreelancerProductItemType.UsageType.PICK_ME_UP.name()));
		model.addAttribute("productTypeIdTicketMap", productTypeIdTicketMap);

		return "freelancer/pickMeUp/option";
	}
	
	
	@GetMapping("/modify")
	public String modify(@RequestParam(value = "pickMeUpId", required = false) final Long pickMeUpId,
						 Model model) throws Exception {
		PickMeUp pickMeUp = pickMeUpRepository.getOne(pickMeUpId);
		model.addAttribute("categories", categoryRepository.findByParentCategoryIsNullOrderBySeqAsc());
		if (pickMeUp.getCategory1st() != null) {
			model.addAttribute("subCategories", categoryRepository.findByParentCategoryId(pickMeUp.getCategory1st().getId()));
		} else {
			User user = userRepository.getOne(getSessionUserId());
			if (!user.getCategories().isEmpty()) {
				Category category = user.getCategories().iterator().next();
				pickMeUp.setCategory1st(category.getParentCategory());
				pickMeUp.setCategory2nd(category);
				pickMeUp = pickMeUpRepository.save(pickMeUp);
				model.addAttribute("subCategories", categoryRepository.findByParentCategoryId(pickMeUp.getCategory1st().getId()));
			}
		}

		FreelancerProductItemType pickMeUpDirectDealItem = freelancerProductItemTypeRepository.findByCode(FreelancerProductItemType.Code.DIRECT_DEAL);
		List<FreelancerProductItemType> pickMeUpPayProducts
				= pickMeUpTicketRepository.findAll(PickMeUpTicketSpecifications.filterForActiveTicket(pickMeUp.getId()))
				.stream()
				.map(PickMeUpTicket::getFreelancerProductItemType)
				.collect(Collectors.toList());
		pickMeUp.setDirectDealAvailable(pickMeUpPayProducts.contains(pickMeUpDirectDealItem));

		model.addAttribute("pickMeUp", pickMeUp);
		model.addAttribute("productList", freelancerPayService.getProductItemTypeList(FreelancerProductItemType.UsageType.PICK_ME_UP.name()));
		if (!PickMeUp.ContentType.BLOG.equals(pickMeUp.getContentType()) && StringUtils.isNotEmpty(pickMeUp.getMainImageUrl())) {
			if (!pickMeUp.getMainImageUrl().startsWith("data:")) {
				java.net.URL url = new java.net.URL(pickMeUp.getMainImageUrl());
				InputStream is = url.openStream();
				byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(is);

				model.addAttribute("mainImageBase64", Base64.encodeBase64String(bytes));
			} else {
				model.addAttribute("mainImageBase64", pickMeUp.getMainImageUrl());
			}
		}

		ContactAvailableDayTime contactAvailableDayTime = contactAvailableDayTimeRepository.findByPickMeUpId(pickMeUp.getId());
		if (contactAvailableDayTime != null) {
			List<ContactAvailableDayTimeModel> dayTimeModels = new ArrayList<>();
			for (String key: contactAvailableDayTime.getDayTimes().keySet()) {
				if (StringUtils.isEmpty(key)) continue;
				ContactAvailableDayTimeModel dayTimeModel = new ContactAvailableDayTimeModel();
				dayTimeModel.setDays(Arrays.asList(key.split(",")));
				dayTimeModel.setTimes(contactAvailableDayTime.getDayTimes().get(key));
				dayTimeModels.add(dayTimeModel);
			}
			model.addAttribute("dayTimes", dayTimeModels);
		}

		return "freelancer/pickMeUp/modify";
	}
}
