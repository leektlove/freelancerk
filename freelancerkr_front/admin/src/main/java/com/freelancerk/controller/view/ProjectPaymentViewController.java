package com.freelancerk.controller.view;

import com.freelancerk.TimeUtil;
import com.freelancerk.controller.BaseController;
import com.freelancerk.domain.EscrowLog;
import com.freelancerk.domain.PaymentToUser;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.EscrowLogRepository;
import com.freelancerk.domain.repository.PaymentToUserRepository;
import com.freelancerk.domain.specification.PaymentToUserSpecifications;
import com.freelancerk.domain.specification.UserSpecifications;
import com.freelancerk.service.ProjectService;
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

@Controller
public class ProjectPaymentViewController extends BaseController {

    private ProjectService projectService;
    private EscrowLogRepository escrowLogRepository;
    private PaymentToUserRepository paymentToUserRepository;

    @Autowired
    public ProjectPaymentViewController(ProjectService projectService, EscrowLogRepository escrowLogRepository,
                                        PaymentToUserRepository paymentToUserRepository) {
        this.projectService = projectService;
        this.escrowLogRepository = escrowLogRepository;
        this.paymentToUserRepository = paymentToUserRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/projectPayment", "/projectPayment/projectList"})
    public String getPaymentListView(Model model,
                                     @RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "createdAtFrom", required = false) String createdAtFrom,
                                     @RequestParam(value = "createdAtTo", required = false) String createdAtTo,
                                     @RequestParam(value = "status", required = false) PaymentToUser.Status status,
                                     @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                     @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize) {

        Page<PaymentToUser> page = paymentToUserRepository.findAll(
                PaymentToUserSpecifications.filterForPaymentAdmin(
                        keyword,
                        status,
                        TimeUtil.convertStrToLocalDateTime(createdAtFrom),
                        TimeUtil.convertStrToLocalDateTime(createdAtTo)),
                PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

        for (PaymentToUser item: page) {
            item.getProject().setRemainEscrowAmount(projectService.getRemainEscrow(item.getProject().getPostingClient().getId()));
        }

        long totalPayedAmount = paymentToUserRepository.findByStatus(PaymentToUser.Status.PAYED).stream().mapToLong(PaymentToUser::getAmount).sum();

        setPaginationModelData(model, pageNumber, page);

        model.addAttribute("page", page);
        model.addAttribute("totalPayedAmount", totalPayedAmount);

        return "payment/projectList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/projectPayment/export/excel")
    public ResponseEntity<InputStreamResource> exportToExcel() throws UnsupportedEncodingException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HSSFSheet sheet = workbook.createSheet();

        List<PaymentToUser> dataList = paymentToUserRepository.findAll(
                PaymentToUserSpecifications.filterForPaymentAdmin(
                        null,
                        null,
                        null,
                        null));

        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(1).setCellValue("요청일");
        headerRow.createCell(2).setCellValue("클라이언트");
        headerRow.createCell(3).setCellValue("프리랜서");
        headerRow.createCell(4).setCellValue("주민등록번호(사업자등록번호)");
        headerRow.createCell(5).setCellValue("프로젝트정보");
        headerRow.createCell(6).setCellValue("진행사항");
        headerRow.createCell(7).setCellValue("결제금액");

        int initialRowIndex = 1;
        for (PaymentToUser item: dataList) {
            HSSFRow row = sheet.createRow(initialRowIndex++);
            row.createCell(1).setCellValue(item.getCreatedAt().toString());
            row.createCell(2).setCellValue(String.format("%s (%s)", item.getProject().getPostingClient().getCorporateName(),
                    item.getProject().getPostingClient().getEmail()));
            row.createCell(3).setCellValue(String.format("%s (%s)", item.getUser().getName(),
                    item.getUser().getEmail()));
            row.createCell(4).setCellValue(item.getUser().getFreelancerBusinessInfo());
            row.createCell(5).setCellValue(item.getProject().getTitle());
            row.createCell(6).setCellValue(item.getStatus().getDesc());
            row.createCell(7).setCellValue(item.getAmount());
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(String.format("프리랜서코리아_프로젝트결제관리_%s.xls", LocalDate.now()), "UTF-8"))
                .body(resource);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/view/payment-to-users/{id}")
    public String getProjectRegistrationView(@PathVariable("id") long id, Model model) {
        PaymentToUser paymentToUser = paymentToUserRepository.getOne(id);

        long escrowDepositMoney = escrowLogRepository.findByDepositUserIdAndType(paymentToUser.getProject().getPostingClient().getId(), EscrowLog.Type.DEPOSIT).stream().mapToInt(EscrowLog::getAmount).sum()
                - escrowLogRepository.findByWithdrawalUserIdAndType(paymentToUser.getProject().getPostingClient().getId(), EscrowLog.Type.WITHDRAWAL).stream().mapToInt(EscrowLog::getAmount).sum();

        model.addAttribute("escrowDepositMoney", escrowDepositMoney);
        model.addAttribute("item", paymentToUser);
        return "payment/paymentToUserForm";
    }
}
