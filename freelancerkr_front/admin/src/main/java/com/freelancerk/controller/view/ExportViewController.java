package com.freelancerk.controller.view;

import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.domain.specification.ProjectSpecifications;
import com.freelancerk.domain.specification.UserSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class ExportViewController {

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private PurchaseRepository purchaseRepository;
    private DirectDealRepository directDealRepository;
    private DailyAccessLogRepository dailyAccessLogRepository;
    private PickMeUpTicketLogRepository pickMeUpTicketLogRepository;
    private ProjectPropositionRepository projectPropositionRepository;
    private ProjectItemTicketLogRepository projectItemTicketLogRepository;
    private ContestEntryTicketLogRepository contestEntryTicketLogRepository;

    @Autowired
    public ExportViewController(UserRepository userRepository, ProjectRepository projectRepository, PurchaseRepository purchaseRepository,
                                DirectDealRepository directDealRepository, DailyAccessLogRepository dailyAccessLogRepository,
                                PickMeUpTicketLogRepository pickMeUpTicketLogRepository,
                                ProjectPropositionRepository projectPropositionRepository, ProjectItemTicketLogRepository projectItemTicketLogRepository,
                                ContestEntryTicketLogRepository contestEntryTicketLogRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.purchaseRepository = purchaseRepository;
        this.directDealRepository = directDealRepository;
        this.dailyAccessLogRepository = dailyAccessLogRepository;
        this.pickMeUpTicketLogRepository = pickMeUpTicketLogRepository;
        this.projectPropositionRepository = projectPropositionRepository;
        this.projectItemTicketLogRepository = projectItemTicketLogRepository;
        this.contestEntryTicketLogRepository = contestEntryTicketLogRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/export/excel/project")
    public ResponseEntity<InputStreamResource> exportToExcelProjectMetric() throws UnsupportedEncodingException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HSSFSheet sheet = workbook.createSheet();

        List<Project> dataList= projectRepository.findAll(ProjectSpecifications.filter(
                null, null, null,
                Arrays.asList(Project.Status.POSTED, Project.Status.IN_PROGRESS, Project.Status.COMPLETED, Project.Status.CANCELLED, Project.Status.TEMP), null),
                Sort.by(Sort.Direction.ASC, "id"));

        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("no");
        headerRow.createCell(1).setCellValue("title");
        headerRow.createCell(2).setCellValue("created_at");
        headerRow.createCell(3).setCellValue("status");
        headerRow.createCell(4).setCellValue("name");
        headerRow.createCell(5).setCellValue("nickname");
        headerRow.createCell(6).setCellValue("email");
        headerRow.createCell(7).setCellValue("에스크로 사용유무");
        headerRow.createCell(8).setCellValue("결제금액");

        int initialRowIndex = 1;
        for (Project item: dataList) {
            HSSFRow row = sheet.createRow(initialRowIndex++);
            row.createCell(0).setCellValue(item.getId());
            row.createCell(1).setCellValue(item.getTitle());
            row.createCell(2).setCellValue(String.format("%s", item.getCreatedAt() != null?item.getCreatedAt().toLocalDate():""));
            row.createCell(3).setCellValue(item.getStatus().getDesc());
            if (item.getPostingClient() != null) {
                row.createCell(4).setCellValue(item.getPostingClient().getName());
                row.createCell(5).setCellValue(item.getPostingClient().getNickname());
                row.createCell(6).setCellValue(item.getPostingClient().getEmail());
            } else {
                row.createCell(4).setCellValue("-");
                row.createCell(5).setCellValue("-");
                row.createCell(6).setCellValue("-");
            }
            row.createCell(7).setCellValue(item.isUseEscrow());
            row.createCell(8).setCellValue(purchaseRepository.findByProjectIdAndStatus(item.getId(), Purchase.Status.COMPLETED).stream().map(Purchase::getSupplyAmountOfMoney).mapToInt(i->i).sum());
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

    @RequestMapping(method = RequestMethod.GET, value = "/export/excel/access-logs")
    public ResponseEntity<InputStreamResource> exportToExcelDailyAccessLog() throws UnsupportedEncodingException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HSSFSheet sheet = workbook.createSheet();

        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(1).setCellValue("날짜");
        headerRow.createCell(2).setCellValue("접속 횟수");

        int initialRowIndex = 0;
        LocalDate date = LocalDate.of(2020, 1, 1);
//        LocalDate endDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.now();
        while (date.isBefore(endDate)) {
            HSSFRow row = sheet.createRow(initialRowIndex++);

            int count = dailyAccessLogRepository.countByDate(date);
            row.createCell(1).setCellValue(date.toString());
            row.createCell(2).setCellValue(count);

            date = date.plusDays(1);
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(String.format("프리랜서코리아일자별접속횟수_%s.xls", LocalDate.now()), "UTF-8"))
                .body(resource);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/export/excel/user")
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
}
