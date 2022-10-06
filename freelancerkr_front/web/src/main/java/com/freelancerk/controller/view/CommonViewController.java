package com.freelancerk.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.Inquiry;
import com.freelancerk.domain.Notice;
import com.freelancerk.domain.Reference;
import com.freelancerk.domain.ReferenceFile;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.InquiryAnswerRepository;
import com.freelancerk.domain.repository.InquiryRepository;
import com.freelancerk.domain.repository.LalaFreelancerRepository;
import com.freelancerk.domain.repository.NoticeRepository;
import com.freelancerk.domain.repository.ReferenceRepository;
import com.freelancerk.service.CommonService;
import com.freelancerk.service.UserService;
import com.freelancerk.vo.TotalPagesInfoVO;

import lombok.extern.java.Log;

@Log
@Controller
@RequestMapping("/common")
public class CommonViewController extends RootController {

	private UserService userService;
	private CommonService commonService;
	private NoticeRepository noticeRepository;
	private InquiryRepository inquiryRepository;
	private ReferenceRepository referenceRepository;
	private InquiryAnswerRepository inquiryAnswerRepository;
	private LalaFreelancerRepository lalaFreelancerRepository;

	@Autowired
	public CommonViewController(UserService userService, CommonService commonService,
								NoticeRepository noticeRepository, InquiryRepository inquiryRepository,
								ReferenceRepository referenceRepository, InquiryAnswerRepository inquiryAnswerRepository,
								LalaFreelancerRepository lalaFreelancerRepository) {
		this.userService = userService;
		this.commonService = commonService;
		this.noticeRepository = noticeRepository;
		this.inquiryRepository = inquiryRepository;
		this.referenceRepository = referenceRepository;
		this.inquiryAnswerRepository = inquiryAnswerRepository;
		this.lalaFreelancerRepository = lalaFreelancerRepository;
	}

	@GetMapping("/notice")
	public String notice(@RequestParam(value = "currentPageNumber", defaultValue = "0") int currentPageNumber,
			@RequestParam(value = "noticeId", required = false) Long noticeId,
			@RequestParam(value = "searchWord", defaultValue = "", required = false) String searchWord, Model model) {

		if (noticeId != null) {
			commonService.updateNoticeHitsCount(noticeId); // hit 수 증가
			Notice notice = commonService.getThisNotice(noticeId);
			model.addAttribute("notice", notice);
			return "common/noticeDetail";
		}

		model.addAttribute("totalCount", noticeRepository.count());
		model.addAttribute("totalReference", referenceRepository.count());
		model.addAttribute("list", noticeRepository.findByHiddenFalseOrderByIdDesc());

		return "common/notice";
	}

	@GetMapping("/reference")
	public String reference(@RequestParam(value = "currentPageNumber", defaultValue = "0") int currentPageNumber,
			@RequestParam(value = "referenceId", required = false) Long referenceId,
			@RequestParam(value = "searchWord", defaultValue = "", required = false) String searchWord, Model model) {

		if (referenceId != null) {
			commonService.updateReferenceHitsCount(referenceId); // hit 수 증가
			Reference reference = commonService.getThisReference(referenceId);
			List<ReferenceFile> referenceFiles = commonService.getThisReferenceFiles(referenceId);
			model.addAttribute("reference", reference);
			model.addAttribute("referenceFiles", referenceFiles);
			return "common/referenceDetail";
		}

		Page<Reference> referencePage = commonService.getReferencePages(searchWord, currentPageNumber);
		TotalPagesInfoVO totalPageInfo = commonService.getReferencePagesInfo(searchWord);

		model.addAttribute("referencePage", referencePage);
		model.addAttribute("totalPageInfo", totalPageInfo);
		model.addAttribute("totalNotice", commonService.getNoticePagesInfo("").getCountOfTotalElements());
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("currentPageNumber", currentPageNumber);

		return "common/reference";
	}

	// 이용안내
	@GetMapping("/howItWorks")
	public String howItWorks(@RequestParam(value = "role", required = false) User.Role role,
							 @RequestParam(value = "openModal", required = false) String openModal, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("loggedIn", authentication instanceof User);
		model.addAttribute("openModal", openModal);
		if (authentication instanceof User) {
			model.addAttribute("loginAsClient", ((User) authentication).isLoginAsClient());
		}
		if (User.Role.ROLE_CLIENT.equals(role)) {
			return "common/howitworksClient";
		}
		return "common/howitworksFreelancer";

	}

	// 문의하기
	@GetMapping("/askForHelp")
	public String askForHelp(Model model) {
		model.addAttribute("inquiry", new Inquiry());
		return "common/askForHelp";
	}

	@PostMapping("/askForHelp")
	public RedirectView askForHelp(@ModelAttribute final Inquiry inquiry) {
		inquiry.setUser(userService.getCurrentUser());
		inquiry.setUserRole(isLoggedIsAsClient()? User.Role.ROLE_CLIENT: User.Role.ROLE_FREELANCER);
		commonService.insertInquiry(inquiry, Inquiry.Status.REGISTERED);
		return new RedirectView("helpList");
	}

	@GetMapping("/helpList")
	public String helpList(
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
			Model model) {

		Page<Inquiry> page = inquiryRepository.findByUserId(getSessionUserId(), PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
		setPaginationModelData(model, pageNumber, page);

		return "common/helpList";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/inquiries/{inquiryId}/details")
	public String getInquiryDetailView(@PathVariable("inquiryId") Long inquiryId, Model model) {
		Inquiry item = inquiryRepository.getOne(inquiryId);
		item.setAnswers(inquiryAnswerRepository.findByInquiryId(inquiryId));
		model.addAttribute("inquiry", item);

		return "common/helpListDetail";
	}

	// 에스크로 이용약관
	@GetMapping("/escrowPolicy")
	public String escrowPolicy() {
		return "common/escrowPolicy";
	}

	// 개인정보취급방법
	@GetMapping("/privateInfoPolicy")
	public String privateInfoPolicy() {
		return "common/privateInfoPolicy";
	}
	
	// 이용약관
	@GetMapping("/usagePolicy")
	public String usagePolicy() {
		return "common/usagePolicy";
	}

	@GetMapping("/blog/{id}")
	public String blog(Model model, @PathVariable long id) {

		model.addAttribute("item", noticeRepository.getOne(id));

		return "common/blog";
	}
}
