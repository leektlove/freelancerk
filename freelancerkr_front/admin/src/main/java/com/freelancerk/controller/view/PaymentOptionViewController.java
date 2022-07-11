package com.freelancerk.controller.view;

import com.freelancerk.controller.BaseController;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.ContestEntryTicketLogRepository;
import com.freelancerk.domain.repository.PickMeUpTicketLogRepository;
import com.freelancerk.domain.repository.ProjectItemTicketLogRepository;
import com.freelancerk.domain.repository.PurchaseRepository;
import com.freelancerk.domain.specification.PurchaseSpecifications;
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

@RequestMapping("paymentOption")
@Controller
public class PaymentOptionViewController extends BaseController {

    private PurchaseRepository purchaseRepository;
    private PickMeUpTicketLogRepository pickMeUpTicketLogRepository;
    private ProjectItemTicketLogRepository projectItemTicketLogRepository;
    private ContestEntryTicketLogRepository contestEntryTicketLogRepository;

    @Autowired
    public PaymentOptionViewController(PurchaseRepository purchaseRepository,
                                       PickMeUpTicketLogRepository pickMeUpTicketLogRepository,
                                       ProjectItemTicketLogRepository projectItemTicketLogRepository,
                                       ContestEntryTicketLogRepository contestEntryTicketLogRepository) {
        this.purchaseRepository = purchaseRepository;
        this.pickMeUpTicketLogRepository = pickMeUpTicketLogRepository;
        this.projectItemTicketLogRepository = projectItemTicketLogRepository;
        this.contestEntryTicketLogRepository = contestEntryTicketLogRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/export/excel")
    public ResponseEntity<InputStreamResource> exportToExcel() throws UnsupportedEncodingException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HSSFSheet sheet = workbook.createSheet();

        List<Purchase> dataList = purchaseRepository.findAll(PurchaseSpecifications.filter(Purchase.Status.COMPLETED));

        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("구분");
        headerRow.createCell(1).setCellValue("구매자(유저번호)");
        headerRow.createCell(2).setCellValue("픽미업(픽미업번호)");
        headerRow.createCell(3).setCellValue("옵션명");
        headerRow.createCell(4).setCellValue("옵션가격");
        headerRow.createCell(5).setCellValue("결제금액(공급가액)");
        headerRow.createCell(6).setCellValue("일시");

        for (Purchase item: dataList) {
            String optionsName = null;
            if (Purchase.Type.PICK_ME_UP.equals(item.getType())) {
                List<PickMeUpTicketLog> logs = pickMeUpTicketLogRepository.findByPurchaseId(item.getId());
                optionsName = StringUtils.join(
                        logs.stream().map(PickMeUpTicketLog::getFreelancerProductItemType).map(FreelancerProductItemType::getName).collect(Collectors.toList()),
                        ",");
            } else if (Arrays.asList(Purchase.Type.CONTEST, Purchase.Type.PROJECT).contains(item.getType())) {
                List<ProjectItemTicketLog> logs = projectItemTicketLogRepository.findByPurchaseId(item.getId());
                optionsName = StringUtils.join(
                        logs.stream().map(ProjectItemTicketLog::getProjectProductItemType).map(ProjectProductItemType::getName).collect(Collectors.toList()),
                        ",");
            } else if (Purchase.Type.CONTEST_ENTRY.equals(item.getType())) {
                List<ContestEntryTicketLog> logs = contestEntryTicketLogRepository.findByPurchaseId(item.getId());
                optionsName = StringUtils.join(
                        logs.stream().map(ContestEntryTicketLog::getFreelancerProductItemType).map(FreelancerProductItemType::getName).collect(Collectors.toList()),
                        ",");
            }

            item.setChargedOptionsName(optionsName);
        }

        int initialRowIndex = 1;
        for (Purchase item: dataList) {

            HSSFRow row = sheet.createRow(initialRowIndex++);
            row.createCell(0).setCellValue(item.getType().getDesc());
            row.createCell(1).setCellValue(String.format("%s (%d)", item.getUser().getExposedEmail(), item.getUser().getId()));
            if (item.getPickMeUp() != null) {
                row.createCell(2).setCellValue(String.format("%s (%d)", item.getPickMeUp().getTitle(), item.getPickMeUp().getId()));
            } else {
                row.createCell(2).setCellValue("");
            }
            row.createCell(3).setCellValue(item.getChargedOptionsName());
            row.createCell(4).setCellValue(item.getChargedOptionsAmountOfMoney());
            row.createCell(5).setCellValue(item.getSupplyAmountOfMoney());
            row.createCell(6).setCellValue(String.valueOf(item.getCreatedAt()));
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(String.format("프리랜서코리아통합유저목록_%s.xls", LocalDate.now()), "UTF-8"))
                .body(resource);
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", "/list"})
    public String getListView(Model model,
                              @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                              @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {

        Page<Purchase> page = purchaseRepository.findAll(PurchaseSpecifications.filter(Purchase.Status.COMPLETED),
                PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createdAt")));

        for (Purchase item: page) {
            String optionsName = null;
            if (Purchase.Type.PICK_ME_UP.equals(item.getType())) {
                List<PickMeUpTicketLog> logs = pickMeUpTicketLogRepository.findByPurchaseId(item.getId());
                optionsName = StringUtils.join(
                        logs.stream().map(PickMeUpTicketLog::getFreelancerProductItemType).map(FreelancerProductItemType::getName).collect(Collectors.toList()),
                        ",");
            } else if (Arrays.asList(Purchase.Type.CONTEST, Purchase.Type.PROJECT).contains(item.getType())) {
                List<ProjectItemTicketLog> logs = projectItemTicketLogRepository.findByPurchaseId(item.getId());
                optionsName = StringUtils.join(
                        logs.stream().map(ProjectItemTicketLog::getProjectProductItemType).map(ProjectProductItemType::getName).collect(Collectors.toList()),
                        ",");
            } else if (Purchase.Type.CONTEST_ENTRY.equals(item.getType())) {
                List<ContestEntryTicketLog> logs = contestEntryTicketLogRepository.findByPurchaseId(item.getId());
                optionsName = StringUtils.join(
                        logs.stream().map(ContestEntryTicketLog::getFreelancerProductItemType).map(FreelancerProductItemType::getName).collect(Collectors.toList()),
                        ",");
            }

            item.setChargedOptionsName(optionsName);
        }

        model.addAttribute("page", page);
        setPaginationModelData(model, pageNumber, page);

        return "paymentOption/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/contestClientList")
    public String getContestClientList() {

        return "paymentOption/contestClientList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/contestFreelancerList")
    public String getContestFreelancerList() {

        return "paymentOption/contestFreelancerList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/pickMeUpList")
    public String getPickMeUpList() {

        return "paymentOption/pickMeUpList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/projectList")
    public String getProjectList() {

        return "paymentOption/projectList";
    }
}
