package com.freelancerk.controller.view;

import com.freelancerk.FileUtil;
import com.freelancerk.controller.BaseController;
import com.freelancerk.controller.io.JqueryUploaderResponse;
import com.freelancerk.controller.io.JqueryUploaderResponseItem;
import com.freelancerk.domain.AdminUser;
import com.freelancerk.domain.Apply;
import com.freelancerk.domain.Audition;
import com.freelancerk.domain.Category;
import com.freelancerk.domain.ContestEntryFile;
import com.freelancerk.domain.DailyAccessLog;
import com.freelancerk.domain.Notice;
import com.freelancerk.domain.PaymentToUser;
import com.freelancerk.domain.ReferenceFile;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.ApplyRepository;
import com.freelancerk.domain.repository.AuditionRepository;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.DailyAccessLogRepository;
import com.freelancerk.domain.repository.PaymentToUserRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.domain.specification.AuditionSpecifications;
import com.freelancerk.domain.specification.UserSpecifications;
import com.freelancerk.gateway.EmailService;
import com.freelancerk.service.StorageService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.bytebuddy.dynamic.scaffold.MethodRegistry.Handler.ForImplementation;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequestMapping(value = "/audition")
@Controller
public class AdminAuditionViewController extends BaseController {

	private StorageService storageService;
	private UserRepository userRepository;
	private AuditionRepository auditionRepository;
	private ApplyRepository applyRepository;
	private CategoryRepository categoryRepository;
	private PaymentToUserRepository paymentToUserRepository;
	private DailyAccessLogRepository dailyAccessLogRepository;
    private EmailService emailService;
    private TemplateEngine templateEngine;

	@Autowired
	public AdminAuditionViewController(UserRepository userRepository, CategoryRepository categoryRepository,
			PaymentToUserRepository paymentToUserRepository, AuditionRepository auditionRepository,
			ApplyRepository applyRepository, StorageService storageService,
			DailyAccessLogRepository dailyAccessLogRepository, TemplateEngine templateEngine, EmailService emailService) {
		this.userRepository = userRepository;
		this.auditionRepository = auditionRepository;
		this.categoryRepository = categoryRepository;
		this.applyRepository = applyRepository;
		this.paymentToUserRepository = paymentToUserRepository;
		this.dailyAccessLogRepository = dailyAccessLogRepository;
		this.templateEngine = templateEngine;
        this.storageService = storageService;
        this.emailService = emailService;
	}

	@RequestMapping(method = RequestMethod.GET, value = { "/list", "/" })
	public String getAdminAuditionList(Model model, @RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "parentCategoryId", required = false) Long parentCategoryId,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "keywordName", required = false) String keywordName,
			@RequestParam(value = "categoryKeyword", required = false) String categoryKeyword,
			@RequestParam(value = "fpUser", required = false) String fpUser,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
		
		if (parentCategoryId != null && StringUtils.isNotEmpty(keywordName)) {
			Category category = categoryRepository.findByParentCategoryIdAndName(parentCategoryId, keywordName);
			categoryId = category != null ? category.getId() : null;
			model.addAttribute("category", category);
		} else if (categoryId != null) {
			model.addAttribute("category", categoryRepository.getOne(categoryId));
		}

//        Page<User> page2 = userRepository.findAll(UserSpecifications.filterForFreelancer(keyword, categoryId, categoryKeyword, fpUser, true), PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
		Page<Audition> page = auditionRepository.findAll(AuditionSpecifications.filterForAudition(),
				PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

		model.addAttribute("page", page);
		setPaginationModelData(model, pageNumber, page);
		return "audition/list";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/excel/download")
	public ResponseEntity<InputStreamResource> getExcelDownload(
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "parentCategoryId", required = false) Long parentCategoryId,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "categoryKeyword", required = false) String categoryKeyword,
			@RequestParam(value = "keywordName", required = false) String keywordName)
			throws UnsupportedEncodingException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		HSSFSheet sheet = workbook.createSheet();
		HSSFRow headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("식별자");
		headerRow.createCell(1).setCellValue("이름");
		headerRow.createCell(2).setCellValue("이메일");
		headerRow.createCell(3).setCellValue("휴대전화번호");
		headerRow.createCell(4).setCellValue("이메일 수신 여부");
		headerRow.createCell(5).setCellValue("탈퇴여부");

		if (parentCategoryId != null && StringUtils.isNotEmpty(keywordName)) {
			Category category = categoryRepository.findByParentCategoryIdAndName(parentCategoryId, keywordName);
			categoryId = category != null ? category.getId() : null;
		}
		List<User> list = userRepository
				.findAll(UserSpecifications.filterForFreelancer(keyword, categoryId, categoryKeyword, false));

		int initialRowIndex = 1;
		for (User item : list) {

			HSSFRow row = sheet.createRow(initialRowIndex++);
			row.createCell(0).setCellValue(item.getId());
			row.createCell(1).setCellValue(item.getName());
			row.createCell(2).setCellValue(item.getEmail());
			row.createCell(3).setCellValue(item.getCellphone());
			row.createCell(4).setCellValue(item.isReceiveEmail());
			row.createCell(5).setCellValue(item.isLeaved() ? "탈퇴" : "정상가입");
		}

		try {
			workbook.write(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null)
					workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment;filename="
								+ URLEncoder.encode(String.format("FRK_키워드별유저목록_%s.xls", LocalDate.now()), "UTF-8"))
				.header(HttpHeaders.SET_COOKIE, "fileDownload=true; path=/").body(resource);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/export/excel")
	public ResponseEntity<InputStreamResource> exportToExcel() throws UnsupportedEncodingException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HSSFSheet sheet = workbook.createSheet();

		List<User> dataList = userRepository.findAll(UserSpecifications.filterForFreelancer(null, null, null, false));

		HSSFRow headerRow = sheet.createRow(0);
		headerRow.createCell(1).setCellValue("이름");
		headerRow.createCell(2).setCellValue("회원가입일");
		headerRow.createCell(3).setCellValue("최근로그인 날짜");
		headerRow.createCell(4).setCellValue("전체 방문 수");

		int initialRowIndex = 1;
		for (User item : dataList) {
			if (item.getCreatedAt() != null && item.getCreatedAt().isBefore(LocalDate.of(2019, 1, 1).atStartOfDay()))
				continue;
			if (item.getId() < 13312L)
				continue;

			HSSFRow row = sheet.createRow(initialRowIndex++);
			row.createCell(1).setCellValue(item.getName());
			row.createCell(2).setCellValue(
					String.format("%s", item.getCreatedAt() != null ? item.getCreatedAt().toLocalDate() : ""));
			row.createCell(3).setCellValue(
					String.format("%s", item.getLastAccessAt() != null ? item.getLastAccessAt().toLocalDate() : ""));
			row.createCell(4).setCellValue(String.valueOf(dailyAccessLogRepository.countByUserId(item.getId())));
		}

		try {
			workbook.write(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null)
					workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment;filename="
								+ URLEncoder.encode(String.format("프리랜서코리아프리랜서유저목록_%s.xls", LocalDate.now()), "UTF-8"))
				.body(resource);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/detail/{id}")
	public String getAdminAuditionDetailsView(Model model, @PathVariable("id") String id) {
		Audition audition = auditionRepository.findByAuditionId(id);
		List<Apply> list = applyRepository.findByWork(id);

		model.addAttribute("audition", audition);
		model.addAttribute("list", list);

		return "audition/detail";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/add")
	public String getAdminAuditionAddView() {

		return "audition/auditionAdd";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/save")
	public String auditionSave(Model model, @RequestParam(value = "auditionId", required = false) String auditionId,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "product", required = false) String product,
			@RequestParam(value = "adress", required = false) String adress,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "text", required = false) String text,
			@RequestParam(value = "auditionAt", required = false) String auditionAt,
			@RequestParam("file") MultipartFile file) throws IOException {
		
		Audition audition = new Audition();
		
		if (auditionId != null && !"".equals(auditionId)) {
			audition = auditionRepository.findByAuditionId(auditionId);
		} else {
			String uuid = UUID.randomUUID().toString();
			uuid = uuid.replace("-", "");

			audition.setAuditionId(uuid);
		}
		if (name != null && !"".equals(name)) {
			audition.setName(name);
		}
		if (product != null && !"".equals(product)) {
			audition.setProduct(product);
		}
		if (adress != null && !"".equals(adress)) {
			audition.setAdress(adress);
		}
		if (text != null && !"".equals(text)) {
			audition.setText(text);
		}
		if (status != null) {
			audition.setStatus(status);
		}
		if (auditionAt != null) {
			audition.setAuditionAt(LocalDateTime.of(Integer.parseInt(auditionAt.substring(0, 4)),
					Integer.parseInt(auditionAt.substring(5, 7)), Integer.parseInt(auditionAt.substring(8, 10)),
					Integer.parseInt(auditionAt.substring(11, 13)), Integer.parseInt(auditionAt.substring(14, 16))));
		}

		String fileName = storageService.storeFile(file);
		if (fileName.length() > 89)
			audition.setImageUrl(fileName);

		auditionRepository.save(audition);

		return "redirect:/audition/list";
	}

	@ResponseBody
	@RequestMapping(value = "/getApplyList", method = RequestMethod.POST)
	public Apply getApplyList(@RequestBody Apply apply, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		List<Apply> applyList = new ArrayList<Apply>();

		String num = apply.getName();
		String id = apply.getWork();
		
		if (num.equals("0")) { // 전체보기
			applyList = applyRepository.findByWork(id);
		} else if (num.equals("-1")) { // 미합격자
			applyList = applyRepository.findByWorkAndPass1(id, "N");
		} else if (num.equals("1")) { // 1차합격자
			applyList = applyRepository.findByWorkAndPass1(id, "Y");
		} else if (num.equals("2")) { // 2차합격자
			applyList = applyRepository.findByWorkAndPass2(id, "Y");
		} else if (num.equals("3")) { // 3차합격자
			applyList = applyRepository.findByWorkAndPass3(id, "Y");
		}

		for (Apply apply2 : applyList) {
			apply2.setDateTime(apply2.getCreatedAt().toString());
		}

		apply.setList(applyList);

		return apply;
	}

	@ResponseBody
	@RequestMapping(value = "/passApply", method = RequestMethod.GET)
	public String passApply(Model model, HttpServletRequest request, HttpServletResponse response) {

		String num = request.getParameter("num");
		String[] array = request.getParameterValues("array");

		/*
		 * 현재 선택된 카테고리 = num -1 : 미합격자 = 1차합격 1 : 1차합격자 = 2차합격 2 : 2차합격자 = 3차합격
		 */
		Apply apply = null;
		if ("-1".equals(num)) { // 1차합격
			for (int i = 0; i < array.length; i++) {
				Long id = Long.parseLong(array[i]);
				apply = applyRepository.findById(id).get();
				apply.setPass1("Y");
				applyRepository.save(apply);
			}
		} else if ("1".equals(num)) { // 2차합격
			for (int i = 0; i < array.length; i++) {
				Long id = Long.parseLong(array[i]);
				apply = applyRepository.findById(id).get();
				apply.setPass2("Y");
				applyRepository.save(apply);
			}
		} else if ("2".equals(num)) { // 3차합격
			for (int i = 0; i < array.length; i++) {
				Long id = Long.parseLong(array[i]);
				apply = applyRepository.findById(id).get();
				apply.setPass3("Y");
				applyRepository.save(apply);
			}
		}

		return "200";

	}

	@ResponseBody
	@RequestMapping(value = "/failApply", method = RequestMethod.GET)
	public String failApply(Model model, HttpServletRequest request, HttpServletResponse response) {

		String num = request.getParameter("num");
		String[] array = request.getParameterValues("array");

		/*
		 * 현재 선택된 카테고리 = num 1 : 1차합격자 = 1차합격취소 2 : 2차합격자 = 2차합격취소 3 : 3차합격자 = 3차합격취소
		 */
		Apply apply = null;
		if ("1".equals(num)) { // 1차합격
			for (int i = 0; i < array.length; i++) {
				Long id = Long.parseLong(array[i]);
				apply = applyRepository.findById(id).get();
				apply.setPass1("N");
				applyRepository.save(apply);
			}
		} else if ("2".equals(num)) { // 2차합격
			for (int i = 0; i < array.length; i++) {
				Long id = Long.parseLong(array[i]);
				apply = applyRepository.findById(id).get();
				apply.setPass2("N");
				applyRepository.save(apply);
			}
		} else if ("3".equals(num)) { // 3차합격
			for (int i = 0; i < array.length; i++) {
				Long id = Long.parseLong(array[i]);
				apply = applyRepository.findById(id).get();
				apply.setPass3("N");
				applyRepository.save(apply);
			}
		}

		return "200";

	}
	
	@RequestMapping(value = "/mailAddAjax", method = RequestMethod.POST)
	public String mailAddViewAjax(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "array[]", required = false) String[] array) {
		
		model.addAttribute("item", new Notice());
		model.addAttribute("array",array);
		
		return "audition/form";
	}
 
	@RequestMapping(value = "/sendMail")
	public String sendMail(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "ir1", required = false) String ir1,
			@RequestParam(value = "array[]", required = false) String[] array ) {
		
		
		if(array.length>0) {
			for (int i = 0; i < array.length; i++) {
				try {
		            if (StringUtils.isNotEmpty(array[i])) {
		        		emailService.sendEmail(array[i], title, makeEmailForm(ir1));
		            }
		        } catch (Exception e) {
		        	
		        }
			}
		}
		
		return "redirect:/audition/list";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/sendMailTest")
	public String sendMailTest(Model model, HttpServletRequest request, HttpServletResponse response) {
		String email="junhyuk11225@gmail.com";
		String name = "박준혁";
		
		try {
            if (StringUtils.isNotEmpty(email)) {
                String a = "<p><b><span style=\"font-size: 24pt;\">안녕하세요</span></b></p><p><span style=\"font-size: 32px;\"><b>제일ㅇ니린<span style=\"color: rgb(255, 0, 0);\"><sub>아리ㅏㄴㅇ링ㄴ라ㅣㅇㄴㄹ</sub></span></b></span></p>";
                emailService.sendEmail(email, "테스트입니다", makeEmailForm(a));
            }
            
        } catch (Exception e) {
        	
        }
	  
		return null;
	}

	
	@RequestMapping(method = RequestMethod.GET, value = "/mailAdd")
	public String getAdminAuditionMailAddView(Model model) {

        model.addAttribute("item", new Notice());
	        
		return "audition/form";
	}
	

	@RequestMapping(method = RequestMethod.GET, value = "/mailList")
	public String getAdminFreelancerMailListView() {

		return "audition/mailList";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/mailList/{id}")
	public String getAdminFreelancerMailDetailView() {

		return "freelancer/mailDetail";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/smsList")
	public String getAdminFreelancerSmsList() {

		return "freelancer/smsList";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/smsList/{id}")
	public String getSmsListDetailView() {

		return "freelancer/smsDetail";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/files/for-jquery-upload") // todo token
	public JqueryUploaderResponse insertImagesForJqueryUploader(
			@RequestParam(value = "files[]", required = false) MultipartFile[] files,
			MultipartHttpServletRequest request) {
		JqueryUploaderResponse response = new JqueryUploaderResponse();
		JqueryUploaderResponseItem item = new JqueryUploaderResponseItem();
		List<JqueryUploaderResponseItem> items = new ArrayList<>();
		for (MultipartFile file : files) {
			String url = file != null ? storageService.storeFile(file)
					: storageService.storeFile(request.getFileMap().values().iterator().next());
			item.setUrl(url);
			if (ContestEntryFile.Type.VIDEO.equals(FileUtil.getFileType(url))) {
				item.setVideoUrl(url);
			}
			item.setThumbnailUrl(url);
			item.setSize((int) file.getSize());
			item.setDeleteType("DELETE");
			item.setDeleteType(url);
			item.setName(file.getOriginalFilename());
			items.add(item);
		}
		response.setFiles(items);
		return response;
	}
	
	 private String makeSignUpEmailContent(String userName, String userEmail, User.Role role) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("username", userName);
        ctx.setVariable("userEmail", userEmail);

        return this.templateEngine.process(User.Role.ROLE_CLIENT.equals(role)?"email/signup-client":"email/signup-freelancer", ctx);
    }
	private String makeEmailForm(String content) {
		String html = "<!DOCTYPE html>\n"
					+ "<html>\n"
					+ "<head>\n"
					+ "    <meta charset=\"utf-8\"/>\n"
					+ "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/>\n"
					+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0,shrink-to-fit=no, maximum-scale=1, user-scalable=no\">\n"
					+ "    <title>프리랜서코리아 이메일</title>\n"
					+ "</head>\n"
					+ "<body>\n"
					+ "<div class=\"email-container\" style=\"padding: 2rem 1rem;max-width: 700px;margin: auto;\">\n"
					+ "\n"
					+ "    <!-- 헤더 -->\n"
					+ "    <div class=\"header\" style=\"display: block;padding-bottom:10px;border-bottom: 1px solid #ddd;\">\n"
					+ "        <img src=\"https://s3.ap-northeast-2.amazonaws.com/freelancerk/97ec2dad-c867-4e19-bd9b-a94f709ae4c2.png\" style=\"max-width: 150px;\">\n"
					+ "    </div>\n"
					+ "    <div style=\"display: block;clear: both;\"></div>\n"
					+ "    <!-- 헤더//-->\n"
					+ "\n"
					+ "    <!-- 본문 -->\n"
					+ "    <div class=\"discription\" style=\"font-size: 15px;border-bottom: 1px solid #ddd;padding-top: 2rem;padding-bottom: 2rem;margin-bottom: 2.5rem;line-height: 1.5rem;\">\n"
					+ content
					+ "    </div>\n"
					+ "    <div class=\"action_btn\" style=\"margin-top:1.5rem;text-align: center;\">\n"
					+ "    <a class=\"btn\" href=\"https://www.freelancerk.com/fplus\" target=\"_blank\" style=\"padding: 0.7rem 1rem;border: 0px currentColor;color: rgb(255,255,255);font-size: 18px;font-weight: 700;max-width: 300px;background-color: #222;text-decoration: none;display: block;margin: auto;\">\n"
					+ "    	F+PLUS 바로가기\n"
					+ "    </a>\n"
					+ "</div>\n"
					+ "    <!-- 본문//-->\n"
					+ "\n"
					+ "    <!-- 푸터 -->\n"
					+ "<div class=\"footer\">\n"
					+ "	<div class=\"footer_wrap\" style=\"margin-top: 3rem;margin-bottom: 3rem;\">\n"
					+ "	    <div>\n"
					+ "	        감사합니다.<br/>F+PLUS팀 드림.\n"
					+ "	    </div>\n"
					+ "	    <div>\n"
					+ "	        (주)플랫폼위즈컴퍼니 | 1599-2118(10:00~17:00) | 서울특별시 송파구 백제고분로 202, 오케이202빌딩 6층 | Copyright© 플랫폼위즈컴퍼니 All Rights Reserved | 이 이메일은 회원분의 사전 동의를 얻어 발송되었습니다. 수신거부를 원하시는 분께서는 본인 프로필의 수신 여부를 확인해주시기 바랍니다.\n"
					+ "	    </div>\n"
					+ "	</div>\n"
					+ "</div>\n"
					+ "<!-- 푸터//-->\n"
					+ "\n"
					+ "</div>\n"
					+ "\n"
					+ "\n"
					+ "</body>\n"
					+ "</html>";
		
		return html;
	}

	@RequestMapping(value="/uploadSummernoteImageFile", produces = "application/json; charset=utf8")
	@ResponseBody
	public Audition uploadSummernoteImageFile(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request )  {
		Audition audition = new Audition();
		audition.setImageUrl(storageService.storeFile(multipartFile));
		return audition;
	}
}
