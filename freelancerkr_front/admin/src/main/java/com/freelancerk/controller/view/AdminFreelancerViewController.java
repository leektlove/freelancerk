package com.freelancerk.controller.view;

import com.freelancerk.controller.BaseController;
import com.freelancerk.domain.Apply;
import com.freelancerk.domain.Audition;
import com.freelancerk.domain.Category;
import com.freelancerk.domain.DailyAccessLog;
import com.freelancerk.domain.PaymentToUser;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.DailyAccessLogRepository;
import com.freelancerk.domain.repository.PaymentToUserRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.domain.specification.UserSpecifications;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

@RequestMapping(value = "/freelancer")
@Controller
public class AdminFreelancerViewController extends BaseController {

    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private PaymentToUserRepository paymentToUserRepository;
    private DailyAccessLogRepository dailyAccessLogRepository;

    @Autowired
    public AdminFreelancerViewController(UserRepository userRepository, CategoryRepository categoryRepository,
                                         PaymentToUserRepository paymentToUserRepository,
                                         DailyAccessLogRepository dailyAccessLogRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.paymentToUserRepository = paymentToUserRepository;
        this.dailyAccessLogRepository = dailyAccessLogRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/list", "/"})
    public String getAdminFreelancerList(Model model,
                                         @RequestParam(value = "keyword", required = false) String keyword,
                                         @RequestParam(value = "parentCategoryId", required = false) Long parentCategoryId,
                                         @RequestParam(value = "categoryId", required = false) Long categoryId,
                                         @RequestParam(value = "keywordName", required = false) String keywordName,
                                         @RequestParam(value = "categoryKeyword", required = false) String categoryKeyword,
                                         @RequestParam(value = "fpUser", required = false) String fpUser,
                                         @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                         @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
        if (parentCategoryId != null && StringUtils.isNotEmpty(keywordName)) {
            Category category = categoryRepository.findByParentCategoryIdAndName(parentCategoryId, keywordName);
            categoryId = category != null?category.getId():null;
            model.addAttribute("category", category);
        } else if (categoryId != null) {
            model.addAttribute("category", categoryRepository.getOne(categoryId));
        }

        Page<User> page = userRepository.findAll(UserSpecifications.filterForFreelancer(keyword, categoryId, categoryKeyword, fpUser, true), PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

        for (User item: page) {
            DailyAccessLog dailyAccessLog = dailyAccessLogRepository.findTop1ByUserIdOrderByIdDesc(item.getId());
            if (dailyAccessLog != null) {
                item.setLastAccessAt(dailyAccessLog.getCreatedAt());
            }
            item.setTotalTransactionAmount(paymentToUserRepository.findByUserIdAndStatus(item.getId(), PaymentToUser.Status.PAYED).stream().map(PaymentToUser::getAmount).mapToLong(Long::longValue).sum());
            item.setTotalTransactionCount(paymentToUserRepository.findByUserIdAndStatus(item.getId(), PaymentToUser.Status.PAYED).size());
        }

        model.addAttribute("page", page);
        setPaginationModelData(model, pageNumber, page);
        return "freelancer/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/excel/download")
    public ResponseEntity<InputStreamResource> getExcelDownload(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "parentCategoryId", required = false) Long parentCategoryId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "categoryKeyword", required = false) String categoryKeyword,
            @RequestParam(value = "keywordName", required = false) String keywordName) throws UnsupportedEncodingException {
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
            categoryId = category != null?category.getId():null;
        }
        List<User> list = userRepository.findAll(UserSpecifications.filterForFreelancer(keyword, categoryId, categoryKeyword, false));

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
                if(workbook!=null) workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(String.format("FRK_키워드별유저목록_%s.xls", LocalDate.now()), "UTF-8"))
                .header(HttpHeaders.SET_COOKIE, "fileDownload=true; path=/")
                .body(resource);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/export/excel")
    public ResponseEntity<InputStreamResource> exportToExcel() throws UnsupportedEncodingException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HSSFSheet sheet = workbook.createSheet();

        List<User> dataList= userRepository.findAll(UserSpecifications.filterForFreelancer(null, null, null, false));

        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(1).setCellValue("이름");
        headerRow.createCell(2).setCellValue("회원가입일");
        headerRow.createCell(3).setCellValue("최근로그인 날짜");
        headerRow.createCell(4).setCellValue("전체 방문 수");

        int initialRowIndex = 1;
        for (User item: dataList) {
            if (item.getCreatedAt() != null && item.getCreatedAt().isBefore(LocalDate.of(2019, 1, 1).atStartOfDay())) continue;
            if (item.getId() < 13312L) continue;

            HSSFRow row = sheet.createRow(initialRowIndex++);
            row.createCell(1).setCellValue(item.getName());
            row.createCell(2).setCellValue(String.format("%s", item.getCreatedAt() != null?item.getCreatedAt().toLocalDate():""));
            row.createCell(3).setCellValue(String.format("%s", item.getLastAccessAt() != null?item.getLastAccessAt().toLocalDate():""));
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
                if(workbook!=null) workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(String.format("프리랜서코리아프리랜서유저목록_%s.xls", LocalDate.now()), "UTF-8"))
                .body(resource);
    }

    
    @RequestMapping(method = RequestMethod.GET, value = "/detail/{id}")
    public String getAdminFreelancerDetailsView(Model model,
    						@PathVariable("id") String id) {
    	User user = userRepository.findByUid(id);
    	
    	model.addAttribute("user", user);

    	return "freelancer/detail";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/add")
    public String getAdminFreelancerAddView() {

        return "freelancer/freelancerAdd";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/mailList")
    public String getAdminFreelancerMailListView() {

        return "freelancer/mailList";
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

}
