package com.freelancerk.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.Apply;
import com.freelancerk.domain.Audition;
import com.freelancerk.domain.Category;
import com.freelancerk.domain.FreelancerProductItemType;
import com.freelancerk.domain.Keyword;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.PickMeUpTicketLog;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectCategory;
import com.freelancerk.domain.Purchase;
import com.freelancerk.domain.Reference;
import com.freelancerk.domain.ReferenceFile;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.ApplyRepository;
import com.freelancerk.domain.repository.AuditionRepository;
import com.freelancerk.domain.repository.EmailLogRepository;
import com.freelancerk.domain.repository.InquiryAnswerRepository;
import com.freelancerk.domain.repository.InquiryRepository;
import com.freelancerk.domain.repository.KeywordRepository;
import com.freelancerk.domain.repository.LalaFreelancerRepository;
import com.freelancerk.domain.repository.NoticeRepository;
import com.freelancerk.domain.repository.PickMeUpRepository;
import com.freelancerk.domain.repository.PickMeUpTicketLogRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.PurchaseRepository;
import com.freelancerk.domain.repository.ReferenceRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.gateway.EmailService;
import com.freelancerk.gateway.KakaoMessageService;
import com.freelancerk.service.CommonService;
import com.freelancerk.service.ContestSectorService;
import com.freelancerk.service.FrkEmailService;
import com.freelancerk.service.ImageService;
import com.freelancerk.service.KeywordOrSectorAlarmService;
import com.freelancerk.service.MessageService;
import com.freelancerk.service.SmsService;
import com.freelancerk.service.UserSectorService;
import com.freelancerk.service.UserService;
import com.freelancerk.vo.TotalPagesInfoVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class TestController {

    private SmsService smsService;
    private ImageService imageService;
    private EmailService emailService;
    private TemplateEngine templateEngine;
    private MessageService messageService;
    private UserRepository userRepository;
    private FrkEmailService frkEmailService;
    private KeywordRepository keywordRepository;
    private ApplyRepository applyRepository;
    private AuditionRepository auditionRepository;
    private ProjectRepository projectRepository;
    private UserSectorService userSectorService;
    private PurchaseRepository purchaseRepository;
    private PickMeUpRepository pickMeUpRepository;
    private EmailLogRepository emailLogRepository;
    private KakaoMessageService kakaoMessageService;
    private ContestSectorService contestSectorService;
    private KeywordOrSectorAlarmService keywordOrSectorAlarmService;
    private PickMeUpTicketLogRepository pickMeUpTicketLogRepository;
    private UserService userService;
	private CommonService commonService;
	private NoticeRepository noticeRepository;
	private InquiryRepository inquiryRepository;
	private ReferenceRepository referenceRepository;
	private InquiryAnswerRepository inquiryAnswerRepository;
	private LalaFreelancerRepository lalaFreelancerRepository;

	
	
    @Autowired
    public TestController(SmsService smsService, EmailService emailService,
                          ImageService imageService,
                          TemplateEngine templateEngine,
                          MessageService messageService, UserRepository userRepository,
                          FrkEmailService frkEmailService,
                          KeywordRepository keywordRepository,
                          ProjectRepository projectRepository,
                          ApplyRepository applyRepository,
                          AuditionRepository auditionRepository,
                          UserSectorService userSectorService,
                          PurchaseRepository purchaseRepository,
                          PickMeUpRepository pickMeUpRepository,
                          EmailLogRepository emailLogRepository,
                          KakaoMessageService kakaoMessageService,
                          ContestSectorService contestSectorService,
                          KeywordOrSectorAlarmService keywordOrSectorAlarmService,
                          PickMeUpTicketLogRepository pickMeUpTicketLogRepository,
                          UserService userService, CommonService commonService,
							NoticeRepository noticeRepository, InquiryRepository inquiryRepository,
							ReferenceRepository referenceRepository, InquiryAnswerRepository inquiryAnswerRepository,
							LalaFreelancerRepository lalaFreelancerRepository) {
        this.smsService = smsService;
        this.emailService = emailService;
        this.imageService = imageService;
        this.templateEngine = templateEngine;
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.frkEmailService = frkEmailService;
        this.keywordRepository = keywordRepository;
        this.projectRepository = projectRepository;
        this.applyRepository = applyRepository;
        this.auditionRepository  = auditionRepository;
        this.userSectorService = userSectorService;
        this.purchaseRepository = purchaseRepository;
        this.pickMeUpRepository = pickMeUpRepository;
        this.emailLogRepository = emailLogRepository;
        this.kakaoMessageService = kakaoMessageService;
        this.contestSectorService = contestSectorService;
        this.keywordOrSectorAlarmService = keywordOrSectorAlarmService;
        this.pickMeUpTicketLogRepository = pickMeUpTicketLogRepository;
        this.userService = userService;
		this.commonService = commonService;
		this.noticeRepository = noticeRepository;
		this.inquiryRepository = inquiryRepository;
		this.referenceRepository = referenceRepository;
		this.inquiryAnswerRepository = inquiryAnswerRepository;
		this.lalaFreelancerRepository = lalaFreelancerRepository;
    }

    @ResponseBody
    @GetMapping("/imagecompressed")
    public void imagecompressed() {
        for (PickMeUp pickMeUp: pickMeUpRepository.findByInvalidFalseOrderByCreatedAtDesc()) {
            if (StringUtils.isEmpty(pickMeUp.getCompressedImageUrl()) && StringUtils.isNotEmpty(pickMeUp.getMainImageUrl()) && pickMeUp.getMainImageUrl().startsWith("http")) {
                pickMeUp.setCompressedImageUrl(imageService.getCompressedImageUrl(pickMeUp.getMainImageUrl()));
                pickMeUpRepository.save(pickMeUp);
                log.info("<<< id: {}", pickMeUp.getId());
            }
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/test/sms")
    public void testSms() {

        String result = smsService.sendMessageAndReturnId("????????? ???????????????.2", "821057572427");
        log.info("<<< result: {}", result);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/test/ajax/portfolio-ajax-project")
    public String getTestProjectView() {

        return "test/portfolio-ajax-project";
    }

    @ResponseBody
//    @Transactional
    @RequestMapping(method = RequestMethod.GET, value = "/test/match-sector")
    public Set<Long> matchSector(@RequestParam("projectId") long projectId) {
        Project project = projectRepository.getOne(projectId);
//        Set<Category> categorySet = project.getProjectCategories().stream().map(ProjectCategory::getCategory).collect(Collectors.toSet());
//        log.info("<<< project categories: {}", categorySet.stream().map(Category::getName).collect(Collectors.toSet()));
//        List<Long> matchedUsers = userSectorService.getUsersByKeywordMatchingMoreThan2(categorySet);
//        for (Long matchedUserId: matchedUsers) {
//            log.info("<<< matched user id: {} /  {}", matchedUserId, userRepository.getOne(matchedUserId).getCategories().stream().map(Category::getName).collect(Collectors.toSet()));
//        }

//        keywordOrSectorAlarmService.sendMail(project);

        List<Long> targetUserIds = userSectorService.getUsersByKeywordMatchingMoreThan2(project.getProjectCategories().stream().map(ProjectCategory::getCategory).collect(Collectors.toSet()));
        Set<User> users = new HashSet<>(userRepository.findAllById(targetUserIds));
        log.info("<<< {} project matching keyword user size: {}", project.getId(), targetUserIds.size());
//        for (User user : users) {
//            log.info("sectors: {}", user.categories);
//            try {
//                String content = keywordOrSectorAlarmService.makeProjectPostedEmailContent(project, user.getEmail());
//                emailService.sendEmail(user.getEmail(), "[?????????????????????] ????????? ??????(3??? ??????) ???????????? ????????? ??????", content);
//
//                EmailLog emailLog = new EmailLog();
//                emailLog.setContent(content);
//                emailLog.setTitle("[?????????????????????] ????????? ??????(3??? ??????) ???????????? ????????? ??????");
//                emailLog.setReceiveEmails(user.getEmail());
//                emailLogRepository.save(emailLog);
//            } catch (Exception e) {
//                log.error("<<< {} ????????? ?????? ??????. ", user.getEmail(), e);
//            }
//        }

        return users.stream().map(User::getId).collect(Collectors.toSet());
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/test/email")
    public void sendTestEmail() {
//        User user = userRepository.findByUsername("jinhwan.stephen.choi@gmail.com");
//        frkEmailService.sendEmailResetLink(user.getExposeName(), "http://www.freelancerk.com", user.getEmail());

        Project project = projectRepository.getOne(1653L);
        User freelancer = userRepository.getOne(5640L);
        if (StringUtils.isNotEmpty(freelancer.getEmail()) && freelancer.isReceiveEmail()) {
            frkEmailService.sendProjectBidSuccessToFreelancer(freelancer, project.getPostingClient(), project, freelancer.getEmail());
        }

        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("clientName", project.getPostingClient().getExposeName());
        variableMap.put("projectName", project.getTitle());
        variableMap.put("freelancerName", freelancer.getExposeName());
        messageService.sendMessage(freelancer, AligoKakaoMessageTemplate.Code.TA_3180, variableMap);

        Map<String, Object> messageVariables = new HashMap<>();
        messageVariables.put("projectName", project.getTitle());
        messageVariables.put("freelancerName", freelancer.getExposeName());
        messageService.sendMessage(freelancer, AligoKakaoMessageTemplate.Code.TA_3205, messageVariables);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/test/kakao")
    public void sendTestKakao() {

        Project project = projectRepository.getOne(1446L);

        messageService.sendMessage(project.getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3174, null);
    }

    @ResponseBody
    @GetMapping("/test/project-keywords")
    public void projectKeywords() {
        keywordRepository.deleteAll();
        for (User user: userRepository.findByLeavedFalse()) {
            for (Category category: user.getCategories()) {
                Keyword keyword = keywordRepository.findTop1ByCategoryId(category.getId());
                if (keyword == null) {
                    keyword = new Keyword();
                    keyword.setUsageCount(0L);
                    keyword.setCategory(category);
                    keyword.setName(category.getName());
                }
                keyword.setUsageCount(keyword.getUsageCount() + 1);
                keywordRepository.save(keyword);
            }
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/test/contest-sectors")
    public void contestSectors() {
        Project project = projectRepository.getOne(1653L);
        Set<User> users = new HashSet<>(userSectorService.getUsersByTopKeyword(contestSectorService.convertContestSectorToUserCategory(project.getContestSectors().iterator().next())));
        log.info("<< user count: {}", users.size());
        for (User user: users) {
            if (StringUtils.isNotEmpty(user.getCellphone()) && user.isCellphoneCertified()) {
                Map<String, Object> messageVariablesFor = new HashMap<>();
                messageVariablesFor.put("freelancerName", user.getExposeName());
                messageVariablesFor.put("projectName", project.getTitle());
                messageService.sendMessage(user, AligoKakaoMessageTemplate.Code.TA_3176, messageVariablesFor);
            }
        }
//        for (User user: users) {
//            if (sendUserEmails.contains(user.getEmail())) continue;
//            try {
//                log.info("<<< id: {}, sectors: {}", user.getId(), user.getCategories().stream().map(Category::getParentCategory).map(Category::getName).collect(Collectors.toSet()));
//                if (StringUtils.isNotEmpty(user.getEmail()) && user.isReceiveEmail()) {
//                    String content = makeContestPostedEmailContent(project, user.getEmail());
//                    emailService.sendEmail(user.getEmail(), "[?????????????????????] ?????? ?????? ???????????? ????????? ??????", content);
//
//                    EmailLog emailLog = new EmailLog();
//                    emailLog.setContent(content);
//                    emailLog.setTitle("[?????????????????????] ?????? ?????? ???????????? ????????? ??????");
//                    emailLog.setReceiveEmails(user.getEmail());
//                    emailLogRepository.save(emailLog);
//                }
//            } catch (Exception e) {
//                log.info("<< ????????? ?????? ??????: {}", user.getEmail(), e);
//            }
//        }
//
//        User user = userRepository.findByUsername("jinhwan.stephen.choi@gmail.com");
//        String content = makeContestPostedEmailContent(project, user.getEmail());
//        emailService.sendEmail(user.getEmail(), "[?????????????????????] ?????? ?????? ???????????? ????????? ??????", content);
    }

    private String makeContestPostedEmailContent(Project project, String email) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("item", project);
        ctx.setVariable("userEmail", email);

        return this.templateEngine.process("email/sector-duplicated-contest-posted", ctx);
    }

    @GetMapping("/test/free-pickmeups")
    public void freePickMeUps() {
        List<PickMeUpTicketLog> pickMeUpTicketLogs = pickMeUpTicketLogRepository.findByPurchaseIsNull();
        for (PickMeUpTicketLog ticketLog: pickMeUpTicketLogs) {
            if (purchaseRepository.countByPickMeUpIdAndStatusAndCreatedAt(ticketLog.getPickMeUp().getId(), Purchase.Status.COMPLETED, ticketLog.getCreatedAt()) > 0) {
                Purchase purchase = purchaseRepository.findByPickMeUpIdAndStatusAndCreatedAt(ticketLog.getPickMeUp().getId(), Purchase.Status.COMPLETED, ticketLog.getCreatedAt());
                ticketLog.setPurchase(purchase);
                pickMeUpTicketLogRepository.save(ticketLog);

                continue;
            }

            log.info("<<< hit pickmeupid: {}", ticketLog.getPickMeUp().getId());

            Purchase purchase = new Purchase();
            purchase.setUser(ticketLog.getPickMeUp().getUser());
            purchase.setCreatedAt(ticketLog.getCreatedAt());
            purchase.setUpdatedAt(purchase.getCreatedAt());
            purchase.setType(Purchase.Type.PICK_ME_UP);
            purchase.setStatus(Purchase.Status.COMPLETED);
            purchase.setPickMeUp(ticketLog.getPickMeUp());
            purchase = purchaseRepository.save(purchase);

            ticketLog.setPurchase(purchase);
            pickMeUpTicketLogRepository.save(ticketLog);
        }

        List<Purchase> purchases = purchaseRepository.findByTypeAndSupplyAmountOfMoney(Purchase.Type.PICK_ME_UP, 0);
        for (Purchase item: purchases) {
            List<PickMeUpTicketLog> logs = pickMeUpTicketLogRepository.findByPurchaseId(item.getId());
            item.setChargedOptionsAmountOfMoney(
                    logs.stream().map(PickMeUpTicketLog::getFreelancerProductItemType).map(FreelancerProductItemType::getUnitPrice).mapToInt(Integer::intValue).sum()
            );
            purchaseRepository.save(item);
        }
    }

    @GetMapping("/test/kakao-sms")
    public void testKakaoSms() {
        kakaoMessageService.sendMessage("TA_3175", "01057572427", "???????????? ????????? ??????", "??????????????? ??????????????? ?????????????????????.");
    }
    
    @GetMapping("/users/gojoinjh")
    public String goJoinJh() {
		return "main/join";
    }
    @GetMapping("/intro")
    public String intro() {
		return "main/intro";
    }
    
    @GetMapping("/fplus")
    public String mainFP(Model model,
            HttpServletRequest request) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	model.addAttribute("loggedIn", authentication instanceof User);
          
    	List<Audition> list = auditionRepository.findByStatus("2000");
    	Audition topAudition = list.get(list.size()-1);
    	model.addAttribute("topAudition",topAudition);
		
		return "main/mainFP";
    }

	@RequestMapping(method = RequestMethod.GET, value = "/audition/detail")
	public String getAdminAuditionAddView(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "id", required = true) String id) {
		
		Audition audition = auditionRepository.findByAuditionId(id);
		model.addAttribute("audition", audition);
		
		List<Apply> list =  new ArrayList<>(); 
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if( authentication instanceof User) {
			Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
			User user = userRepository.getOne(userId);
			String uid = user.getUid();
			list = applyRepository.findByUidAndWork(uid, audition.getAuditionId());
		}
   
		boolean apply = false;
		if(list.size()>0) apply = true;
		model.addAttribute("audition", audition);
		model.addAttribute("apply",apply);
		
		
		return "audition/detail";
	}
    
    @RequestMapping(method = RequestMethod.POST, value = "/audition_apply")
    public String auditionApply(@RequestParam(value = "work", required = true) String work, 
                         @RequestParam(value = "uid", required = true) String uid,
                         Model model,
                         HttpServletRequest request, HttpServletResponse response
                      ) throws IOException {

    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	model.addAttribute("loggedIn", authentication instanceof User);

		for (int i = 0; i < 6; i++) {
	    	System.out.println(work+"?????? ??????  ?????????: "+uid);
		}
		User user = userRepository.findByUid(uid);
		
    	Apply apply = new Apply();
		List<Apply> list = new ArrayList<>();
		list = applyRepository.findByUidAndWork(uid, work);
		if(list.size()>0) {
			System.out.println("??????"+apply.toString());
		} else {
			apply = new Apply();
			apply.setUid(uid);
			apply.setWork(work);
			apply.setEmail(user.getEmail());
			apply.setName(user.getName());
			apply.setCreatedAt(LocalDateTime.now());
		}

		applyRepository.save(apply);

		return "main/mainFP";
    }
    
    @RequestMapping(value="/audition_list")
	@ResponseBody
	public Audition auditionList(
			HttpServletRequest request, HttpServletResponse response,
					@RequestBody Audition audition
		         ) throws IOException {
    	
    	Audition returnAudition = new Audition();
    	
		List<Audition> list = auditionRepository.findByStatus("2000");
		Collections.reverse(list);
		returnAudition.setList(list);
		returnAudition.setListCnt(list.size());
		
		return returnAudition;
	}
    
    @RequestMapping(value="/ajax_audition_apply")
	@ResponseBody
	public Apply ajaxAuditionApply(@RequestBody Apply apply,
		            Model model,
		            HttpServletRequest request, HttpServletResponse response
		         ) throws IOException {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("loggedIn", authentication instanceof User);
		
		
		String uid = apply.getUid();
		String work = apply.getWork();
		User user = userRepository.findByUid(uid);
		Apply returnApply =  new Apply();
		
		List<Apply> list = new ArrayList<>();
		list = applyRepository.findByUidAndWork(uid, work);
		if(list.size() > 0) {
			returnApply.setStatus("400");
			returnApply.setErrorMsg("?????? ??????????????? ????????????.");
			return returnApply;
		} else {
			apply = new Apply();
			apply.setUid(uid);
			apply.setEmail(user.getEmail());
			apply.setCellphone(user.getCellphone());
			apply.setName(user.getName());
			apply.setWork(work);
			apply.setPass1("N");
			apply.setPass2("N");
			apply.setPass3("N");
			apply.setCreatedAt(LocalDateTime.now());
		}
		
		applyRepository.save(apply);
		
		apply.setStatus("200");
		
		return apply;
	}
    
    @RequestMapping("/noticef/list")
	public String reference( HttpServletRequest request,
			@RequestParam(value = "currentPageNumber", defaultValue = "0") int currentPageNumber,
			@RequestParam(value = "referenceId", required = false) Long referenceId,
			@RequestParam(value = "searchWord", defaultValue = "", required = false) String searchWord, Model model) {

    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	model.addAttribute("loggedIn", authentication instanceof User);
    	
		if (referenceId != null) {
			commonService.updateReferenceHitsCount(referenceId); // hit ??? ??????
			Reference reference = commonService.getThisReference(referenceId);
			List<ReferenceFile> referenceFiles = commonService.getThisReferenceFiles(referenceId);
			model.addAttribute("reference", reference);
			model.addAttribute("referenceFiles", referenceFiles);
			return "reference/detail";
		}

		Page<Reference> referencePage = commonService.getReferencePages(searchWord, currentPageNumber);
		TotalPagesInfoVO totalPageInfo = commonService.getReferencePagesInfo(searchWord);

		model.addAttribute("referencePage", referencePage);
		model.addAttribute("totalPageInfo", totalPageInfo);
		model.addAttribute("totalNotice", commonService.getNoticePagesInfo("").getCountOfTotalElements());
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("currentPageNumber", currentPageNumber);

		return "reference/list";
	}
    
    @GetMapping("/oreotest")
    public String oreoTest() {
		return "main/intro";
    }
    
}
