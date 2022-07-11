package com.freelancerk.service.impl;

import com.freelancerk.FileUtil;
import com.freelancerk.TimeUtil;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.legacy.domain.*;
import com.freelancerk.legacy.repository.*;
import com.freelancerk.policy.PaymentPolicy;
import com.freelancerk.service.MigrationService;
import com.freelancerk.service.PointService;
import com.freelancerk.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class MigrationServiceImpl implements MigrationService {
	private ClientMemberRepository clientMemberRepository;
	private FreelancerMemberRepository freelancerMemberRepository;
	private ClientMemberPriceRepository clientMemberPriceRepository;
	private FrProjectApplyRepository frProjectApplyRepository;
	private UserRepository userRepository;
	private FrPopupRepository frPopupRepository;
	private PopupRepository popupRepository;
	private FrNoticeListRepository frNoticeListRepository;
	private NoticeRepository noticeRepository;
	private MemoFromAdminRepository memoFromAdminRepository;
	private BankTypeRepository bankTypeRepository;
	private FrProjectRepository frProjectRepository;
	private ProjectBidRepository projectBidRepository;
	private ProjectRepository projectRepository;
	private FrPickMeUpRepository frPickMeUpRepository;
	private PickMeUpRepository pickMeUpRepository;
	private FrCategoryRepository frCategoryRepository;
	private CategoryRepository categoryRepository;
	private FrClaimRepository frClaimRepository;
	private InquiryRepository inquiryRepository;
	private FrClaimAnswerRepository frClaimAnswerRepository;
	private InquiryAnswerRepository inquiryAnswerRepository;
	private AdminUserRepository adminUserRepository;
	private FrTagRepository frTagRepository;
	private KeywordRepository keywordRepository;
	private FrProjectAmountRepository frProjectAmountRepository;
	private StorageService storageService;
	private PickMeUpDetailImageRepository PickMeUpDetailImageRepository;
	private FrProjectGradeRepository frProjectGradeRepository;
	private ProjectRateRepository projectRateRepository;
	private PickMeUpTicketRepository pickMeUpTicketRepository;
	private PickMeUpTicketLogRepository pickMeUpTicketLogRepository;
	private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;
	private PickMeUpFreeChargeRepository pickMeUpFreeChargeRepository;
	private EscrowLogRepository escrowLogRepository;
	private EscrowRefundRequestRepository escrowRefundRequestRepository;
	private PaymentToUserRepository paymentToUserRepository;
	private ProjectProductItemTypeRepository projectProductItemTypeRepository;
	private ProjectItemTicketRepository projectItemTicketRepository;
	private ProjectItemTicketLogRepository projectItemTicketLogRepository;
	private FrProjectTagRepository frProjectTagRepository;
	private ProjectCategoryRepository projectCategoryRepository;
	private FreelancerMemberTagRepository freelancerMemberTagRepository;
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private FrProjectFileRepository frProjectFileRepository;
	private ProjectContractFileRepository projectContractFileRepository;
	private ProjectCompleteRepository projectCompleteRepository;
	private PointService pointService;

	public MigrationServiceImpl(ClientMemberRepository clientMemberRepository,
								ClientMemberPriceRepository clientMemberPriceRepository,
								FreelancerMemberRepository freelancerMemberRepository, UserRepository userRepository,
								FrProjectApplyRepository frProjectApplyRepository, FrPopupRepository frPopupRepository,
								PopupRepository popupRepository, FrNoticeListRepository frNoticeListRepository,
								NoticeRepository noticeRepository, MemoFromAdminRepository memoFromAdminRepository,
								BankTypeRepository bankTypeRepository, FrProjectRepository frProjectRepository,
								ProjectRepository projectRepository, FrPickMeUpRepository frPickMeUpRepository,
								PickMeUpRepository PickMeUpRepository, FrCategoryRepository frCategoryRepository,
								CategoryRepository categoryRepository, FrClaimRepository frClaimRepository,
								InquiryRepository inquiryRepository, FrClaimAnswerRepository frClaimAnswerRepository,
								InquiryAnswerRepository inquiryAnswerRepository, AdminUserRepository adminUserRepository,
								KeywordRepository keywordRepository, FrTagRepository frTagRepository,
								FrProjectAmountRepository frProjectAmountRepository, ProjectBidRepository projectBidRepository,
								PickMeUpTicketRepository pickMeUpTicketRepository,
								PickMeUpTicketLogRepository pickMeUpTicketLogRepository,
								StorageService storageService, PickMeUpDetailImageRepository PickMeUpDetailImageRepository,
								FrProjectGradeRepository frProjectGradeRepository, ProjectRateRepository projectRateRepository,
								PickMeUpFreeChargeRepository pickMeUpFreeChargeRepository, EscrowLogRepository escrowLogRepository,
								PaymentToUserRepository paymentToUserRepository, EscrowRefundRequestRepository escrowRefundRequestRepository,
								FreelancerProductItemTypeRepository freelancerProductItemTypeRepository,
								ProjectProductItemTypeRepository projectProductItemTypeRepository,
								ProjectItemTicketRepository projectItemTicketRepository,
								ProjectItemTicketLogRepository projectItemTicketLogRepository,
								ProjectContractFileRepository projectContractFileRepository,
								ProjectCategoryRepository projectCategoryRepository,
								FreelancerMemberTagRepository freelancerMemberTagRepository,
								FrProjectFileRepository frProjectFileRepository,
								FrProjectTagRepository frProjectTagRepository, PointService pointService,
								ProjectCompleteRepository projectCompleteRepository) {
		this.clientMemberRepository = clientMemberRepository;
		this.clientMemberPriceRepository = clientMemberPriceRepository;
		this.freelancerMemberRepository = freelancerMemberRepository;
		this.frProjectApplyRepository = frProjectApplyRepository;
		this.userRepository = userRepository;
		this.frPopupRepository = frPopupRepository;
		this.popupRepository = popupRepository;
		this.frNoticeListRepository = frNoticeListRepository;
		this.noticeRepository = noticeRepository;
		this.memoFromAdminRepository = memoFromAdminRepository;
		this.bankTypeRepository = bankTypeRepository;
		this.frProjectRepository = frProjectRepository;
		this.projectRepository = projectRepository;
		this.frPickMeUpRepository = frPickMeUpRepository;
		this.pickMeUpRepository = PickMeUpRepository;
		this.frCategoryRepository = frCategoryRepository;
		this.categoryRepository = categoryRepository;
		this.frClaimRepository = frClaimRepository;
		this.inquiryRepository = inquiryRepository;
		this.frClaimAnswerRepository = frClaimAnswerRepository;
		this.inquiryAnswerRepository = inquiryAnswerRepository;
		this.adminUserRepository = adminUserRepository;
		this.frTagRepository = frTagRepository;
		this.keywordRepository = keywordRepository;
		this.frProjectAmountRepository = frProjectAmountRepository;
		this.projectBidRepository = projectBidRepository;
		this.PickMeUpDetailImageRepository = PickMeUpDetailImageRepository;
		this.storageService = storageService;
		this.pickMeUpTicketRepository = pickMeUpTicketRepository;
		this.frProjectGradeRepository = frProjectGradeRepository;
		this.pickMeUpTicketLogRepository = pickMeUpTicketLogRepository;
		this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
		this.escrowRefundRequestRepository = escrowRefundRequestRepository;
		this.pickMeUpFreeChargeRepository = pickMeUpFreeChargeRepository;
		this.paymentToUserRepository = paymentToUserRepository;
		this.projectRateRepository = projectRateRepository;
		this.escrowLogRepository = escrowLogRepository;
		this.projectItemTicketLogRepository = projectItemTicketLogRepository;
		this.projectItemTicketRepository = projectItemTicketRepository;
		this.projectProductItemTypeRepository = projectProductItemTypeRepository;
		this.projectCategoryRepository = projectCategoryRepository;
		this.frProjectTagRepository = frProjectTagRepository;
		this.freelancerMemberTagRepository = freelancerMemberTagRepository;
		this.projectContractFileRepository = projectContractFileRepository;
		this.frProjectFileRepository = frProjectFileRepository;
		this.pointService = pointService;
		this.projectCompleteRepository = projectCompleteRepository;
	}

	@Override
	public void migrateNotice() {
		List<FrNoticeList> frNoticeLists = frNoticeListRepository.findAll();
		for (FrNoticeList frNoticeList : frNoticeLists) {
			try {
				Notice notice = new Notice();
				if (StringUtils.isEmpty(frNoticeList.getFlSubject())) continue;
				notice.setId(Long.valueOf(frNoticeList.getFlNo()));
				notice.setTitle(frNoticeList.getFlSubject());
				notice.setContent(frNoticeList.getFlContent());
				notice.setCreatedAt(frNoticeList.getFlRegDate());
				notice.setHits(frNoticeList.getFlHit());
				if (StringUtils.isNotEmpty(frNoticeList.getFlFile1())) {
					notice.setFileUrl(getUrlFrom(noticeFilePrefix + frNoticeList.getFlFile1(), frNoticeList.getFlFileName1()));
				}
				noticeRepository.save(notice);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void migratePopup() {
		List<FrPopup> frPopupList = frPopupRepository.findAll();
		for (FrPopup frPopup : frPopupList) {
			Popup popup = new Popup();
			popup.setLinkUrl(frPopup.getFpLink());
			popup.setMemo(frPopup.getFpMemo());
			popup.setHidden(!"Y".equalsIgnoreCase(frPopup.getFpDisplay()));
			popup.setStartAt(TimeUtil.convertStrToLocalDate(frPopup.getFpStartDay()));
			popup.setEndAt(TimeUtil.convertStrToLocalDate(frPopup.getFpEndDay()));
			popup.setMemo(frPopup.getFpMemo());

			Popup.LinkType linkType = null;
			if ("1".equalsIgnoreCase(frPopup.getFpLinkType())) {
				linkType = Popup.LinkType.CURRENT;
			} else if ("2".equalsIgnoreCase(frPopup.getFpLinkType())) {
				linkType = Popup.LinkType.NEW;
			}
			Popup.DeviceType deviceType = null;
			if ("1".equalsIgnoreCase(frPopup.getFpType())) {
				deviceType = Popup.DeviceType.PC;
			} else if ("2".equalsIgnoreCase(frPopup.getFpType())) {
				deviceType = Popup.DeviceType.MOBILE;
			}
			popup.setDeviceType(deviceType);
			popup.setLinkType(linkType);
			popupRepository.save(popup);
		}
	}

	@Override
	public void migrateFaq() {

	}

	@Override
	public void migrateClient() {
		List<ClientMember> clientMemberList = clientMemberRepository.findAll();
		for (ClientMember clientMember : clientMemberList) {
			try {
			User user = userRepository.findByUsername(clientMember.getCmId());
			if (user == null) {
				continue;
			} else {
				log.info("<<< {} 번", user.getId());
				user.setCreatedAt(clientMember.getCmRegDate());
				userRepository.save(user);
			}
//			user.setRoles(User.Role.ROLE_CLIENT.name());
//			user.setUsername(clientMember.getCmId());
//			user.setEmail(clientMember.getCmEmail());
//			user.setCellphone(clientMember.getCmHp().replaceAll("\\D", ""));
//			user.setCellphoneCertified("Y".equalsIgnoreCase(clientMember.getCmHpYn()));
//			user.setName(clientMember.getCmName());
//			user.setLegacyUser(true);
//			user.setPassword(passwordEncoder.encode(clientMember.getCmPassword()));
//			user.setPasswordFailCnt(clientMember.getCmPasswordFail());
//			user.setPasswordFailedAt(clientMember.getCmPasswordFailDate());
//			user.setEscrowPrice(clientMember.getCmEscrowPrice());
//			if (StringUtils.isNotEmpty(clientMember.getCmBusinessType())) {
//				if ("개인사업자".equalsIgnoreCase(clientMember.getCmBusinessType())) {
//					user.setBusinessType(User.BusinessType.INDIV_BUSINESS);
//				} else if ("개인".equalsIgnoreCase(clientMember.getCmBusinessType())) {
//					user.setBusinessType(User.BusinessType.INDIVIDUAL);
//				} else if ("법인사업자".equalsIgnoreCase(clientMember.getCmBusinessType())) {
//					user.setBusinessType(User.BusinessType.CORP_BUSINESS);
//				}
//			}
//			user.setCorporateName(clientMember.getCmCompany());
//			user.setCorporateNumber(clientMember.getCmLicenseNumber());
//			user.setUseEscrow("Y".equalsIgnoreCase(clientMember.getCmEscrowYn()));
//			user.setUid(UUID.randomUUID().toString());
//			if (StringUtils.isNotEmpty(clientMember.getCmLicenseFile())) {
//				user.setBusinessLicenseUrl(getUrlFrom(clientFilePrefix + clientMember.getCmLicenseFile(), clientMember.getCmLicenseFileName()));
//			}
//			user.setBusinessLicenseFileName(clientMember.getCmLicenseFileName());
//			user.setCreatedAt(clientMember.getCmRegDate());
//			if (!"sns".equalsIgnoreCase(clientMember.getCmRegType())) {
//				user.setAuthType(User.AuthType.EMAIL);
//			} else if (clientMember.getCmId().startsWith("fid")) {
//				user.setAuthType(User.AuthType.FACEBOOK);
//			} else if (clientMember.getCmId().startsWith("nid")) {
//				user.setAuthType(User.AuthType.NAVER);
//			}
//			user.setNickname(clientMember.getCmNick());
//			user.setReceiveEmail("Y".equalsIgnoreCase(clientMember.getCmEmailNotice()));
//			user.setExposeEmail("Y".equalsIgnoreCase(clientMember.getCmDisplay11()));
//			user.setExposeCellphone("Y".equalsIgnoreCase(clientMember.getCmDisplay12()));
//			user.setExposeSns("Y".equalsIgnoreCase(clientMember.getCmDisplay13()));
//			BankType bankType = bankTypeRepository.findByName(clientMember.getCmBankName());
//			if (bankType == null) {
//				bankType = new BankType();
//				bankType.setName(clientMember.getCmBankName());
//				bankTypeRepository.save(bankType);
//			}
//			user.setBankForReceivingPayment(bankType);
//			user.setBankAccountForReceivingPayment(clientMember.getCmBankNumber());
//			if ("1".equalsIgnoreCase(clientMember.getCmNameType())) {
//				user.setExposeType(User.ExposeType.NAME);
//			} else if ("2".equalsIgnoreCase(clientMember.getCmNameType())) {
//				user.setExposeType(User.ExposeType.NICKNAME);
//			}
//			user.setLeaveAt(TimeUtil.convertStrToLocalDateTime(clientMember.getCmLeaveDate()));
//			user.setLeaveType(clientMember.getCmLeaveType());
//			user.setLeaveText(clientMember.getCmLeaveText());
//			user.setBankAccountName(clientMember.getCmBankName());
//			if (StringUtils.isNotEmpty(clientMember.getCmProfileFile())) {
//				user.setProfileImageUrl(getUrlFrom(clientFilePrefix + clientMember.getCmProfileFile(), clientMember.getCmProfileFile()));
//			} else if (StringUtils.isNotEmpty(clientMember.getCmProfileSampleFile())) {
//				String[] profileImagePath =clientMember.getCmProfileSampleFile().split("/");
//				user.setProfileImageUrl(getUrlFrom(clientMember.getCmProfileSampleFile(), profileImagePath[profileImagePath.length - 1]));
//			}
//			user = userRepository.save(user);
//
//			MemoFromAdmin memoFromAdmin = new MemoFromAdmin();
//			memoFromAdmin.setContent(clientMember.getCmMemo());
//			memoFromAdmin.setCreatedAt(LocalDateTime.now());
//			memoFromAdmin.setUser(user);
//			memoFromAdminRepository.save(memoFromAdmin);
			} catch (Exception e) {
				e.printStackTrace();
				log.info("<<< {} 클라이언트 유저 마이그레이션 중 문제 발생", clientMember.getCmId(), e);
			}
		}
	}

	@Override
	public void migrateStrangeFreelancer() {
		List<BigInteger> dataList = userRepository.findStrangeFreelancer();
		for (BigInteger bigInteger: dataList) {
			try {
				long id = (bigInteger).longValue();
				User user = userRepository.getOne(id);
				String fmId = user.getUsername();
				if (User.AuthType.NAVER.equals(user.getAuthType())) {
					fmId = "nid_" + fmId;
				} else if (User.AuthType.FACEBOOK.equals(user.getAuthType())) {
					fmId = "fcb_" + fmId;
				} else if (User.AuthType.KAKAO.equals(user.getAuthType())) {
					fmId = "kko_" + fmId;
				} else {
					continue;
				}
				FreelancerMember freelancerMember = freelancerMemberRepository.findByFmId(fmId);
				if (freelancerMember == null) continue;
				user.setCreatedAt(freelancerMember.getFmRegDate());
				userRepository.save(user);
				log.info("<<< hit: {}", id);
			} catch (Exception e) {
				log.error("<<< {}번 유저 마이그레이션 중 에러 발생", bigInteger.longValue());
			}
		}
	}

	@Override
	public void migrateFreelancer() {

		List<FreelancerMember> freelancerMembers = freelancerMemberRepository.findAll();
		for (FreelancerMember freelancerMember : freelancerMembers) {
			try {
				User user = userRepository.findByUsername(freelancerMember.getFmId());
				if (user == null) {
//					user = new User();
				} else {
					user.setCreatedAt(freelancerMember.getFmRegDate());
					userRepository.save(user);
					log.info("<<< {} 번", user.getId());
//					List<FreelancerMemberTag> freelancerMemberTags = freelancerMemberTagRepository.findByFmId(freelancerMember.getFmId());
//					for (FreelancerMemberTag tag: freelancerMemberTags) {
//						if (StringUtils.isEmpty(tag.getFtTag())) continue;
//						Set<Category> categories = new HashSet<>();
//						for (String categoryStr: tag.getFtTag().split("\\|-\\|")) {
//							Category category = categoryRepository.findByParentCategoryOriginalCodeAndName(tag.getFcId2(), categoryStr);
//							if (category == null) continue;
//							categories.add(category);
//						}
//						user.setCategories(categories);
//						userRepository.save(user);
//					}
				}
//				if (StringUtils.isNotEmpty(user.getRoles()) && !user.getRoles().contains(User.Role.ROLE_FREELANCER.name())) {
//					user.setRoles(String.join(",", Arrays.asList(User.Role.ROLE_FREELANCER.name(), user.getRoles())));
//				} else {
//					user.setRoles(User.Role.ROLE_FREELANCER.name());
//				}
//				user.setLegacyUser(true);
//				user.setUsername(freelancerMember.getFmId());
//				user.setCellphone(freelancerMember.getFmHp().replaceAll("\\D", ""));
//				user.setCellphoneCertified("Y".equalsIgnoreCase(freelancerMember.getFmHpYn()));
//				user.setReceiveEmail("Y".equalsIgnoreCase(freelancerMember.getFmEmailNotice()));
//				user.setEmail(freelancerMember.getFmEmail());
//				user.setName(freelancerMember.getFmName());
//				user.setPassword(passwordEncoder.encode(freelancerMember.getFmPassword()));
//				user.setPasswordFailCnt(freelancerMember.getFmPasswordFail());
//				user.setPasswordFailedAt(freelancerMember.getFmPasswordFailDate());
//				if (StringUtils.isEmpty(user.getUid())) {
//					user.setUid(UUID.randomUUID().toString());
//				}
//				user.setCreatedAt(freelancerMember.getFmRegDate());
//				if (!"sns".equalsIgnoreCase(freelancerMember.getFmRegType())) {
//					user.setAuthType(User.AuthType.EMAIL);
//				} else if (freelancerMember.getFcId().startsWith("fid")) {
//					user.setAuthType(User.AuthType.FACEBOOK);
//				} else if (freelancerMember.getFcId().startsWith("nid")) {
//					user.setAuthType(User.AuthType.NAVER);
//				}
//				if (StringUtils.isNotEmpty(freelancerMember.getFmBusinessType())) {
//					if ("개인사업자".equalsIgnoreCase(freelancerMember.getFmBusinessType())) {
//						user.setBusinessType(User.BusinessType.INDIV_BUSINESS);
//					} else if ("개인".equalsIgnoreCase(freelancerMember.getFmBusinessType())) {
//						user.setBusinessType(User.BusinessType.INDIVIDUAL);
//					} else if ("법인사업자".equalsIgnoreCase(freelancerMember.getFmBusinessType())) {
//						user.setBusinessType(User.BusinessType.CORP_BUSINESS);
//					}
//				}
//				if ("N".equals(freelancerMember.getFmServicePrice())) {
//					user.setTaxType(TaxType.VAT);
//				} else if ("Y".equals(freelancerMember.getFmServicePrice())) {
//					user.setTaxType(TaxType.COLLECTION_IN_ADVANCE);
//				} else {
//					user.setTaxType(null);
//				}
//				user.setFacebookLinkUrl(freelancerMember.getFmSns2());
//				user.setAboutMe(freelancerMember.getFmIntroduce());
//				if (StringUtils.isEmpty(user.getAboutMeFileUrl()) && StringUtils.isNotEmpty(freelancerMember.getFmIntroduceFile())) {
//					user.setAboutMeFileUrl(getUrlFrom(freelancerFilePrefix + freelancerMember.getFmIntroduceFile(), freelancerMember.getFmIntroduceFileName()));
//				}
//				user.setAboutMeFileName(freelancerMember.getFmLicenseFileName());
//				user.setCorporateName(freelancerMember.getFmCompany());
//				user.setCorporateNumber(freelancerMember.getFmLicenseNumber());
//				user.setRealName(freelancerMember.getFmRealName());
//				user.setNickname(freelancerMember.getFmNick());
//				user.setExposeEmail("Y".equalsIgnoreCase(freelancerMember.getFmDisplay11()));
//				user.setExposeCellphone("Y".equalsIgnoreCase(freelancerMember.getFmDisplay12()));
//				user.setExposeSns("Y".equalsIgnoreCase(freelancerMember.getFmDisplay13()));
//				if (StringUtils.isNotEmpty(freelancerMember.getFmBankName())) {
//					BankType bankType = bankTypeRepository.findByName(freelancerMember.getFmBankName());
//					if (bankType == null) {
//						bankType = new BankType();
//						bankType.setName(freelancerMember.getFmBankName());
//						bankTypeRepository.save(bankType);
//					}
//					user.setBankForReceivingPayment(bankType);
//				}
//				user.setBankAccountName(freelancerMember.getFmAccountName());
//				user.setBankAccountForReceivingPayment(freelancerMember.getFmBankNumber());
//				if ("1".equalsIgnoreCase(freelancerMember.getFmNameType())) {
//					user.setExposeType(User.ExposeType.NAME);
//				} else if ("2".equalsIgnoreCase(freelancerMember.getFmNameType())) {
//					user.setExposeType(User.ExposeType.NICKNAME);
//				}
//				if (StringUtils.isEmpty(user.getBusinessLicenseUrl()) && StringUtils.isNotEmpty(freelancerMember.getFmLicenseFile())) {
//					user.setBusinessLicenseUrl(getUrlFrom(freelancerFilePrefix + freelancerMember.getFmLicenseFile(), freelancerMember.getFmLicenseFileName()));
//				}
//				user.setBusinessLicenseFileName(freelancerMember.getFmLicenseFileName());
//				user.setLeaveAt(TimeUtil.convertStrToLocalDateTime(freelancerMember.getFmLeaveDate()));
//				user.setLeaveType(freelancerMember.getFmLeaveType());
//				user.setLeaveText(freelancerMember.getFmLeaveText());
//				user.setRegistrationNumber(freelancerMember.getFmResidentRegistration());
//
//				if (StringUtils.isEmpty(user.getProfileImageUrl()) && StringUtils.isNotEmpty(freelancerMember.getFmProfileFile())) {
//					user.setProfileImageUrl(getUrlFrom(freelancerFilePrefix + freelancerMember.getFmProfileFile(), freelancerMember.getFmProfileFile()));
//				} else if (StringUtils.isEmpty(user.getProfileImageUrl()) &&  StringUtils.isNotEmpty(freelancerMember.getFmProfileSampleFile())) {
//					String[] profileImagePath = freelancerMember.getFmProfileSampleFile().split("/");
//					user.setProfileImageUrl(getUrlFrom(freelancerMember.getFmProfileSampleFile(), profileImagePath[profileImagePath.length - 1]));
//				}
//
////				List<FreelancerMemberTag> freelancerMemberTags = freelancerMemberTagRepository.findByFmId(freelancerMember.getFmId());
////				Set<Category> categories = new HashSet<>();
////				for (FreelancerMemberTag tag: freelancerMemberTags) {
////					if (StringUtils.isEmpty(tag.getFtTag())) continue;
////					for (String categoryStr: tag.getFtTag().split("\\|-\\|")) {
////						Category category = getCategoryFromTwoPhaseAndKeyword(tag.getFcId2(), categoryStr);
////						if (category == null) continue;
////						categories.add(category);
////					}
////				}
////				if (user.getCategories() != null) {
////					user.getCategories().clear();
////					user.getCategories().addAll(categories);
////				} else {
////					user.setCategories(categories);
////				}
//
//				if (StringUtils.isNotEmpty(freelancerMember.getFmText1())) {
//					String[] skillsArr = freelancerMember.getFmText1().split("\\|-\\|");
//					List<UserSkill> userSkills = new ArrayList<>();
//					for (String skills: skillsArr) {
//						UserSkill userSkill = new UserSkill();
//						userSkill.setUser(user);
//						userSkill.setSkillName(skills.split("\\|\\|")[0]);
//						userSkill.setSkillLevel(skills.split("\\|\\|")[1]);
//						userSkills.add(userSkill);
//					}
//					if (user.getUserSkillList() != null) {
//						user.getUserSkillList().clear();
//						user.getUserSkillList().addAll(userSkills);
//					} else {
//						user.setUserSkillList(userSkills);
//					}
//				}
//
//				if (StringUtils.isNotEmpty(freelancerMember.getFmText2())) {
//					String[] certificationsArr = freelancerMember.getFmText2().split("\\|-\\|");
//					List<UserCertification> userCertifications = new ArrayList<>();
//					for (String certification: certificationsArr) {
//						UserCertification userCertification = new UserCertification();
//						userCertification.setUser(user);
//						userCertification.setAuthOrganization(certification.split("\\|\\|")[2]);
//						userCertification.setAcqDate(TimeUtil.convertStrToLocalDate(certification.split("\\|\\|")[0]));
//						userCertification.setCertificationName(certification.split("\\|\\|")[1]);
//						userCertifications.add(userCertification);
//					}
//					if (user.getUserCertificationList() != null) {
//						user.getUserCertificationList().clear();
//						user.getUserCertificationList().addAll(userCertifications);
//					} else {
//						user.setUserCertificationList(userCertifications);
//					}
//				}
//
//				if (StringUtils.isNotEmpty(freelancerMember.getFmText3())) {
//					String[] careerArr = freelancerMember.getFmText3().split("\\|-\\|");
//					List<UserCareer> userCareers = new ArrayList<>();
//					for (String career: careerArr) {
//						UserCareer userCareer = new UserCareer();
//						userCareer.setUser(user);
//						userCareer.setStartDate(TimeUtil.convertStrToLocalDate(career.split("\\|\\|")[0]));
//						userCareer.setEndDate(TimeUtil.convertStrToLocalDate(career.split("\\|\\|")[1]));
//						userCareer.setProjectName(career.split("\\|\\|")[2]);
//						userCareer.setJobPosition(career.split("\\|\\|")[3]);
//						userCareer.setJobDescription(career.split("\\|\\|")[4]);
//						userCareers.add(userCareer);
//					}
//					if (user.getUserCareerList() != null) {
//						user.getUserCareerList().clear();
//						user.getUserCareerList().addAll(userCareers);
//					} else {
//						user.setUserCareerList(userCareers);
//					}
//				}
//				userRepository.save(user);
			} catch (Exception e) {
				e.printStackTrace();
				log.info("<<< {} 프리랜서 유저 마이그레이션 중 문제 발생", freelancerMember.getFmId(), e);
			}
		}
	}

	@Override
	public void migrateCategory() {
		List<FrCategory> frCategories = frCategoryRepository.findAll();
		for (FrCategory frCategory : frCategories) {
			Category category = new Category();
			category.setName(frCategory.getFcSubject());
			category.setCreatedAt(LocalDateTime.now());
			category.setSeq(frCategory.getFrOrder());
			category.setTags(StringUtils.join(Arrays.asList(frCategory.getFcTag().split("\\|-\\|")), ","));
			category.setHidden(!"Y".equalsIgnoreCase(frCategory.getFrDisplay()));
			category.setOriginalCode(frCategory.getFcId());
			category.setImageDetailUrl(frCategory.getFcImg());
			if (frCategory.getFcId().length() > 2) {
				category.setParentCategory(categoryRepository.findByOriginalCode(frCategory.getFcId().substring(0, 2)));
			}
			categoryRepository.save(category);
		}
	}

	@Override
	public void migrateTag() {
		List<FrTag> frTags = frTagRepository.findAll();
		for (FrTag frTag : frTags) {
			Keyword keyword = new Keyword();
			keyword.setName(frTag.getFtName());
			keyword.setSeq(frTag.getFtOrder());
			keyword.setUsageCount(Long.valueOf(frTag.getFtCnt()));
			keyword.setCreatedAt(LocalDateTime.now());
			keyword.setCategory(categoryRepository.findByOriginalCode(frTag.getFcId()));
			keywordRepository.save(keyword);
		}
	}

	@Override
	public void migrateClaim() {
		List<FrClaim> claims = frClaimRepository.findAll();
		for (FrClaim frClaim : claims) {
			Inquiry inquiry = new Inquiry();
			inquiry.setId(Long.valueOf(frClaim.getFcNo()));
			inquiry.setTitle(frClaim.getFcSubject());
			inquiry.setContent(frClaim.getFcContent());
			inquiry.setCreatedAt(frClaim.getFcRegDate());
			Inquiry.Status status = null;
			if (frClaim.getFcCategory() == 1) {
				status = Inquiry.Status.REGISTERED;
			} else if (frClaim.getFcCategory() == 2) {
				status = Inquiry.Status.IN_PROGRESS;
			} else if (frClaim.getFcCategory() == 3) {
				status = Inquiry.Status.COMPLETED;
			}
			inquiry.setStatus(status);
//			Inquiry.Type type = null;
//			if ("1".equalsIgnoreCase(frClaim.getFcType())) {
//				type = Inquiry.Type.CLIENT;
//			} else if ("2".equalsIgnoreCase(frClaim.getFcType())) {
//				type = Inquiry.Type.FREELANCER;
//			}
//			inquiry.setType(type);
			inquiry.setUser(userRepository.findByUsername(frClaim.getFmId()));

			inquiryRepository.save(inquiry);
		}
	}

	@Override
	public void migrateClaimAnswer() {
		AdminUser adminUser = adminUserRepository.findByUsername("admin");
		List<FrClaimAnswer> claimAnswers = frClaimAnswerRepository.findAll();
		for (FrClaimAnswer claimAnswer : claimAnswers) {
			InquiryAnswer inquiryAnswer = new InquiryAnswer();
			inquiryAnswer.setContent(claimAnswer.getFaContent());
			inquiryAnswer.setCreatedAt(claimAnswer.getFaRegDate());
			inquiryAnswer.setInquiry(inquiryRepository.getOne(Long.valueOf(claimAnswer.getFcNo())));
			inquiryAnswer.setAdminUser(adminUser);
			inquiryAnswerRepository.save(inquiryAnswer);
		}
	}

	@Override
	public void migrateProject() {
		ProjectProductItemType externalProjectItem = projectProductItemTypeRepository.findByCodeAndProjectType(ProjectProductItemType.Code.EXTERNAL, Project.Type.PROJECT);
		ProjectProductItemType internalProjectItem = projectProductItemTypeRepository.findByCodeAndProjectType(ProjectProductItemType.Code.INTERNAL, Project.Type.PROJECT);
		List<FrProject> frProjects = frProjectRepository.findAll();
//		for (FrProject frProject : frProjects) {
//			try {
//				Optional<Project> projectOptional = projectRepository.findById(Long.valueOf(frProject.getFpNo()));
//				Project project = null;
//				if (!projectOptional.isPresent()) {
//					project = new Project();
//				} else {
//					project = projectOptional.get();
//					continue;
//				}
//				project.setId(Long.valueOf(frProject.getFpNo()));
//				project.setTitle(frProject.getFpSubject());
//				project.setProjectType(Project.Type.PROJECT);
//				project.setPostingClient(userRepository.findByUsername(frProject.getCmId()));
//				if (StringUtils.isNotEmpty(frProject.getFmId())) {
//					User freelancerUser = userRepository.findByUsername(frProject.getFmId());
//					project.setContractedFreelancer(freelancerUser);
//					ProjectBid projectBid = projectBidRepository.findTop1ByParticipantIdAndProjectIdOrderByCreatedAtDesc(freelancerUser.getId(), project.getId());
//					if (projectBid == null) {
//						projectBid = new ProjectBid();
//					}
//					projectBid.setBidType(ProjectBid.BidType.PROJECT_BID);
//					projectBid.setParticipant(freelancerUser);
//					projectBid.setBidStatus(ProjectBid.BidStatus.PICKED);
//					projectBid.setPickedAmountOfMoney(frProject.getFpContractPrice());
//					projectBid.setSuccessBidAt(frProject.getFpWinDate());
//
//					projectBidRepository.save(projectBid);
//				}
//				project.setMemo(frProject.getFpMemo());
//				project.setCreatedAt(frProject.getFpRegDate());
//				project.setUpdatedAt(frProject.getFpModDate());
//				project.setCancelAt(frProject.getFpCanDate());
//
//				project.setCancelReason(frProject.getFpCanType());
//				project.setDescription(frProject.getFpIntroduce());
//				project.setPayMean(Project.PayMean.PER_UNIT);
//
//				if (StringUtils.isEmpty(project.getProjectDescriptionFileUrl()) && StringUtils.isNotEmpty(frProject.getFpIntroduceFile())) {
//					project.setProjectDescriptionFileUrl(getUrlFrom(projectFilePrefix + frProject.getFpIntroduceFile(), frProject.getFpIntroduceFileName()));
//					project.setProjectDescriptionFileName(frProject.getFpIntroduceFileName());
//				}
//
//				ProjectConverter.convertPeriod(project, frProject.getFpHope1());
//				ProjectConverter.convertBudget(project, frProject.getFpHope2());
//				ProjectConverter.convertPayCriteria(project, frProject.getFpHope3());
//				ProjectConverter.convertWorkPlace(project, frProject.getFpHope4());
//
//				project.setMemo(frProject.getFpMemo());
//				ProjectConverter.convertPostingPeriod(project, frProject, frProject.getFpTerm());
//				project.setSuccessBidPrice(frProject.getFpContractPrice());
//				project.setPostingEndAt(frProject.getFpEndDate());
//				project.setContractAt(frProject.getFpConDate());
//				project.setStartAt(frProject.getFpWinDate());
//				project.setContractCompletedAt(frProject.getFpComDate());
//				project.setUseEscrow("Y".equalsIgnoreCase(frProject.getFpEscrowYn()));
//				project.setReceiveEmailAtBid("Y".equalsIgnoreCase(frProject.getFpNotice1()));
//				project.setReceiveSmsAtBid("Y".equalsIgnoreCase(frProject.getFpNotice2()));
//				if (!StringUtils.isEmpty(frProject.getFpCanText()) || !StringUtils.isEmpty(frProject.getFpCanType())) {
//					project.setCancelReason(
//							StringUtils.join(Arrays.asList(frProject.getFpCanType(), frProject.getFpCanText()), ","));
//				}
//				Project.Status status = null;
//				if ("1".equalsIgnoreCase(frProject.getFpType())) {
//					status = Project.Status.TEMP;
//				} else if ("2".equalsIgnoreCase(frProject.getFpType())) {
//					status = Project.Status.POSTED;
//				} else if ("3".equalsIgnoreCase(frProject.getFpType())) {
//					status = Project.Status.CANCELLED;
//				} else if ("4".equalsIgnoreCase(frProject.getFpType())) {
//					status = Project.Status.TEMP;
//				} else if ("5".equalsIgnoreCase(frProject.getFpType())) {
//					status = Project.Status.IN_PROGRESS;
//				} else if ("6".equalsIgnoreCase(frProject.getFpType())) {
//					status = Project.Status.COMPLETED;
//				}
//				project.setStatus(status);
//				if (Project.Status.COMPLETED.equals(status)) {
//					project.setCompletedAt(frProject.getFpComDate());
//				}
//
//				projectRepository.save(project);
//
//				List<FrProjectTag> frProjectTags = frProjectTagRepository.findByFpNo(Math.toIntExact(project.getId()));
//				for (FrProjectTag tag: frProjectTags) {
//					if (StringUtils.isEmpty(tag.getFtTag())) continue;
//					for (String categoryStr: tag.getFtTag().split("\\|-\\|")) {
//						try {
//							Category category = getCategoryFromTwoPhaseAndKeyword(tag.getFcId2(), categoryStr);
//							if (category == null) continue;
//							ProjectCategory projectCategory = new ProjectCategory();
//							projectCategory.setProject(project);
//							projectCategory.setCategory(category);
//							projectCategoryRepository.save(projectCategory);
//						} catch (Exception e) {
////							e.printStackTrace();
//						}
//					}
//				}
//
//				if (Project.Status.POSTED.equals(project.getStatus()) && !project.isPostingEnd()) {
//					ProjectItemTicket externalTicket = new ProjectItemTicket();
//					externalTicket.setCreatedAt(LocalDateTime.now());
//					externalTicket.setExpiredAt(project.getPostingEndAt());
//					externalTicket.setProject(project);
//					externalTicket.setProjectProductItemType(externalProjectItem);
//					projectItemTicketRepository.save(externalTicket);
//
//					ProjectItemTicket internalTicket = new ProjectItemTicket();
//					internalTicket.setCreatedAt(LocalDateTime.now());
//					internalTicket.setExpiredAt(project.getPostingEndAt());
//					internalTicket.setProject(project);
//					internalTicket.setProjectProductItemType(internalProjectItem);
//					projectItemTicketRepository.save(internalTicket);
//
//					ProjectItemTicketLog externalTicketLog = new ProjectItemTicketLog();
//					externalTicketLog.setProject(project);
//					externalTicketLog.setExpiredAt(externalTicket.getExpiredAt());
//					externalTicketLog.setProjectProductItemType(externalProjectItem);
//					externalTicketLog.setOptionPrice(0);
//					externalTicketLog.setOptionCount(1);
//					projectItemTicketLogRepository.save(externalTicketLog);
//
//					ProjectItemTicketLog internalTicketLog = new ProjectItemTicketLog();
//					internalTicketLog.setProject(project);
//					internalTicketLog.setExpiredAt(internalTicket.getExpiredAt());
//					internalTicketLog.setProjectProductItemType(internalProjectItem);
//					internalTicketLog.setOptionPrice(0);
//					internalTicketLog.setOptionCount(1);
//					projectItemTicketLogRepository.save(internalTicketLog);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

		List<ClientMemberPrice> clientMemberPrices = clientMemberPriceRepository.findAll();
		for (ClientMemberPrice memberPrice : clientMemberPrices) {
			try {
				if (Arrays.asList("1", "5").contains(memberPrice.getCpType())) {
					EscrowLog escrowLog = new EscrowLog();
					escrowLog.setCreatedAt(memberPrice.getCpDay());
					escrowLog.setUpdatedAt(memberPrice.getCpDay());
					if (StringUtils.isNotEmpty(memberPrice.getFpNo())) {
						escrowLog.setProject(projectRepository.getOne(Long.valueOf(memberPrice.getFpNo())));
					}
					escrowLog.setDepositUser(userRepository.findByUsername(memberPrice.getCmId()));
					EscrowLog.Type type = null;
					if ("1".equalsIgnoreCase(memberPrice.getCpType())) {
						type = EscrowLog.Type.DEPOSIT;
					} else if ("5".equalsIgnoreCase(memberPrice.getCpType())) {
						type = EscrowLog.Type.PENDING;
					}
					escrowLog.setAmount(memberPrice.getCpPrice());
					escrowLog.setAmountWhVat(memberPrice.getCpTotalPrice());
					escrowLog.setType(type);
					escrowLogRepository.save(escrowLog);
				} else {
					EscrowRefundRequest refundRequest = new EscrowRefundRequest();
					EscrowRefundRequest.Type type = null;
					if ("2".equalsIgnoreCase(memberPrice.getCpType())) {
						type = EscrowRefundRequest.Type.REQUEST;
					} else if ("3".equalsIgnoreCase(memberPrice.getCpType())) {
						type = EscrowRefundRequest.Type.PROCESSED;
					} else if ("4".equalsIgnoreCase(memberPrice.getCpType())) {
						type = EscrowRefundRequest.Type.CANCELLED;
					}
					refundRequest.setType(type);
					refundRequest.setAmount(memberPrice.getCpPrice());
					refundRequest.setReason(memberPrice.getCpMemo());
					if ("개인".equalsIgnoreCase(memberPrice.getCmBusinessType())) {
						refundRequest.setBusinessType(User.BusinessType.INDIVIDUAL);
					} else if ("개인사업자".equalsIgnoreCase(memberPrice.getCmBusinessType())) {
						refundRequest.setBusinessType(User.BusinessType.INDIV_BUSINESS);
					} else if ("법인사업자".equalsIgnoreCase(memberPrice.getCmBusinessType())) {
						refundRequest.setBusinessType(User.BusinessType.CORP_BUSINESS);
					}
					refundRequest.setCreatedAt(memberPrice.getCpApplicationDay());
					refundRequest.setRefundedAt(memberPrice.getCpDay());

					escrowRefundRequestRepository.save(refundRequest);
				}
			} catch (Exception e) {
				log.error("<<< {} 에스크로 마이그레이션 중 에러 발생.", memberPrice.getCpNo(), e);
			}
		}

		List<FrProjectAmount> frProjectAmounts = frProjectAmountRepository.findAll(new Sort(Sort.Direction.ASC, "faNo"));
		for (FrProjectAmount frProjectAmount : frProjectAmounts) {
			try {
				FrProject frProject = frProjectRepository.getOne(frProjectAmount.getFpNo());
				Project project = projectRepository.getOne(Long.valueOf(frProjectAmount.getFpNo()));
				User freelancerUser = userRepository.findByUsername(frProject.getFmId());
				PaymentToUser paymentToUser = new PaymentToUser();
				paymentToUser.setProject(project);
				PaymentToUser.Status status = null;
				switch (frProjectAmount.getFaType()) {
					case "1":
						status = PaymentToUser.Status.REQUESTED;
						break;
					case "2":
						status = PaymentToUser.Status.ACCEPTED;
						break;
					case "3":
						status = PaymentToUser.Status.PAYED;
						break;
					case "4":
						status = PaymentToUser.Status.DENIED;
						break;
				}
				paymentToUser.setType(PaymentToUser.Type.PROJECT);
				paymentToUser.setUser(freelancerUser);
				paymentToUser.setStatus(status);
				paymentToUser.setProject(project);
				paymentToUser.setAmount(frProjectAmount.getFaPrice());
				paymentToUser.setDescription(frProjectAmount.getFaMemo1());
				paymentToUser.setAcceptDescription(frProjectAmount.getFaMemo2());
				paymentToUser.setPaymentDescription(frProjectAmount.getFaMemo3());
				paymentToUser.setDenyReason(frProjectAmount.getFaMemo4());
				paymentToUser.setCreatedAt(frProjectAmount.getFaDate1());
				paymentToUser.setAcceptedAt(frProjectAmount.getFaDate2());
				paymentToUser.setPayedAt(frProjectAmount.getFaDate3());
				paymentToUser.setDeniedAt(frProjectAmount.getFaDate4());

				if (paymentToUser.getUser() != null) {
					paymentToUser.setBankType(paymentToUser.getUser().getBankForReceivingPayment());
					paymentToUser.setBankAccountName(paymentToUser.getUser().getBankAccountName());
					paymentToUser.setBankAccountForReceivingPayment(paymentToUser.getUser().getBankAccountName());
					paymentToUser.setTaxType(paymentToUser.getUser().getTaxType());
				}

				if (PaymentToUser.Status.PAYED.equals(paymentToUser.getStatus())) {
					EscrowLog escrowLog = new EscrowLog();
					escrowLog.setType(EscrowLog.Type.WITHDRAWAL);
					escrowLog.setWithdrawalUser(paymentToUser.getProject().getPostingClient());
					escrowLog.setProject(paymentToUser.getProject());
					escrowLog.setAmount((int) paymentToUser.getAmount());
					escrowLog.setCreatedAt(frProjectAmount.getFaDate3());
					escrowLog.setUpdatedAt(frProjectAmount.getFaDate3());
					escrowLog.setAmountWhVat((int) (paymentToUser.getAmount() + paymentToUser.getAmount() * 0.1));
					escrowLogRepository.save(escrowLog);
				}

				paymentToUserRepository.save(paymentToUser);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		List<FrProjectApply> projectApplies = frProjectApplyRepository.findAll();
		for (FrProjectApply apply : projectApplies) {
			try {
				User freelancerUser = userRepository.findByUsername(apply.getFmId());
				Project project = projectRepository.getOne(Long.valueOf(apply.getFpNo()));
				ProjectBid bid = projectBidRepository.findTop1ByParticipantIdAndProjectIdOrderByCreatedAtDesc(freelancerUser.getId(), project.getId());
				if (bid == null) {
					bid = new ProjectBid();
				}
				bid.setBidType(ProjectBid.BidType.PROJECT_BID);
				bid.setApplyAt(apply.getFaRegDate());
				bid.setParticipant(freelancerUser);
				bid.setAmountOfMoney(apply.getFaPrice());
				bid.setCreatedAt(apply.getFaRegDate());
				bid.setProject(project);
				bid.setAmountOfMoney(apply.getFaPrice());

				ProjectBid.BidStatus bidStatus = null;

				if ("1".equals(apply.getFaType())) {
					bidStatus = ProjectBid.BidStatus.APPLY;
				} else if ("2".equals(apply.getFaType())) {
					bidStatus = ProjectBid.BidStatus.CANCEL;
				} else if ("3".equals(apply.getFaType())) {
					bidStatus = ProjectBid.BidStatus.FAILED;
				} else if ("4".equals(apply.getFaType())) {
					bidStatus = ProjectBid.BidStatus.PICKED;
				}
				bid.setBidStatus(bidStatus);
				projectBidRepository.save(bid);

				if (ProjectBid.BidStatus.PICKED.equals(bidStatus)) {
					project.setSuccessBidPrice(bid.getAmountOfMoney());
					project.setSuccessBidAt(bid.getApplyAt());
				}
			} catch (Exception e) {
				log.error("<<< {}번 프로젝트 지원 마이그레이션 중 문제 발생", apply.getFaNo(), e);
			}
		}

		for (FrProject frProject : frProjects) {
			try {
				Optional<Project> projectOptional = projectRepository.findById(Long.valueOf(frProject.getFpNo()));
				Project project = null;
				if (!projectOptional.isPresent()) {
					continue;
				} else {
					project = projectOptional.get();
				}
				if (StringUtils.isNotEmpty(frProject.getFmId())) {
					User freelancerUser = userRepository.findByUsername(frProject.getFmId());
					project.setContractedFreelancer(freelancerUser);
					ProjectBid projectBid = projectBidRepository.findTop1ByParticipantIdAndProjectIdOrderByCreatedAtDesc(freelancerUser.getId(), project.getId());
					if (projectBid == null) {
						projectBid = new ProjectBid();
						projectBid.setBidType(ProjectBid.BidType.PROJECT_BID);
						projectBid.setParticipant(freelancerUser);
						projectBid.setBidStatus(ProjectBid.BidStatus.PICKED);
						projectBid.setPickedAmountOfMoney(frProject.getFpContractPrice());
						projectBid.setSuccessBidAt(frProject.getFpWinDate());

						projectBidRepository.save(projectBid);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void migrateProjectFile() {
		List<FrProjectFile> frProjectFiles = frProjectFileRepository.findAll();
		for (FrProjectFile frProjectFile: frProjectFiles) {
			try {
				if (StringUtils.isEmpty(frProjectFile.getFfFile1())) {
					continue;
				}

				Project project = projectRepository.getOne(Long.valueOf(frProjectFile.getFpNo()));
				List<ProjectContractFile> contractFiles = new ArrayList<>();
				if (StringUtils.isNotEmpty(frProjectFile.getFfFile1())) {
					ProjectContractFile projectContractFile = new ProjectContractFile();
					projectContractFile.setFileUrl(getUrlFrom(projectFilePrefix + frProjectFile.getFfFile1(), frProjectFile.getFfFile1Name()));
					projectContractFile.setFileName(frProjectFile.getFfFile1Name());
					projectContractFile.setProject(project);
					projectContractFile.setUserRole("1".equalsIgnoreCase(frProjectFile.getFfType()) ? User.Role.ROLE_CLIENT : User.Role.ROLE_FREELANCER);
					projectContractFile.setUser(User.Role.ROLE_CLIENT.equals(projectContractFile.getUserRole())?project.getPostingClient():project.getContractedFreelancer());
					if (StringUtils.isNotEmpty(frProjectFile.getFfFile2())) {
						projectContractFile.setInvalid(true);
					}
					contractFiles.add(projectContractFile);
				}

				if (StringUtils.isNotEmpty(frProjectFile.getFfFile2())) {
					ProjectContractFile projectContractFile = new ProjectContractFile();
					projectContractFile.setFileUrl(getUrlFrom(projectFilePrefix + frProjectFile.getFfFile2(), frProjectFile.getFfFile2Name()));
					projectContractFile.setFileName(frProjectFile.getFfFile2Name());
					projectContractFile.setProject(project);
					projectContractFile.setUserRole("1".equalsIgnoreCase(frProjectFile.getFfType()) ? User.Role.ROLE_CLIENT : User.Role.ROLE_FREELANCER);
					projectContractFile.setUser(User.Role.ROLE_CLIENT.equals(projectContractFile.getUserRole())?project.getPostingClient():project.getContractedFreelancer());
					if (StringUtils.isNotEmpty(frProjectFile.getFfFile3())) {
						projectContractFile.setInvalid(true);
					}
					contractFiles.add(projectContractFile);
				}

				if (StringUtils.isNotEmpty(frProjectFile.getFfFile3())) {
					ProjectContractFile projectContractFile = new ProjectContractFile();
					projectContractFile.setFileUrl(getUrlFrom(projectFilePrefix + frProjectFile.getFfFile3(), frProjectFile.getFfFile3Name()));
					projectContractFile.setFileName(frProjectFile.getFfFile3Name());
					projectContractFile.setProject(project);
					projectContractFile.setUserRole("1".equalsIgnoreCase(frProjectFile.getFfType()) ? User.Role.ROLE_CLIENT : User.Role.ROLE_FREELANCER);
					projectContractFile.setUser(User.Role.ROLE_CLIENT.equals(projectContractFile.getUserRole())?project.getPostingClient():project.getContractedFreelancer());
					if (StringUtils.isNotEmpty(frProjectFile.getFfFile4())) {
						projectContractFile.setInvalid(true);
					}
					contractFiles.add(projectContractFile);
				}

				if (StringUtils.isNotEmpty(frProjectFile.getFfFile4())) {
					ProjectContractFile projectContractFile = new ProjectContractFile();
					projectContractFile.setFileUrl(getUrlFrom(projectFilePrefix + frProjectFile.getFfFile4(), frProjectFile.getFfFile4Name()));
					projectContractFile.setFileName(frProjectFile.getFfFile4Name());
					projectContractFile.setProject(project);
					projectContractFile.setUserRole("1".equalsIgnoreCase(frProjectFile.getFfType()) ? User.Role.ROLE_CLIENT : User.Role.ROLE_FREELANCER);
					projectContractFile.setUser(User.Role.ROLE_CLIENT.equals(projectContractFile.getUserRole())?project.getPostingClient():project.getContractedFreelancer());
					if (StringUtils.isNotEmpty(frProjectFile.getFfFile5())) {
						projectContractFile.setInvalid(true);
					}
					contractFiles.add(projectContractFile);
				}
				projectContractFileRepository.saveAll(contractFiles);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void migrateProjectRate() {
		List<FrProjectGrade> projectGrades = frProjectGradeRepository.findAll();
		for (FrProjectGrade frProjectGrade : projectGrades) {
			User raterUser = userRepository.findByUsername(
					"1".equalsIgnoreCase(frProjectGrade.getFgType()) ? frProjectGrade.getCmId() : frProjectGrade.getFmId());
			User ratedUser = userRepository.findByUsername(
					"1".equalsIgnoreCase(frProjectGrade.getFgType()) ? frProjectGrade.getFmId() : frProjectGrade.getCmId());
			ProjectRate projectRate = projectRateRepository.findByProjectIdAndRaterUserIdAndRatedUserId(Long.valueOf(frProjectGrade.getFpNo()), raterUser.getId(), ratedUser.getId());
			if (projectRate == null) {
				projectRate = new ProjectRate();
			}
			projectRate.setCreatedAt(frProjectGrade.getFgRegDate());
			projectRate.setRaterType(
					"1".equalsIgnoreCase(frProjectGrade.getFgType()) ? ProjectRate.RaterType.CLIENT : ProjectRate.RaterType.FREELANCER);

			projectRate.setRaterUser(raterUser);
			projectRate.setProject(projectRepository.getOne(Long.valueOf(frProjectGrade.getFpNo())));
			projectRate.setRatedUser(ratedUser);
			projectRate.setType1Rate(frProjectGrade.getFgGrade1());
			projectRate.setType2Rate(frProjectGrade.getFgGrade2());
			projectRate.setType3Rate(frProjectGrade.getFgGrade3());
			projectRate.setType4Rate(frProjectGrade.getFgGrade4());
			projectRate.setType5Rate(frProjectGrade.getFgGrade5());
			projectRate.setContent(frProjectGrade.getFgText());

			projectRateRepository.save(projectRate);

			ProjectComplete projectComplete = projectCompleteRepository.findByProjectId(projectRate.getProject().getId());
			if (projectComplete == null) {
				projectComplete = new ProjectComplete();
				projectComplete.setProject(projectRate.getProject());
			}

			if (ProjectRate.RaterType.FREELANCER.equals(projectRate.getRaterType())) {
				projectComplete.setFreelancerRequest(true);
				projectComplete.setFreelancerRequestAt(frProjectGrade.getFgRegDate());
			} else if (ProjectRate.RaterType.CLIENT.equals(projectRate.getRaterType())) {
				projectComplete.setClientAccept(true);
				projectComplete.setClientAcceptAt(frProjectGrade.getFgRegDate());
			}
			projectCompleteRepository.save(projectComplete);
		}
	}

	@Override
	public void migratePickMeUp() {
		FreelancerProductItemType pickMeUpItem = freelancerProductItemTypeRepository.findByCode(FreelancerProductItemType.Code.PICK_ME_UP);
        List<FrPortfolio> pickMeUps = frPickMeUpRepository.findAll();
        for (FrPortfolio frPortfolio : pickMeUps) {
            try {
				User user = userRepository.findByUsername(frPortfolio.getFmId());
				Optional<PickMeUp> pickMeUpOptional = pickMeUpRepository.findById(Long.valueOf(frPortfolio.getFpNo()));
                PickMeUp pickMeUp = pickMeUpOptional.orElseGet(PickMeUp::new);
                pickMeUp.setTitle(frPortfolio.getFpSubject());
                pickMeUp.setDescription(frPortfolio.getFpContent());
                pickMeUp.setId(Long.valueOf(frPortfolio.getFpNo()));
				pickMeUp.setUser(user);
                pickMeUp.setWorkStartAt(frPortfolio.getFpStartDay());
                pickMeUp.setWorkEndAt(frPortfolio.getFpEndDay());
                pickMeUp.setCreatedAt(frPortfolio.getFpRegDate());
                pickMeUp.setExposeAdmin("Y".equalsIgnoreCase(frPortfolio.getFpAdminDisplayYn()));

                int portfolioIndex = 0;
				if (StringUtils.isEmpty(pickMeUp.getMainImageUrl()) && StringUtils.isNotEmpty(frPortfolio.getFpImg1())) {
					pickMeUp.setMainImageUrl(getUrlFrom(portfolioImagePrefix + frPortfolio.getFpImg1(),String.format("포트폴리오 %d", portfolioIndex++)));
				} else if (StringUtils.isEmpty(pickMeUp.getMainImageUrl())) {
					pickMeUp.setMainImageUrl(portfolioDefaultImage);
				}

                if (StringUtils.isNotEmpty(frPortfolio.getFcId())) {
                    Category category = categoryRepository.findByOriginalCode(frPortfolio.getFcId());
                    if (category == null) {
                        log.warn("<<< {} 카테고리가 없습니다", frPortfolio.getFcId());
                    } else {
                    	pickMeUp.setCategory1st(category);
                    }
                }
                if (StringUtils.isNotEmpty(frPortfolio.getFcId2())) {
                    Category category = categoryRepository.findByOriginalCode(frPortfolio.getFcId2());
                    if (category == null) {
                        log.warn("<<< {} 카테고리가 없습니다", frPortfolio.getFcId2());
                    } else {
                    	pickMeUp.setCategory2nd(category);
                    }
                }

                if (pickMeUpOptional.isPresent()) {
                	continue;
				}

                List<PickMeUpDetailFile> detailFiles = new ArrayList<>();

				if (StringUtils.isNotEmpty(frPortfolio.getFpImg2())) {
					detailFiles.add(savePickMeUpDetailImage(getUrlFrom(portfolioImagePrefix + frPortfolio.getFpImg2(),String.format("포트폴리오 %d", portfolioIndex++)), pickMeUp));
				}
				if (StringUtils.isNotEmpty(frPortfolio.getFpImg3())) {
					detailFiles.add(savePickMeUpDetailImage(getUrlFrom(portfolioImagePrefix + frPortfolio.getFpImg3(),String.format("포트폴리오 %d", portfolioIndex++)), pickMeUp));
				}
				int freeChargeUseCount = pickMeUpFreeChargeRepository.countByUserId(pickMeUp.getUser().getId());

				if (PaymentPolicy.PICK_ME_UP_FREE_CHARGE_COUNT - freeChargeUseCount > 0 && "Y".equalsIgnoreCase(frPortfolio.getFpDisplayYn())) {
					pickMeUp.setFreeCharge(true);
				}

				pickMeUp.setDetailFiles(detailFiles);
				pickMeUp = pickMeUpRepository.save(pickMeUp);

                if (PaymentPolicy.PICK_ME_UP_FREE_CHARGE_COUNT - freeChargeUseCount > 0 && "Y".equalsIgnoreCase(frPortfolio.getFpDisplayYn())) {
                	PickMeUpTicket pickMeUpTicket = new PickMeUpTicket();
                	pickMeUpTicket.setPickMeUp(pickMeUp);
                	pickMeUpTicket.setUser(pickMeUp.getUser());
                	pickMeUpTicket.setStartAt(pickMeUp.getCreatedAt());
                	pickMeUpTicket.setEndAt(pickMeUp.getCreatedAt().plusYears(1000));
                	pickMeUpTicket.setFreelancerProductItemType(pickMeUpItem);
                	pickMeUpTicketRepository.save(pickMeUpTicket);

                	PickMeUpTicketLog log = new PickMeUpTicketLog();
                	log.setFreelancerProductItemType(pickMeUpItem);
                	log.setCreatedAt(pickMeUpTicket.getStartAt());
                	log.setExpiredAt(pickMeUpTicket.getEndAt());
                	log.setPickMeUp(pickMeUp);
                	log.setOptionCount(1);
                	log.setOptionPrice(pickMeUpItem.getUnitPrice());
                	pickMeUpTicketLogRepository.save(log);

                	PickMeUpFreeCharge pickMeUpFreeCharge = new PickMeUpFreeCharge();
                	pickMeUpFreeCharge.setPickMeUp(pickMeUp);
                	pickMeUpFreeCharge.setUser(pickMeUp.getUser());
                	pickMeUpFreeCharge.setCount(1);
                	pickMeUpFreeChargeRepository.save(pickMeUpFreeCharge);
				}
            } catch (Exception e) {
                log.error("<<< 포트폴리오 {} 마이그레이션 중 에러 발생", frPortfolio.getFpSubject(), e);
            }
        }
	}

	public void migrateTemp() {
//		List<FrProjectApply> projectApplies = frProjectApplyRepository.findAll();
//		for (FrProjectApply apply : projectApplies) {
//			try {
//				User freelancerUser = userRepository.findByUsername(apply.getFmId());
//				Project project = projectRepository.getOne(Long.valueOf(apply.getFpNo()));
//				ProjectBid bid = projectBidRepository.findTop1ByParticipantIdAndProjectIdOrderByCreatedAtDesc(freelancerUser.getId(), project.getId());
//				if (bid == null) {
//					continue;
//				}
//
//				if (ProjectBid.BidStatus.PICKED.equals(bid.getBidStatus())) {
//					project.setSuccessBidPrice(bid.getAmountOfMoney());
//					project.setSuccessBidAt(bid.getApplyAt());
//					projectRepository.save(project);
//					log.info("<<< {} 번 프로젝트", project.getId());
//				}
//			} catch (Exception e) {
//				log.error("<<< {}번 프로젝트 지원 마이그레이션 중 문제 발생", apply.getFaNo(), e);
//			}
//		}

//		List<FrProject> frProjects = frProjectRepository.findAll();
//		for (FrProject frProject : frProjects) {
//			try {
//				Optional<Project> projectOptional = projectRepository.findById(Long.valueOf(frProject.getFpNo()));
//				Project project = null;
//				if (!projectOptional.isPresent()) {
//					continue;
//				} else {
//					project = projectOptional.get();
//				}
//				Project.Status status = null;
//				if ("1".equalsIgnoreCase(frProject.getFpType())) {
//					status = Project.Status.TEMP;
//				} else if ("2".equalsIgnoreCase(frProject.getFpType())) {
//					status = Project.Status.POSTED;
//				} else if ("3".equalsIgnoreCase(frProject.getFpType())) {
//					status = Project.Status.CANCELLED;
//				} else if ("4".equalsIgnoreCase(frProject.getFpType())) {
//					status = Project.Status.TEMP;
//				} else if ("5".equalsIgnoreCase(frProject.getFpType())) {
//					status = Project.Status.IN_PROGRESS;
//				} else if ("6".equalsIgnoreCase(frProject.getFpType())) {
//					status = Project.Status.COMPLETED;
//				}
//				project.setStatus(status);
//
//				projectRepository.save(project);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		FrProject frProject = frProjectRepository.getOne(691);
//		Project project = projectRepository.getOne(691L);
//		ProjectConverter.convertPayCriteria(project, frProject.getFpHope3());
//		projectRepository.save(project);

		List<FreelancerMember> freelancerMembers = freelancerMemberRepository.findAll();
		for (FreelancerMember freelancerMember : freelancerMembers) {
			try {

				List<FreelancerMemberTag> freelancerMemberTags = freelancerMemberTagRepository.findByFmId(freelancerMember.getFmId());
				Set<Category> categories = new HashSet<>();
				for (FreelancerMemberTag tag: freelancerMemberTags) {
					if (StringUtils.isEmpty(tag.getFtTag())) continue;
					for (String categoryStr: tag.getFtTag().split("\\|-\\|")) {
						Category category = getCategoryFromTwoPhaseAndKeyword(tag.getFcId2(), categoryStr);
						if (category == null) continue;
						categories.add(category);
					}
				}
				User user = userRepository.findByUsername(freelancerMember.getFmId());
				user.getCategories().clear();
				user.getCategories().addAll(categories);
				userRepository.save(user);
				log.info("<<< {} 번 유저", user.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		List<PaymentToUser> paymentToUsers = paymentToUserRepository.findAll();
//		for (PaymentToUser paymentToUser: paymentToUsers) {
//			if (paymentToUser.getUser() == null) continue;
//			paymentToUser.setBankType(paymentToUser.getUser().getBankForReceivingPayment());
//			paymentToUser.setBankAccountName(paymentToUser.getUser().getBankAccountName());
//			paymentToUser.setBankAccountForReceivingPayment(paymentToUser.getUser().getBankAccountForReceivingPayment());
//			paymentToUser.setTaxType(paymentToUser.getUser().getTaxType());
//			paymentToUserRepository.save(paymentToUser);
//		}

//		List<ClientMemberPrice> clientMemberPrices = clientMemberPriceRepository.findAll();
//		for (ClientMemberPrice memberPrice : clientMemberPrices) {
//			if (Arrays.asList("1", "5").contains(memberPrice.getCpType())) {
//				EscrowLog escrowLog = new EscrowLog();
//				escrowLog.setCreatedAt(memberPrice.getCpDay());
//				escrowLog.setUpdatedAt(memberPrice.getCpDay());
//				if (StringUtils.isNotEmpty(memberPrice.getFpNo())) {
//					escrowLog.setProject(projectRepository.getOne(Long.valueOf(memberPrice.getFpNo())));
//				}
//				escrowLog.setDepositUser(userRepository.findByUsername(memberPrice.getCmId()));
//				EscrowLog.Type type = null;
//				if ("1".equalsIgnoreCase(memberPrice.getCpType())) {
//					type = EscrowLog.Type.DEPOSIT;
//				} else if ("5".equalsIgnoreCase(memberPrice.getCpType())) {
//					type = EscrowLog.Type.PENDING;
//				}
//				escrowLog.setAmount(memberPrice.getCpPrice());
//				escrowLog.setAmountWhVat(memberPrice.getCpTotalPrice());
//				escrowLog.setType(type);
//				escrowLogRepository.save(escrowLog);
//			}
//		}

//		List<ClientMemberPrice> clientMemberPrices = clientMemberPriceRepository.findAll();
//		for (ClientMemberPrice memberPrice : clientMemberPrices) {
//			if (Arrays.asList("1", "5").contains(memberPrice.getCpType())) {
//				EscrowLog escrowLog = new EscrowLog();
//				escrowLog.setCreatedAt(memberPrice.getCpDay());
//				escrowLog.setUpdatedAt(memberPrice.getCpDay());
//				if (StringUtils.isNotEmpty(memberPrice.getFpNo())) {
//					escrowLog.setProject(projectRepository.getOne(Long.valueOf(memberPrice.getFpNo())));
//				}
//				escrowLog.setDepositUser(userRepository.findByUsername(memberPrice.getCmId()));
//				EscrowLog.Type type = null;
//				if ("1".equalsIgnoreCase(memberPrice.getCpType())) {
//					type = EscrowLog.Type.DEPOSIT;
//				} else if ("5".equalsIgnoreCase(memberPrice.getCpType())) {
//					type = EscrowLog.Type.PENDING;
//				}
//				escrowLog.setAmount(memberPrice.getCpPrice());
//				escrowLog.setAmountWhVat(memberPrice.getCpTotalPrice());
//				escrowLog.setType(type);
//				escrowLogRepository.save(escrowLog);
//			} else {
//				EscrowRefundRequest refundRequest = new EscrowRefundRequest();
//				EscrowRefundRequest.Type type = null;
//				if ("2".equalsIgnoreCase(memberPrice.getCpType())) {
//					type = EscrowRefundRequest.Type.REQUEST;
//				} else if ("3".equalsIgnoreCase(memberPrice.getCpType())) {
//					type = EscrowRefundRequest.Type.PROCESSED;
//				} else if ("4".equalsIgnoreCase(memberPrice.getCpType())) {
//					type = EscrowRefundRequest.Type.CANCELLED;
//				}
//				refundRequest.setType(type);
//				refundRequest.setAmount(memberPrice.getCpPrice());
//				refundRequest.setReason(memberPrice.getCpMemo());
//				if ("개인".equalsIgnoreCase(memberPrice.getCmBusinessType())) {
//					refundRequest.setBusinessType(User.BusinessType.INDIVIDUAL);
//				} else if ("개인사업자".equalsIgnoreCase(memberPrice.getCmBusinessType())) {
//					refundRequest.setBusinessType(User.BusinessType.INDIV_BUSINESS);
//				} else if ("법인사업자".equalsIgnoreCase(memberPrice.getCmBusinessType())) {
//					refundRequest.setBusinessType(User.BusinessType.CORP_BUSINESS);
//				}
//				refundRequest.setCreatedAt(memberPrice.getCpApplicationDay());
//				refundRequest.setRefundedAt(memberPrice.getCpDay());
//
//				escrowRefundRequestRepository.save(refundRequest);
//			}
//		}
//
//		List<FrProjectAmount> frProjectAmounts = frProjectAmountRepository.findAll(new Sort(Sort.Direction.ASC, "faNo"));
//		for (FrProjectAmount frProjectAmount : frProjectAmounts) {
//			try {
//				FrProject frProject = frProjectRepository.getOne(frProjectAmount.getFpNo());
//				Project project = projectRepository.getOne(Long.valueOf(frProjectAmount.getFpNo()));
//				User freelancerUser = userRepository.findByUsername(frProject.getFmId());
//				PaymentToUser paymentToUser = new PaymentToUser();
//				paymentToUser.setProject(project);
//				PaymentToUser.Status status = null;
//				switch (frProjectAmount.getFaType()) {
//					case "1":
//						status = PaymentToUser.Status.REQUESTED;
//						break;
//					case "2":
//						status = PaymentToUser.Status.ACCEPTED;
//						break;
//					case "3":
//						status = PaymentToUser.Status.PAYED;
//						break;
//					case "4":
//						status = PaymentToUser.Status.DENIED;
//						break;
//				}
//				paymentToUser.setType(PaymentToUser.Type.PROJECT);
//				paymentToUser.setUser(freelancerUser);
//				paymentToUser.setStatus(status);
//				paymentToUser.setProject(project);
//				paymentToUser.setAmount(frProjectAmount.getFaPrice());
//				paymentToUser.setDescription(frProjectAmount.getFaMemo1());
//				paymentToUser.setAcceptDescription(frProjectAmount.getFaMemo2());
//				paymentToUser.setPaymentDescription(frProjectAmount.getFaMemo3());
//				paymentToUser.setDenyReason(frProjectAmount.getFaMemo4());
//				paymentToUser.setCreatedAt(frProjectAmount.getFaDate1());
//				paymentToUser.setAcceptedAt(frProjectAmount.getFaDate2());
//				paymentToUser.setPayedAt(frProjectAmount.getFaDate3());
//				paymentToUser.setDeniedAt(frProjectAmount.getFaDate4());
//
//				if (paymentToUser.getUser() != null) {
//					paymentToUser.setBankType(paymentToUser.getUser().getBankForReceivingPayment());
//					paymentToUser.setBankAccountName(paymentToUser.getUser().getBankAccountName());
//					paymentToUser.setBankAccountForReceivingPayment(paymentToUser.getUser().getBankAccountName());
//					paymentToUser.setTaxType(paymentToUser.getUser().getTaxType());
//				}
//
//				if (PaymentToUser.Status.PAYED.equals(paymentToUser.getStatus())) {
//					EscrowLog escrowLog = new EscrowLog();
//					escrowLog.setType(EscrowLog.Type.WITHDRAWAL);
//					escrowLog.setWithdrawalUser(paymentToUser.getProject().getPostingClient());
//					escrowLog.setProject(paymentToUser.getProject());
//					escrowLog.setAmount((int) paymentToUser.getAmount());
//					escrowLog.setCreatedAt(frProjectAmount.getFaDate3());
//					escrowLog.setUpdatedAt(frProjectAmount.getFaDate3());
//					escrowLog.setAmountWhVat((int) (paymentToUser.getAmount() + paymentToUser.getAmount() * 0.1));
//					escrowLogRepository.save(escrowLog);
//				}
//
//				paymentToUserRepository.save(paymentToUser);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

//		List<ClientMember> clientMemberList = clientMemberRepository.findAll();
//		for (ClientMember clientMember : clientMemberList) {
//			try {
//				User user = userRepository.findByUsername(clientMember.getCmId());
//				if (user == null) continue;
//				user.setReceiveEmail("Y".equalsIgnoreCase(clientMember.getCmEmailNotice()));
//				user.setBusinessLicenseFileName(clientMember.getCmLicenseFileName());
//				userRepository.save(user);
//				log.info("<<< {} 번 유저", user.getId());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		List<FreelancerMember> freelancerMembers = freelancerMemberRepository.findAll();
//		for (FreelancerMember freelancerMember: freelancerMembers) {
//			try {
//				User user = userRepository.findByUsername(freelancerMember.getFmId());
//				if (user == null) continue;
//				user.setReceiveEmail("Y".equalsIgnoreCase(freelancerMember.getFmEmailNotice()));
//				userRepository.save(user);
//				log.info("<<< {} 번 유저", user.getId());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

//		List<FrProject> frProjects = frProjectRepository.findAll();
//
//		for (FrProject frProject : frProjects) {
//			try {
//				Optional<Project> projectOptional = projectRepository.findById(Long.valueOf(frProject.getFpNo()));
//				Project project = null;
//				if (!projectOptional.isPresent()) {
//					continue;
//				} else {
//					project = projectOptional.get();
//				}
////				if (Project.Status.COMPLETED.equals(project.getStatus())) {
////					project.setCompletedAt(frProject.getFpComDate());
////				}
//				project.setStartAt(frProject.getFpWinDate());
//				projectRepository.save(project);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}


//		List<FrProjectAmount> frProjectAmounts = frProjectAmountRepository.findAll(new Sort(Sort.Direction.ASC, "faNo"));
//		for (FrProjectAmount frProjectAmount : frProjectAmounts) {
//			try {
//				FrProject frProject = frProjectRepository.getOne(frProjectAmount.getFpNo());
//				Project project = projectRepository.getOne(Long.valueOf(frProjectAmount.getFpNo()));
//				User freelancerUser = userRepository.findByUsername(frProject.getFmId());
//				PaymentToUser paymentToUser = new PaymentToUser();
//				paymentToUser.setProject(project);
//				PaymentToUser.Status status = null;
//				switch (frProjectAmount.getFaType()) {
//					case "1":
//						status = PaymentToUser.Status.REQUESTED;
//						break;
//					case "2":
//						status = PaymentToUser.Status.ACCEPTED;
//						break;
//					case "3":
//						status = PaymentToUser.Status.PAYED;
//						break;
//					case "4":
//						status = PaymentToUser.Status.DENIED;
//						break;
//				}
//				paymentToUser.setType(PaymentToUser.Type.PROJECT);
//				paymentToUser.setUser(freelancerUser);
//				paymentToUser.setStatus(status);
//				paymentToUser.setProject(project);
//				paymentToUser.setAmount(frProjectAmount.getFaPrice());
//				paymentToUser.setDescription(frProjectAmount.getFaMemo1());
//				paymentToUser.setAcceptDescription(frProjectAmount.getFaMemo2());
//				paymentToUser.setPaymentDescription(frProjectAmount.getFaMemo3());
//				paymentToUser.setDenyReason(frProjectAmount.getFaMemo4());
//				paymentToUser.setCreatedAt(frProjectAmount.getFaDate1());
//				paymentToUser.setAcceptedAt(frProjectAmount.getFaDate2());
//				paymentToUser.setPayedAt(frProjectAmount.getFaDate3());
//				paymentToUser.setDeniedAt(frProjectAmount.getFaDate4());
//
//				paymentToUserRepository.save(paymentToUser);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		List<FrProject> frProjects = frProjectRepository.findAll();
//		for (FrProject frProject : frProjects) {
//			try {
//				Optional<Project> projectOptional = projectRepository.findById(Long.valueOf(frProject.getFpNo()));
//				Project project = null;
//				if (!projectOptional.isPresent()) {
//					project = new Project();
//					continue;
//				} else {
//					project = projectOptional.get();
//				}
//
//				ProjectConverter.convertPeriod(project, frProject.getFpHope1());
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}

	@Override
	public void giveInitialPoint() {
		List<User> users = userRepository.findByIdGreaterThan(480L);
		for (User user: users) {
			if (user.getRoles().contains(User.Role.ROLE_FREELANCER.name())) {
				pointService.givePointsToFreelancerForEtcExpiredAt(user, 50000, "2차 오픈 포인트 지급 이벤트", LocalDateTime.of(2019, 8, 31, 23, 59));
			} else if (user.getRoles().contains(User.Role.ROLE_CLIENT.name())) {
				pointService.givePointsToClient(user.getId(), 50000, "2차 오픈 포인트 지급 이벤트", null);
			}
		}
	}

	@Override
	public void successBidPrice() {
		Page<Project> allProject = projectRepository.findByProjectTypeAndStatusIn(Project.Type.PROJECT, Arrays.asList(Project.Status.IN_PROGRESS, Project.Status.COMPLETED), null);
		for (Project project: allProject) {
			if (project.getSuccessBidPrice() != null && project.getSuccessBidPrice() > 0) {
				log.info("<<< {} 번 프로젝트 건너뜀. {}", project.getId(), project.getSuccessBidPrice());
				continue;
			}

			List<ProjectBid> projectBid = projectBidRepository.findByProjectIdAndBidStatusIn(project.getId(), Arrays.asList(ProjectBid.BidStatus.PICKED));
			if (projectBid.isEmpty()) {
				log.info("<<< {} 번 프로젝트 건너뜀. picked 없음", project.getId());
				continue;
			}

			log.info("<<< {} 번 프로젝트 이전 낙찰 기록 금액 {} / 이후 낙찰 기록 금액 {}", project.getId(), project.getSuccessBidPrice(), projectBid.get(0).getAmountOfMoney());
			project.setSuccessBidPrice(projectBid.get(0).getAmountOfMoney());
			projectRepository.save(project);

		}
	}

	@Override
	public void migrateCellphoneCertified() {
		List<ClientMember> clientMemberList = clientMemberRepository.findAll();
		for (ClientMember clientMember : clientMemberList) {
			try {
				User user = userRepository.findByUsername(clientMember.getCmId());
				if (user == null || user.isCellphoneCertified()) continue;

				String priorCellphone = clientMember.getCmHp().replaceAll("\\D", "");
				if (StringUtils.isEmpty(user.getCellphone())) continue;
				if (!user.getCellphone().equalsIgnoreCase(priorCellphone)) continue;

				user.setCellphoneCertified("Y".equalsIgnoreCase(clientMember.getCmHpYn()));
				userRepository.save(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		List<FreelancerMember> freelancerMembers = freelancerMemberRepository.findAll();
		for (FreelancerMember freelancerMember : freelancerMembers) {
			try {
				User user = userRepository.findByUsername(freelancerMember.getFmId());
				if (user == null || user.isCellphoneCertified()) {
					continue;
				}

				String priorCellphone = freelancerMember.getFmHp().replaceAll("\\D", "");
				if (StringUtils.isEmpty(user.getCellphone())) continue;
				if (!user.getCellphone().equalsIgnoreCase(priorCellphone)) continue;

				user.setCellphoneCertified("Y".equalsIgnoreCase(freelancerMember.getFmHpYn()));
				userRepository.save(user);
			} catch (Exception e) {

			}
		}
	}

	@Override
	public void migrateCareerYear() {
		List<FreelancerMember> freelancerMembers = freelancerMemberRepository.findAll();
		for (FreelancerMember freelancerMember : freelancerMembers) {
			try {
				User user = userRepository.findByUsername(freelancerMember.getFmId());
				if (user.getCareerYear() != null || StringUtils.isEmpty(freelancerMember.getFmIntroduceCareer())) continue;
				log.info("<<< {} 번 유저", user.getId());
				user.setCareerYear(Integer.parseInt(freelancerMember.getFmIntroduceCareer()));
				userRepository.save(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void migratePickMeUpEtc() {
		List<PickMeUp> pickMeUps = pickMeUpRepository.findAll();
		for (PickMeUp pickMeUp: pickMeUps) {
			if (StringUtils.isAllEmpty(pickMeUp.getWorkPlaceAddress1(), pickMeUp.getWorkPlaceAddress2()) && pickMeUp.getWorkPlace() == null ) {
				if (pickMeUp.getUser().getUserJobPreference() != null && pickMeUp.getUser().getUserJobPreference().getWorkPlace() != null) {
					pickMeUp.setWorkPlace(pickMeUp.getUser().getUserJobPreference().getWorkPlace());
					pickMeUpRepository.save(pickMeUp);
				}
			}
		}
	}

	public PickMeUpDetailFile savePickMeUpDetailImage(String imageUrl, PickMeUp pickMeUp) {
		PickMeUpDetailFile file = new PickMeUpDetailFile();
		file.setFileUrl(imageUrl);
		file.setFileOriginName(FileUtil.getFileNameFromContentDisposition(imageUrl));
		file.setFileType(ContestEntryFile.Type.IMAGE);
		file.setPickMeUp(pickMeUp);;

		return file;
	}

	public String getUrlFrom(String url, String fileName) throws Exception {

		File f = new File("/tmp/temp");
		FileUtils.copyURLToFile(new URL(url), f);
		return storageService.storeFile(new FileInputStream(f), fileName);
	}

	public Category getCategoryFromTwoPhaseAndKeyword(String originalSectorCode, String keyword) {
		Category parentCategory = categoryRepository.findByOriginalCode(originalSectorCode);
		if (parentCategory == null) {
//			log.warn("<<< {} 에 해당하는 카테고리가 존재하지 않습니다", parentCategory);
			return null;
		}

		Category category = categoryRepository.findByParentCategoryIdAndName(parentCategory.getId(), keyword);
		if (category != null) {
//			log.info("<<< hit origincode: {}/ newCategoryName: {}, keyword: {} ", originalSectorCode, parentCategory.getName(), keyword);
			return category;
		}

		category = new Category();
		category.setParentCategory(parentCategory);
		category.setName(keyword);
//		log.info("<<< new origincode: {}/ newCategoryName: {}, keyword: {} ", originalSectorCode, parentCategory.getName(), keyword);
		return categoryRepository.save(category);
	}
	
	public enum BusinessType {
		INDIVIDUAL("개인"), INDIV_BUSINESS("개인 사업자"), CORP_BUSINESS("법인 사업자");

		final private String name;

		private BusinessType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}
