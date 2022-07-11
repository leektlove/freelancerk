package com.freelancerk.controller.view;

import com.freelancerk.controller.BaseController;
import com.freelancerk.domain.*;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping(value = "/client")
@Controller
public class AdminClientViewController extends BaseController {

    private UserRepository userRepository;
    private PaymentToUserRepository paymentToUserRepository;
    private DailyAccessLogRepository dailyAccessLogRepository;

    @Autowired
    public AdminClientViewController(UserRepository userRepository, PaymentToUserRepository paymentToUserRepository,
                                     DailyAccessLogRepository dailyAccessLogRepository) {
        this.userRepository = userRepository;
        this.paymentToUserRepository = paymentToUserRepository;
        this.dailyAccessLogRepository = dailyAccessLogRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/list", "/"})
    public String getAdminClientList(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                     @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {

        Page<User> page = userRepository.findAll(UserSpecifications.filterForClient(keyword, true), PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        for (User item: page) {
            DailyAccessLog dailyAccessLog = dailyAccessLogRepository.findTop1ByUserIdOrderByIdDesc(item.getId());
            if (dailyAccessLog != null) {
                item.setLastAccessAt(dailyAccessLog.getCreatedAt());
            }
            item.setTotalTransactionAmount(paymentToUserRepository.findByProjectPostingClientIdAndStatus(item.getId(), PaymentToUser.Status.PAYED).stream().map(PaymentToUser::getAmount).mapToLong(Long::longValue).sum());
            item.setTotalTransactionCount(paymentToUserRepository.findByProjectPostingClientIdAndStatus(item.getId(), PaymentToUser.Status.PAYED).size());
        }

        model.addAttribute("page", page);
        setPaginationModelData(model, pageNumber, page);

        return "client/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/export/excel")
    public ResponseEntity<InputStreamResource> exportToExcel() throws UnsupportedEncodingException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HSSFSheet sheet = workbook.createSheet();

        List<User> dataList= userRepository.findAll(UserSpecifications.filterForClient(null, false));

        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(1).setCellValue("이름");
        headerRow.createCell(2).setCellValue("회원가입일");
        headerRow.createCell(3).setCellValue("최근로그인 날짜");
        headerRow.createCell(4).setCellValue("전체 방문 수");

        int initialRowIndex = 1;
        for (User item: dataList) {
            if (item.getCreatedAt() != null && item.getCreatedAt().isBefore(LocalDate.of(2019, 1, 1).atStartOfDay())) continue;

            if (item.getId() < 1864L) continue;
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(String.format("프리랜서코리아클라이언트유저목록_%s.xls", LocalDate.now()), "UTF-8"))
                .body(resource);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list/{id}")
    public String getAdminClientDetails() {

        return "client/detail";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/add")
    public String getClientAddView() {

        return "client/clientAdd";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/mailList")
    public String getClientMailListView() {

        return "client/mailList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/mailList/{id}")
    public String getClientMailDetailView() {

        return "client/mailDetail";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/smsList")
    public String getClientSmsList() {

        return "client/smsList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/smsList/{id}")
    public String getSmsDetailView() {

        return "client/smsDetail";
    }
}
