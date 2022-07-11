package com.freelancerk.controller.view;

import com.freelancerk.TimeUtil;
import com.freelancerk.controller.BaseController;
import com.freelancerk.domain.DailyAccessLog;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.DailyAccessLogRepository;
import com.freelancerk.domain.specification.DailyAccessLogSpecifications;
import com.freelancerk.domain.specification.UserSpecifications;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminAccessLogViewController extends BaseController {

    private DailyAccessLogRepository dailyAccessLogRepository;

    @Autowired
    public AdminAccessLogViewController(DailyAccessLogRepository dailyAccessLogRepository) {
        this.dailyAccessLogRepository = dailyAccessLogRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/accessLogs")
    public String getAccessLogs(
            Model model,
            @RequestParam(value = "accessAtFrom", required = false) String accessAtFrom,
            @RequestParam(value = "accessAtTo", required = false) String accessAtTo,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize) {

        Page<DailyAccessLog> page = dailyAccessLogRepository.findAll(DailyAccessLogSpecifications.filter(
                TimeUtil.convertStrToLocalDate(accessAtFrom),
                TimeUtil.convertStrToLocalDate(accessAtTo)),
                PageRequest.of(pageNumber, pageSize));

        setPaginationModelData(model, pageNumber, page);

        return "accessLog/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/accessLogs/export/excel")
    public ResponseEntity<InputStreamResource> exportToExcel(@RequestParam("from") String from, @RequestParam("to") String to) throws UnsupportedEncodingException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HSSFSheet sheet = workbook.createSheet();

        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(1).setCellValue("일자");
        headerRow.createCell(2).setCellValue("전체 방문 수");

        List<List<Object>> accessDaily = new ArrayList<>();

        LocalDate dateFrom = StringUtils.isEmpty(from)?LocalDate.now().minusDays(15): TimeUtil.convertStrToLocalDate(from);
        LocalDate dateTo = StringUtils.isEmpty(to)?LocalDate.now(): TimeUtil.convertStrToLocalDate(to);
        dateTo = dateTo.plusDays(1);

        int initialRowIndex = 1;
        while (dateFrom.isBefore(dateTo)) {
            HSSFRow row = sheet.createRow(initialRowIndex++);
            row.createCell(1).setCellValue(String.format("%02d-%02d" ,dateFrom.getMonth().getValue(), dateFrom.getDayOfMonth()));
            row.createCell(2).setCellValue(String.valueOf(dailyAccessLogRepository.countByDate(dateFrom)));
            dateFrom = dateFrom.plusDays(1);
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(String.format("프리랜서트래픽추이_%s.xls", LocalDate.now()), "UTF-8"))
                .body(resource);
    }
}
