package com.freelancerk.controller.view;

import com.freelancerk.TimeUtil;
import com.freelancerk.controller.RootController;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.domain.specification.PickMeUpSpecifications;
import com.freelancerk.domain.specification.ProjectBidSpecifications;
import com.freelancerk.helper.LocalDateDeserializer;
import com.freelancerk.helper.LocalDateSerializer;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.model.SelectedKeywordModel;
import com.freelancerk.model.SortBy;
import com.freelancerk.service.CategoryService;
import com.freelancerk.service.PickMeUpService;
import com.freelancerk.service.StorageService;
import com.freelancerk.service.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/freelancer/profile")
public class FreelancerProfileViewController extends RootController {

	private UserService userService;
	private UserRepository userRepository;
    private StorageService storageService;
    private CategoryService categoryService;
	private PickMeUpService pickMeUpService;
    private MessageRepository messageRepository;
	private BankTypeRepository bankTypeRepository;
    private PickMeUpRepository pickMeUpRepository;
    private ProjectBidRepository projectBidRepository;
	private PaymentToUserRepository paymentToUserRepository;
	private PickMeUpTicketRepository pickMeUpTicketRepository;
    private FreelancerPointLogRepository freelancerPointLogRepository;
    private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;

	public FreelancerProfileViewController(UserRepository userRepository, UserService userService, StorageService storageService,
										   BankTypeRepository bankTypeRepository, CategoryService categoryService,
										   PickMeUpService pickMeUpService,
										   MessageRepository messageRepository,
										   PickMeUpTicketRepository pickMeUpTicketRepository,
										   PickMeUpRepository pickMeUpRepository, ProjectBidRepository projectBidRepository,
										   PaymentToUserRepository paymentToUserRepository, FreelancerPointLogRepository freelancerPointLogRepository,
										   FreelancerProductItemTypeRepository freelancerProductItemTypeRepository) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.storageService = storageService;
		this.categoryService = categoryService;
		this.pickMeUpService = pickMeUpService;
		this.messageRepository = messageRepository;
		this.bankTypeRepository = bankTypeRepository;
		this.pickMeUpRepository = pickMeUpRepository;
		this.projectBidRepository = projectBidRepository;
		this.paymentToUserRepository = paymentToUserRepository;
		this.pickMeUpTicketRepository = pickMeUpTicketRepository;
		this.freelancerPointLogRepository = freelancerPointLogRepository;
		this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
	}

	@Transactional
	@ModelAttribute("allSkills")
	public String populateSkills() {

		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.excludeFieldsWithoutExposeAnnotation()
				.registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
				.create();

		if (isLoggedIn()) {
			String skills = gson.toJson(userService.getCurrentUser().getUserSkillList());
			return skills;
		} else {
			return null;
		}
	}

	@Transactional
	@ModelAttribute("allCerts")
	public String populateCerts() {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.excludeFieldsWithoutExposeAnnotation()
				.registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
				.create();

		if (isLoggedIn()) {
			String certs = gson.toJson(userService.getCurrentUser().getUserCertificationList());

			return certs;
		} else {
			return null;
		}
	}

	@Transactional
	@ModelAttribute("allCareers")
	public String populateCareer() {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.excludeFieldsWithoutExposeAnnotation()
				.registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
				.create();

		if (isLoggedIn()) {
			String careers = gson.toJson(userService.getCurrentUser().getUserCareerList());
			return careers;
		} else {
			return null;
		}
	}

	@GetMapping("/index")
	public String showProfile(Model model) {

		Page<PickMeUp> page = pickMeUpRepository.findAll(
				PickMeUpSpecifications.filter(null, null,
						getSessionUserId(), null, null, null, null),
				PageRequest.of(0, 100));

		for (PickMeUp pickMeUp : page) {
			pickMeUpService.setValidTicketItem(pickMeUp);
		}

		long totalIncomeAmount = paymentToUserRepository.findByUserIdAndStatus(getSessionUserId(), PaymentToUser.Status.PAYED).stream().map(PaymentToUser::getAmount).mapToLong(Long::longValue).sum();
		Page<Message> messagePage = messageRepository.findByUserId(getSessionUserId(), PageRequest.of(0, 5, new Sort(Sort.Direction.DESC, "id")));
		List<Message> messages = messagePage.getContent();

		model.addAttribute("totalIncomeAmount", totalIncomeAmount);
		model.addAttribute("portfolios", page.getContent());
		model.addAttribute("portfolioIds", StringUtils.join(page.getContent().stream().map(PickMeUp::getId).collect(Collectors.toList()), ","));
		model.addAttribute("totalPortfolioCount", page.getTotalElements());
		model.addAttribute("totalApplyCount", projectBidRepository.count(ProjectBidSpecifications.filter(getSessionUserId(), null, null, null, null, null, SortBy.CREATED_AT)));
		model.addAttribute("totalPickedBidCount", projectBidRepository.count(ProjectBidSpecifications.filter(getSessionUserId(), null, null, ProjectBid.BidStatus.PICKED, null, null, SortBy.CREATED_AT)));
		model.addAttribute("totalCompletedCount",
				projectBidRepository.count(ProjectBidSpecifications.filter(getSessionUserId(), null, null, ProjectBid.BidStatus.PICKED, Project.Status.COMPLETED, null, SortBy.CREATED_AT)));
		model.addAttribute("messagePage", messagePage);
		model.addAttribute("messages", messages);
		if (messages != null && messages.size() > 0) {
			model.addAttribute("topMessageCreatedAt", TimeUtil.convertLocalDateTimeToStr(messages.get(0).getCreatedAt()));
		}

		User user = userRepository.getOne(getSessionUserId());
		List<FreelancerPointLog> pointLogs = freelancerPointLogRepository.findByUserIdAndAddedPointExpiredAtGreaterThan(user.getId(), LocalDateTime.now());
		user.setPoints(pointLogs.stream().mapToLong(FreelancerPointLog::getAddedPoint).sum() - pointLogs.stream().mapToLong(FreelancerPointLog::getUsedPoint).sum());
		model.addAttribute("user", user);

		return "freelancer/profile/view";
	}
	
	@GetMapping("/modify")
	public String modifyProfile(Model model, @RequestParam(value = "after-redirect", required = false) String afterRedirect) {

		model.addAttribute("afterRedirect", afterRedirect);
		model.addAttribute("user", userService.getCurrentUser());
		model.addAttribute("allBanks", bankTypeRepository.findAll());

		return "freelancer/profile/modify";
	}

	@ResponseBody
	@Transactional
	@PostMapping("/modify")
	public CommonResponse modifyProfile(
			@ModelAttribute final User user,
			@RequestParam(value = "profileImageUrl", required = false) String profileImageUrl,
			@RequestParam(value = "aboutMeFile", required = false) final  MultipartFile aboutMeFile,
			@RequestParam(value = "skillList", required = false) final String jsonSkillList,
			@RequestParam(value = "certList", required = false) final String jsonCertList,
			@RequestParam(value = "careerList", required = false) final String jsonCareerList,
			@RequestParam(value = "businessLicenseFile", required = false) MultipartFile businessLicenseFile,
			@RequestParam(value = "selectedKeywordJsonId[]", required = false) Long[] selectedKeywordJsonId,
			@RequestParam(value = "selectedKeywordJsonCategoryName[]", required = false) String[] selectedKeywordJsonCategoryName,
			@RequestParam(value = "selectedKeywordJsonKeyword[]", required = false) String[] selectedKeywordJsonKeyword,
			final Model model) {

		List<Category> categories = new ArrayList<>();
		if (selectedKeywordJsonId!= null && selectedKeywordJsonId.length > 0) {
			List<SelectedKeywordModel> keywordModels = new ArrayList<>();
			for (int i = 0; i < selectedKeywordJsonId.length; i++) {
				SelectedKeywordModel selectedKeywordModel = new SelectedKeywordModel();
				selectedKeywordModel.setKeyword(selectedKeywordJsonKeyword[i]);
				selectedKeywordModel.setId(selectedKeywordJsonId[i]);
				selectedKeywordModel.setCategoryName(selectedKeywordJsonCategoryName[i]);
				keywordModels.add(selectedKeywordModel);
			}

			categories = categoryService.createCategoryByParentIdNameJsons(keywordModels);
		}

    	User savedUser = userRepository.getOne(user.getId());
		if (aboutMeFile != null && !aboutMeFile.isEmpty()) {
			savedUser.setAboutMeFileUrl(storageService.storeFile(aboutMeFile));
			savedUser.setAboutMeFileName(aboutMeFile.getOriginalFilename());
		}
		if (businessLicenseFile != null && !businessLicenseFile.isEmpty()) {
			savedUser.setBusinessLicenseUrl(storageService.storeFile(businessLicenseFile));
			savedUser.setBusinessLicenseFileName(businessLicenseFile.getOriginalFilename());
		}
		if (StringUtils.isNotEmpty(profileImageUrl)) {
			savedUser.setProfileImageUrl(profileImageUrl);
		}
		if (StringUtils.isNotEmpty(user.getPassword())) {
			savedUser.setLegacyUser(false);
			savedUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		}

		if (!User.AuthType.EMAIL.equals(user.getAuthType())) {
			if (StringUtils.isNotEmpty(user.getEmail()) && user.getEmail().contains(",")) {
				savedUser.setEmail(user.getEmail().split(",")[0]);
			} else {
				savedUser.setEmail(user.getEmail());
			}
		} else {
			if (StringUtils.isNotEmpty(user.getEmail())) {
				if (user.getEmail().contains(",")) {
					savedUser.setEmail(user.getEmail().split(",")[0]);
				} else {
					savedUser.setEmail(user.getEmail());
				}
			}
		}

		savedUser.setExposeEmail(user.isExposeEmail());
		savedUser.setExposeCellphone(user.isExposeCellphone());
		savedUser.setExposeSns(user.isExposeSns());

		if (StringUtils.isNotEmpty(user.getCellphone())) {
			if (user.getCellphone().contains(",")) {
				savedUser.setCellphone(user.getCellphone().split(",")[0]);
			} else {
				savedUser.setCellphone(user.getCellphone());
			}
		}
		savedUser.setCellphoneCertified(user.isCellphoneCertified());

		savedUser.setFacebookLinkUrl(user.getFacebookLinkUrl());
		savedUser.setInstagramLinkUrl(user.getInstagramLinkUrl());
		savedUser.setTwitterLinkUrl(user.getTwitterLinkUrl());
		savedUser.setYoutubeLinkUrl(user.getYoutubeLinkUrl());
		savedUser.setLinkedInLinkUrl(user.getLinkedInLinkUrl());
		savedUser.setGithubLinkUrl(user.getGithubLinkUrl());
		savedUser.setBlogLinkUrl(user.getBlogLinkUrl());

		savedUser.setName(user.getName());
		savedUser.setNickname(user.getNickname());
		savedUser.setExposeType(user.getExposeType());
		savedUser.setBusinessType(user.getBusinessType());
		savedUser.setAboutMe(user.getAboutMe());
		savedUser.setCorporateName(user.getCorporateName());
		savedUser.setCorporateNumber(user.getCorporateNumber());
		savedUser.setTaxType(user.getTaxType());
		savedUser.setCareerYear(user.getCareerYear());
		savedUser.setBankAccountForReceivingPayment(user.getBankAccountForReceivingPayment());
		savedUser.setBankAccountName(user.getBankAccountName());
		savedUser.setBankForReceivingPayment(user.getBankForReceivingPayment());
		savedUser.setRegistrationNumber(user.getRegistrationNumber());
		savedUser.setRealName(user.getRealName());
		savedUser.setReceiveEmail(user.isReceiveEmail());
		user.getUserJobPreference().setUser(savedUser);
		savedUser.setUserJobPreference(user.getUserJobPreference());

		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
				.create();

		List<UserSkill> userSkills = gson.fromJson(jsonSkillList, new TypeToken<ArrayList<UserSkill>>(){}.getType());
		List<UserCertification> userCertifications = gson.fromJson(jsonCertList, new TypeToken<ArrayList<UserCertification>>(){}.getType());
		List<UserCareer> userCareers = gson.fromJson(jsonCareerList, new TypeToken<ArrayList<UserCareer>>(){}.getType());

		for (UserSkill userSkill: userSkills) {
			userSkill.setUser(savedUser);
		}
		for (UserCertification userCertification: userCertifications) {
			userCertification.setUser(savedUser);
		}
		for (UserCareer userCareer: userCareers) {
			userCareer.setUser(savedUser);
		}
		savedUser.getUserSkillList().clear();
		savedUser.getUserSkillList().addAll(userSkills);
		savedUser.getUserCertificationList().clear();
		savedUser.getUserCertificationList().addAll(userCertifications);
		savedUser.getUserCareerList().clear();
		savedUser.getUserCareerList().addAll(userCareers);
		savedUser.getCategories().clear();
		savedUser.getCategories().addAll(new HashSet<>(categories));

		savedUser = userRepository.save(savedUser);

		model.addAttribute("user", savedUser);
		model.addAttribute("allBanks", bankTypeRepository.findAll());

		return CommonResponse.ok();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public String getFreelancerProfileView(@PathVariable("id") Long userId, @RequestParam(value = "portfolioId", required = false) Long portfolioId,
										   @RequestParam(value = "showContact", required = false) Boolean showContact,
										   Model model) {

		Page<PickMeUp> page = pickMeUpRepository.findAll(
				PickMeUpSpecifications.filter(null, null,
						userId, null, null, null, null),
				PageRequest.of(0, 10));

		for (PickMeUp pickMeUp : page) {
			pickMeUpService.setValidTicketItem(pickMeUp);
		}

		if (portfolioId != null) {
			FreelancerProductItemType pickMeUpDirectDealItem = freelancerProductItemTypeRepository.findByCode(FreelancerProductItemType.Code.DIRECT_DEAL);
			List<FreelancerProductItemType> pickMeUpPayProducts
					= pickMeUpTicketRepository.findByPickMeUpIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(portfolioId, LocalDateTime.now(), LocalDateTime.now())
					.stream()
					.map(PickMeUpTicket::getFreelancerProductItemType)
					.collect(Collectors.toList());
			model.addAttribute("directDealAvailable", pickMeUpPayProducts.contains(pickMeUpDirectDealItem));
			model.addAttribute("directDealPickMeUpId", pickMeUpPayProducts.contains(pickMeUpDirectDealItem)?portfolioId:null);
		} else {
			model.addAttribute("directDealAvailable", false);
		}
		model.addAttribute("portfolios", page.getContent());
		model.addAttribute("portfolioIds", StringUtils.join(page.getContent().stream().map(PickMeUp::getId).collect(Collectors.toList()), ","));
		model.addAttribute("freelancer", userService.getById(userId));
		model.addAttribute("showContact", showContact == null?false:showContact);

		model.addAttribute("isLoggedIn", isLoggedIn());
		if (isLoggedIn()) {
			model.addAttribute("loginAsClient", ((User) SecurityContextHolder.getContext().getAuthentication()).isLoginAsClient());
		}

		return "freelancer/freelancerProfile";
	}
}
