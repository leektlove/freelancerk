package com.freelancerk.controller.view;

import com.freelancerk.domain.Category;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.UserRepository;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ExcelViewController {

    private UserRepository userRepository;

    @Autowired
    public ExcelViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/export/excel")
    public ResponseEntity<InputStreamResource> exportToExcel() throws UnsupportedEncodingException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HSSFSheet sheet = workbook.createSheet();

        List<User> dataList = userRepository.findByLeavedFalse();

        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("이름");
        headerRow.createCell(1).setCellValue("휴대전화번호");
        headerRow.createCell(2).setCellValue("이메일");
        headerRow.createCell(3).setCellValue("이메일 수신 여부");
        headerRow.createCell(4).setCellValue("키워드");
        headerRow.createCell(5).setCellValue("섹터");

        int initialRowIndex = 1;
        for (User item: dataList) {

            HSSFRow row = sheet.createRow(initialRowIndex++);
            row.createCell(0).setCellValue(item.getName());
            row.createCell(1).setCellValue(item.getCellphone());
            row.createCell(2).setCellValue(item.getEmail());
            row.createCell(3).setCellValue(item.isReceiveEmail());

            Set<Category> categorySet = item.getCategories();

            row.createCell(4).setCellValue(categorySet == null?"":categorySet.stream().map(Category::getName).collect(Collectors.joining(",")));
            row.createCell(5).setCellValue(categorySet == null?"":categorySet.stream().map(Category::getParentCategory).map(Category::getName).distinct().collect(Collectors.joining(",")));

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
}
