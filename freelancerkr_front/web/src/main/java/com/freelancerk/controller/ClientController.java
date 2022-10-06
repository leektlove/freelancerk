package com.freelancerk.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.freelancerk.Constant;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.BankTypeRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.StorageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "클라이언트", description = "정보 수정 등")
@RestController
public class ClientController {

    private UserRepository userRepository;
    private StorageService storageService;
    private PasswordEncoder passwordEncoder;
    private BankTypeRepository bankTypeRepository;

    @Autowired
    public ClientController(UserRepository userRepository, StorageService storageService, PasswordEncoder passwordEncoder,
                            BankTypeRepository bankTypeRepository) {
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.passwordEncoder = passwordEncoder;
        this.bankTypeRepository = bankTypeRepository;
    }

    @ApiOperation("클라이언트 정보 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.TOKEN_KEY_NAME, value = "인증용 토큰 ", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "name", value = "이름", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "nickname", value = "닉네임", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "exposeType", value = "노출타입", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "cellphone", value = "휴대전화번호", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "password", value = "비밀번호", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "receiveEmail", value = "이메일 수신 여부", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "exposeEmail", value = "이메일 노출 여부", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "exposeCellphone", value = "휴대전화번호 노출 여부", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "exposeSns", value = "sns 노출 여부", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "useEscrow", value = "에스크로 사용 여부", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "businessType", value = "", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "corporateName", value = "회사명", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "corporateNumber", value = "개인사업자번호", required = false, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "corporateNumber", value = "", required = false, dataType = "string", paramType = "form"),
    })
    @RequestMapping(method = RequestMethod.POST, value = "/users/client/modifications")
    public CommonResponse updateProfile(@RequestParam(value = "profileImageUrl", required = false) String profileImageUrl,
                                        @RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "nickname", required = false) String nickname,
                                        @RequestParam(value = "exposeType", required = false) User.ExposeType exposeType,
                                        @RequestParam(value = "email", required = false) String email,
                                        @RequestParam(value = "cellphone", required = false) String cellphone,
                                        @RequestParam(value = "cellphoneCertified", required = false) Boolean cellphoneCertified,
                                        @RequestParam(value = "password", required = false) String password,
                                        @RequestParam(value = "receiveEmail", required = false) Boolean receiveEmail,
                                        @RequestParam(value = "exposeEmail", required = false) Boolean exposeEmail,
                                        @RequestParam(value = "exposeCellphone", required = false) Boolean exposeCellphone,
                                        @RequestParam(value = "exposeSns", required = false) Boolean exposeSns,
                                        @RequestParam(value = "useEscrow", required = false) Boolean useEscrow,
                                        @RequestParam(value = "businessType", required = false) User.BusinessType businessType,
                                        @RequestParam(value = "corporateName", required = false) String corporateName,
                                        @RequestParam(value = "corporateNumber", required = false) String corporateNumber,
                                        @RequestParam(value = "bankForReceivingPayment", required = false) String bankForReceivingPayment,
                                        @RequestParam(value = "bankAccountForReceivingPayment", required = false) String bankAccountForReceivingPayment,
                                        @RequestParam(value = "bankAccountName", required = false) String bankAccountName,
                                        @RequestParam(value = "clientInfo", required = false) String clientInfo,
                                        @RequestParam(value = "homepageUrl", required = false) String homepageUrl,
                                        @RequestParam(value = "businessLicenseFile", required = false) MultipartFile businessLicenseFile) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        User user = userRepository.getOne(userId);
        if (StringUtils.isNotEmpty(profileImageUrl)) {
            user.setProfileImageUrl(profileImageUrl);
        }
        if (StringUtils.isNotEmpty(name)) {
            user.setName(name);
        }
        if (!User.AuthType.EMAIL.equals(user.getAuthType()) && StringUtils.isNotEmpty(email)) {
            user.setEmail(email);
        }
        if (StringUtils.isNotEmpty(nickname)) {
            user.setNickname(nickname);
        }
        if (StringUtils.isNotEmpty(cellphone)) {
            user.setCellphone(cellphone.replace("-", ""));
        }
        if (cellphone != null) {
            user.setCellphoneCertified(cellphoneCertified);
        }
        if (exposeType != null) {
            user.setExposeType(exposeType);
        }
        if (receiveEmail != null) {
            user.setReceiveEmail(receiveEmail);
        }
        if (exposeEmail != null) {
            user.setExposeEmail(exposeEmail);
        } else {
            user.setExposeEmail(false);
        }
        if (exposeCellphone != null) {
            user.setExposeCellphone(exposeCellphone);
        } else {
            user.setExposeCellphone(false);
        }
        if (useEscrow != null) {
            user.setUseEscrow(useEscrow);
        }
        if (businessType != null) {
            user.setBusinessType(businessType);
        }
        if (StringUtils.isNotEmpty(corporateName)) {
            user.setCorporateName(corporateName);
        }
        if (StringUtils.isNotEmpty(corporateNumber)) {
            user.setCorporateNumber(corporateNumber);
        }
        if (User.BusinessType.INDIVIDUAL.equals(businessType)) {
            user.setRegistrationNumber(corporateNumber);
        }
        if (businessLicenseFile != null) {
            user.setBusinessLicenseFileName(businessLicenseFile.getOriginalFilename());
            user.setBusinessLicenseUrl(storageService.storeFile(businessLicenseFile));
        }
        if (exposeSns != null) {
            user.setExposeSns(exposeSns);
        } else {
            user.setExposeSns(false);
        }
        if (StringUtils.isNotEmpty(homepageUrl) && !homepageUrl.startsWith("http")) {
            user.setHomepageUrl("http://" + homepageUrl);
        } else {
            user.setHomepageUrl(homepageUrl);
        }
        if (StringUtils.isNotEmpty(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        if (StringUtils.isEmpty(bankAccountForReceivingPayment)) {
            user.setBankForReceivingPayment(null);
        } else {
            try {
                user.setBankForReceivingPayment(bankTypeRepository.findByName(bankForReceivingPayment));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        user.setBankAccountForReceivingPayment(bankAccountForReceivingPayment);
        user.setBankAccountName(bankAccountName);
        if (StringUtils.isNotEmpty(clientInfo)) {
            user.setMyClientInfo(clientInfo);
        }
        userRepository.save(user);

        return CommonResponse.ok();
    }
}
