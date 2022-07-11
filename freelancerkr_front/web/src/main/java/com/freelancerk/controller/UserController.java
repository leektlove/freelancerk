package com.freelancerk.controller;

import com.freelancerk.domain.*;
import com.freelancerk.domain.PickMeUpComment.UserRole;
import com.freelancerk.domain.repository.*;
import com.freelancerk.gateway.EmailService;
import com.freelancerk.io.CommonListResponse;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.io.ResponseCode;
import com.freelancerk.model.SelectedKeywordModel;
import com.freelancerk.security.UsernamePasswordAuthTypeAuthenticationToken;
import com.freelancerk.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Api(tags = "유저", description = "유저 관련 정보 조회 등")
@Controller
public class UserController extends RootController {

    @Value("${server.url}") String serverUrl;
    private SmsService smsService;
    private UserService userService;
    private PointService pointService;
    private EmailService emailService;
    private TemplateEngine templateEngine;
    private UserRepository userRepository;
    private MessageService messageService;
    private PasswordEncoder passwordEncoder;
    private StorageService storageService;
    private CategoryService categoryService;
    private ClientPointLogRepository pointLogRepository;
    private DailyAccessLogRepository dailyAccessLogRepository;
    private ResettingPasswordService resettingPasswordService;
    private CertificationCodeRepository certificationCodeRepository;
    private ResettingPasswordRepository resettingPasswordRepository;

    @Autowired
    public UserController(SmsService smsService, UserService userService, PointService pointService, UserRepository userRepository,
                          ClientPointLogRepository pointLogRepository,
                          DailyAccessLogRepository dailyAccessLogRepository,
                          PasswordEncoder passwordEncoder,
                          CategoryService categoryService,
                          EmailService emailService, MessageService messageService, TemplateEngine templateEngine,
                          ResettingPasswordService resettingPasswordService,
                          CertificationCodeRepository certificationCodeRepository,
                          ResettingPasswordRepository resettingPasswordRepository,
                          StorageService storageService) {
        this.smsService = smsService;
        this.userService = userService;
        this.pointService = pointService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.templateEngine = templateEngine;
        this.messageService = messageService;
        this.passwordEncoder = passwordEncoder;
        this.categoryService = categoryService;
        this.pointLogRepository = pointLogRepository;
        this.dailyAccessLogRepository = dailyAccessLogRepository;
        this.resettingPasswordService = resettingPasswordService;
        this.certificationCodeRepository = certificationCodeRepository;
        this.resettingPasswordRepository = resettingPasswordRepository;
        this.storageService = storageService;
    }

    
    @ApiOperation("회원 가입")
    @RequestMapping(method = RequestMethod.POST, value = "/users")
    public String signUp(@RequestParam(value = "email", required = false) String email, @RequestParam(value = "password", required = false) String password, @RequestParam("role") User.Role role,
                         @RequestParam(value = "thirdPartyUserId", required = false) String thirdPartyUserId, @RequestParam(value = "thirdPartyAccessToken", required = false) String thirdPartyAccessToken,
                         @RequestParam("name") String name, @RequestParam("cellphone") String cellphone,
                         @RequestParam("authType") User.AuthType authType,
                         @RequestParam(value = "expertiseSector[]", required = false) String[] expertiseSectors,
                         @RequestParam(value = "selectedKeywordJsonId[]", required = false) Long[] selectedKeywordJsonId,
                         @RequestParam(value = "selectedKeywordJsonCategoryName[]", required = false) String[] selectedKeywordJsonCategoryName,
                         @RequestParam(value = "selectedKeywordJsonKeyword[]", required = false) String[] selectedKeywordJsonKeyword,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {

        int count = 0;
        if (User.AuthType.EMAIL.equals(authType)) {
        	// AndFpUser = fplus 회원 분기하는 임시로직
            count = userRepository.countByUsernameAndAuthTypeAndFpUser(email, authType,"N");
        } else {
        	// AndFpUser = fplus 회원 분기하는 임시로직
            count = userRepository.countByUsernameAndAuthTypeAndFpUser(thirdPartyUserId, authType,"N");
        }
        if (count > 0) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
        
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        if (User.AuthType.EMAIL.equals(authType)) {
            user.setUsername(email);
            user.setPassword(passwordEncoder.encode(password));
        } else {
            user.setUsername(thirdPartyUserId);
            if (StringUtils.isEmpty(name)) {
                user.setName(authType.name() + thirdPartyUserId);
            }
        }
        user.setUid(UUID.randomUUID().toString());
        user.setCellphone(cellphone.replace("-",""));
        user.setAuthType(authType);
        user.setRoles(role.name());
        user.setThirdPartyUserId(thirdPartyUserId);
        user.setReceiveEmail(true);
        user.setUseEscrow(true);
        user.setExposeEmail(false);
        user.setFpUser("N");
        user.setExposeType(User.ExposeType.NAME);
        user.setThirdPartyAccessToken(thirdPartyAccessToken);

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

        user.setCategories(new HashSet<>(categories));

        user = userRepository.save(user);

        UsernamePasswordAuthTypeAuthenticationToken authToken = new UsernamePasswordAuthTypeAuthenticationToken(user.getUsername(), password, authType.name(), role.name());
        authToken.setDetails(new WebAuthenticationDetails(request));
        user.setLoginRole(role);

        SecurityContextHolder.getContext().setAuthentication(user);

        if (user.getRoles().contains(User.Role.ROLE_FREELANCER.name())) {
            pointService.givePointsToFreelancerForEtcExpiredAt(user, 20000, "신규 회원 가입",
                    LocalDateTime.now().plusYears(1));
            Map<String, Object> messageVariables = new HashMap<>();
            messageVariables.put("freelancerName", user.getName());
            messageService.sendMessage(user, AligoKakaoMessageTemplate.Code.TA_3173, messageVariables);
        } else if (user.getRoles().contains(User.Role.ROLE_CLIENT.name())) {
            Map<String, Object> messageVariables = new HashMap<>();
            messageVariables.put("clientName", user.getName());
            messageService.sendMessage(user, AligoKakaoMessageTemplate.Code.TA_3172, messageVariables);
            pointService.givePointsToClientExpiredAt(user.getId(), 20000, "신규 회원 가입", LocalDateTime.now().plusYears(1));
        }

        try {
            if (StringUtils.isNotEmpty(email)) {
                String content = makeSignUpEmailContent(user.getName(), email, role);
                emailService.sendEmail(email, "프리랜서 코리아 회원가입이 완료되었습니다.", content);
            }
        } catch (Exception e) {
            log.error("<<< {} 가입안내 이메일 발송  중 에러 발생 ", email, e);
        }

        if (User.Role.ROLE_CLIENT.equals(role)) {
            return String.format("redirect:%s/client/profile/index", serverUrl);
        }

        return String.format("redirect:%s/freelancer/profile/index", serverUrl);
    }
    
	
	@ResponseBody
	@RequestMapping(value = "/sendMailTest")
	public String sendMailTest(Model model, HttpServletRequest request, HttpServletResponse response) {
		String email="junhyuk11225@gmail.com";
		String name = "박준혁";
		
	  try {
            if (StringUtils.isNotEmpty(email)) {
                String content = makeSignUpEmailContent(name, email, User.Role.ROLE_FREELANCER);
                emailService.sendEmail(email, "프리랜서 코리아 회원가입이 완료되었습니다.", content);
            }
        } catch (Exception e) {
            log.error("<<< {} 가입안내 이메일 발송  중 에러 발생 ", email, e);
            System.out.println(e);
        }
	  
		return null;
	}
	
	
    @ApiOperation("F+ 회원 가입")
    @RequestMapping(method = RequestMethod.POST, value = "/users_fp")
    public String signUpFp(@RequestParam(value = "email", required = false) String email, @RequestParam(value = "password", required = false) String password, @RequestParam("role") User.Role role,
                         @RequestParam(value = "thirdPartyUserId", required = false) String thirdPartyUserId, @RequestParam(value = "thirdPartyAccessToken", required = false) String thirdPartyAccessToken,
                         @RequestParam("name") String name, @RequestParam("cellphone") String cellphone,
                         @RequestParam("authType") User.AuthType authType,
                         @RequestParam(value = "expertiseSector[]", required = false) String[] expertiseSectors,
                         @RequestParam(value = "selectedKeywordJsonId[]", required = false) Long[] selectedKeywordJsonId,
                         @RequestParam(value = "selectedKeywordJsonCategoryName[]", required = false) String[] selectedKeywordJsonCategoryName,
                         @RequestParam(value = "selectedKeywordJsonKeyword[]", required = false) String[] selectedKeywordJsonKeyword,
                         @RequestParam(value = "profileImageUrl1", required = false) String profileImageUrl1, 
                         @RequestParam(value = "profileImageUrl2", required = false) String profileImageUrl2,
                         @RequestParam(value = "profileImageUrl3", required = false) String profileImageUrl3,
                         @RequestParam(value = "profileImageUrl4", required = false) String profileImageUrl4,
                         @RequestParam(value = "profileImageUrl5", required = false) String profileImageUrl5,
                         @RequestParam(value = "profileImageUrl6", required = false) String profileImageUrl6,
                         @RequestParam(value = "instagramLinkUrl", required = false) String instagramLinkUrl,
                         @RequestParam(value = "fpUserAgreeFile", required = false) MultipartFile fpUserAgreeFile,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {

        int count = 0;
        if (User.AuthType.EMAIL.equals(authType)) {
        	// AndFpUser = fplus 회원 분기하는 임시로직
            count = userRepository.countByUsernameAndAuthTypeAndFpUser(email, authType, "Y");
        } else {
        	// AndFpUser = fplus 회원 분기하는 임시로직
            count = userRepository.countByUsernameAndAuthTypeAndFpUser(thirdPartyUserId, authType, "Y");
        }
        if (count > 0) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        if (User.AuthType.EMAIL.equals(authType)) {
            user.setUsername(email);
            user.setPassword(passwordEncoder.encode(password));
        } else {
            user.setUsername(thirdPartyUserId);
            if (StringUtils.isEmpty(name)) {
                user.setName(authType.name() + thirdPartyUserId);
            }
        }
        user.setUid(UUID.randomUUID().toString());
        user.setCellphone(cellphone.replace("-",""));
        user.setAuthType(authType);
        user.setRoles(role.name());
        user.setThirdPartyUserId(thirdPartyUserId);
        user.setFpUser("Y");
        if (StringUtils.isNotEmpty(instagramLinkUrl)) {
        	user.setInstagramLinkUrl("http://www.instagram.com/"+instagramLinkUrl);
        }
        if (StringUtils.isNotEmpty(profileImageUrl1)) {
        	user.setProfileImageUrl1(profileImageUrl1);
		}
        if (StringUtils.isNotEmpty(profileImageUrl2)) {
        	user.setProfileImageUrl2(profileImageUrl2);
		}
        if (StringUtils.isNotEmpty(profileImageUrl3)) {
        	user.setProfileImageUrl3(profileImageUrl3);
		}
        if (StringUtils.isNotEmpty(profileImageUrl4)) {
        	user.setProfileImageUrl4(profileImageUrl4);
		}
        if (StringUtils.isNotEmpty(profileImageUrl5)) {
        	user.setProfileImageUrl5(profileImageUrl5);
		}
        if (StringUtils.isNotEmpty(profileImageUrl6)) {
        	user.setProfileImageUrl6(profileImageUrl6);
		}
        user.setReceiveEmail(true);
        user.setUseEscrow(true);
        user.setExposeEmail(false);
        user.setExposeType(User.ExposeType.NAME);
        user.setThirdPartyAccessToken(thirdPartyAccessToken);

    	String fileUrl = storageService.storeFile(fpUserAgreeFile);
		if(fileUrl.length()>89) {
			user.setAgreeFileUrl(fileUrl);
		}
    	

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
        
        user.setCategories(new HashSet<>(categories));
        	
        user = userRepository.save(user);

        UsernamePasswordAuthTypeAuthenticationToken authToken = new UsernamePasswordAuthTypeAuthenticationToken(user.getUsername(), password, authType.name(), role.name());
        authToken.setDetails(new WebAuthenticationDetails(request));
        user.setLoginRole(role);

        SecurityContextHolder.getContext().setAuthentication(user);

        if (user.getRoles().contains(User.Role.ROLE_FREELANCER.name())) {
            pointService.givePointsToFreelancerForEtcExpiredAt(user, 20000, "신규 회원 가입",
                    LocalDateTime.now().plusYears(1));
            Map<String, Object> messageVariables = new HashMap<>();
            messageVariables.put("freelancerName", user.getName());
            messageService.sendMessage(user, AligoKakaoMessageTemplate.Code.TA_3173, messageVariables);
        } else if (user.getRoles().contains(User.Role.ROLE_CLIENT.name())) {
            Map<String, Object> messageVariables = new HashMap<>();
            messageVariables.put("clientName", user.getName());
            messageService.sendMessage(user, AligoKakaoMessageTemplate.Code.TA_3172, messageVariables);
            pointService.givePointsToClientExpiredAt(user.getId(), 20000, "신규 회원 가입", LocalDateTime.now().plusYears(1));
        }

        try {
            if (StringUtils.isNotEmpty(email)) {
                String content = makeSignUpEmailContent(user.getName(), email, role);
                emailService.sendEmail(email, "프리랜서 코리아 회원가입이 완료되었습니다.", content);
            }
        } catch (Exception e) {
            log.error("<<< {} 가입안내 이메일 발송  중 에러 발생 ", email, e);
        }

        if (User.Role.ROLE_CLIENT.equals(role)) {
            return String.format("redirect:%s/fplus", serverUrl);
        }

        return String.format("redirect:%s/fplus", serverUrl);
    }

    @ResponseBody
    @ApiOperation("내 정보 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/users/me")
    public User getUserInfo() {

        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        User user = userRepository.getOne(userId);

        List<DailyAccessLog> logCountList = dailyAccessLogRepository.findByDateAndUserId(LocalDate.now(), userId);
        DailyAccessLog logCount = null;
        if (logCountList == null || logCountList.size() == 0) {
            logCount = new DailyAccessLog();
            logCount.setCount(0);
        } else {
            logCount = logCountList.get(0);
        }
        logCount.setUser(user);
        logCount.setDate(LocalDate.now());
        logCount.setCount(logCount.getCount() + 1);
        try {
            dailyAccessLogRepository.save(logCount);
        } catch (Exception e) {
            // 빠른 시간안에 연속으로 호출할 경우 SQLIntegrityConstraintViolationException 이 발생할 가능성이 있음
        }
        return user;
    }
    
    // findProject/list에서 사용
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/findUser")
    public User findUser(@RequestParam(value = "userId", required = true) Long userId) {
        User user = userRepository.getOne(userId);
        return user;
    }

    @ResponseBody
    @ApiOperation("포인트 사용 내역 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/users/me/point-logs")
    public CommonListResponse<List<ClientPointLog>> getPointLogs(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                 @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        Page<ClientPointLog> projectFavorites = pointLogRepository.findByUserId(userId, PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        return new CommonListResponse.Builder<List<ClientPointLog>>()
                .totalCount(projectFavorites.getTotalElements())
                .currentPage(pageNumber)
                .totalPages(projectFavorites.getTotalPages())
                .data(projectFavorites.getContent())
                .build();
    }

    @ResponseBody
    @ApiOperation("포인트 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/users/me/points")
    public CommonResponse<Long> getPoints() {
        return new CommonResponse.Builder<Long>().data(userService.getPoints(getSessionUserId(), ((User) SecurityContextHolder.getContext().getAuthentication()).isLoginAsClient()?User.Role.ROLE_CLIENT:User.Role.ROLE_FREELANCER)).build();
    }

    @ApiOperation("로그인")
    @RequestMapping(method = RequestMethod.POST, value = "/users/authentication")
    public User login(@RequestParam(value = "email", required = false) String email, @RequestParam(value = "password", required = false) String password,
                      @RequestParam(value = "thirdPartyUserId", required = false) String thirdPartyUserId, @RequestParam(value = "thirdPartyAccessToken", required = false) String thirdPartyAccessToken,
                      @RequestParam(value = "role", required = false) User.Role role) {
        // todo 실제 로그인 로직은 StatelessLoginFilter.java 에 존재
        return new User();
    }

    @ResponseBody
    @ApiOperation("가입 여부 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/users/check")
    public CommonResponse checkUser(@RequestParam(value = "email", required = false) String email,
                                    @RequestParam(value = "nickname", required = false) String nickname,
                                    @RequestParam(value = "fpUser", required = false) String fpUser,
                                    HttpServletResponse response) {
        int userCount = 0;
        if (StringUtils.isNotEmpty(email) && isLoggedIn()) {
            userCount = userRepository.countByEmailAndIdNotAndLeaveAtIsNullAndFpUser(email, getSessionUserId(),fpUser);
        } else if (StringUtils.isNotEmpty(email) && !isLoggedIn()) {
            userCount = userRepository.countByEmailAndLeaveAtIsNullAndFpUser(email,fpUser);
        } else if (StringUtils.isNotEmpty(nickname)) {
            userCount = userRepository.countByNicknameAndIdNotAndLeaveAtIsNull(nickname, getSessionUserId());
        }
        if (userCount > 0) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }

        return CommonResponse.ok();
    }

    @ResponseBody
    @ApiOperation("가입 여부 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/users/check/thirdparty")
    public CommonResponse checkUser(@RequestParam(value = "username", required = false) String username,
                                    @RequestParam(value = "authType", required = false) User.AuthType authType,
                                    HttpServletResponse response) {
        int count = userRepository.countByUsernameAndAuthType(username, authType);

        if (count > 0) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        return CommonResponse.ok();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/users/email-for-resetting-password")
    public CommonResponse requestEmailForResettingPassword(@RequestParam("email") String email) {
        User user = userRepository.findTop1ByEmailAndLeaveAtIsNullAndAuthType(email, User.AuthType.EMAIL);
        if (user == null) {
            return new CommonResponse.Builder<String>().responseCode(ResponseCode.FAIL).message("회원가입 기록이 없거나 탈퇴한 이메일 주소입니다. 다시 확인해보세요.").build();
        }
        resettingPasswordService.resetPassword(user, email);
        messageService.sendMessage(user, AligoKakaoMessageTemplate.Code.TA_3197, null);
        return CommonResponse.ok();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/users/password/modifications")
    public CommonResponse modifyUserPassword(@RequestParam("token") String token, @RequestParam("password") String password) {
        ResettingPassword resettingPassword = resettingPasswordRepository.findByTokenAndInvalidFalseAndUsedFalseAndExpiredAtAfter(token, LocalDateTime.now());
        if (resettingPassword == null) {
            return new CommonResponse.Builder<String>().responseCode(ResponseCode.FAIL).build();
        }
        User user = resettingPassword.getUser();
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setLegacyUser(false);
        userRepository.save(user);

        resettingPassword.setUsed(true);
        resettingPassword.setUsedAt(LocalDateTime.now());
        resettingPasswordRepository.save(resettingPassword);

        return CommonResponse.ok();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/users")
    @Transactional
    public CommonResponse leaveUser(@RequestParam("leaveReason") String leaveReason) {
        User user = userRepository.getOne(getSessionUserId());
        user.setLeaveAt(LocalDateTime.now());
        user.setLeaveText(leaveReason);
        user.setLeaved(true);
        user.setUsername(null);
        user.setEmail(null);
        user.setCellphone(null);
        userRepository.save(user);

        userService.deleteContents(getSessionUserId());

        return CommonResponse.ok();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/receive-email/off")
    public String turnOffReceiveEmail(@RequestParam("email") String email) {
        List<User> users = userRepository.findByEmailAndLeaveAtIsNull(email);

        for (User user: users) {
            user.setReceiveEmail(false);
            userRepository.save(user);
        }

        return "common/receive-email-off";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/users/cellphone/codes")
    public CommonResponse requestCellphoneCertificationCode(@RequestParam("cellphone") String cellphone) {
        String originalCellphone = cellphone;
        if (cellphone.startsWith("0")) {
            cellphone = String.format("%d%s", 82, cellphone.substring(1));
        }
        CertificationCode certificationCode = new CertificationCode();
        certificationCode.setCellphone(cellphone);
        certificationCode.setCode(RandomStringUtils.randomNumeric(4));
        certificationCode.setExpiredAt(LocalDateTime.now().plusMinutes(3));
        certificationCodeRepository.save(certificationCode);

        if (cellphone.startsWith("82")) {
            Map<String, Object> messageVariables = new HashMap<>();
            messageVariables.put("code", certificationCode.getCode());
            messageService.sendMessage(userRepository.getOne(getSessionUserId()), originalCellphone, AligoKakaoMessageTemplate.Code.TA_9935, messageVariables);
        } else {
            smsService.sendMessageAndReturnId(String.format("[프리랜서코리아] 요청하신 인증코드는 %s 입니다", certificationCode.getCode()), cellphone);
        }

        return CommonResponse.ok();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/users/cellphone/certifications")
    public CommonResponse certify(@RequestParam("cellphone") String cellphone,
                                  @RequestParam("code") String code) {
        if (cellphone.startsWith("0")) {
            cellphone = String.format("%d%s", 82, cellphone.substring(1));
        }
        CertificationCode certificationCode = certificationCodeRepository.findTop1ByCellphoneAndCodeAndExpiredAtAfter(cellphone, code, LocalDateTime.now());
        if (certificationCode == null) {
            return new CommonResponse.Builder<Void>().responseCode(ResponseCode.FAIL).build();
        }
        certificationCode.setCertified(true);
        certificationCodeRepository.save(certificationCode);

        return CommonResponse.ok();
    }

    private String makeSignUpEmailContent(String userName, String userEmail, User.Role role) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("username", userName);
        ctx.setVariable("userEmail", userEmail);

        return this.templateEngine.process(User.Role.ROLE_CLIENT.equals(role)?"email/signup-client":"email/signup-freelancer", ctx);
    }
}
