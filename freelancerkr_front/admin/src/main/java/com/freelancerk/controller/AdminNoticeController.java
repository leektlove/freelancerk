package com.freelancerk.controller;

import com.freelancerk.domain.Notice;
import com.freelancerk.domain.repository.NoticeRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.StorageService;
import com.freelancerk.type.NoticeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@RestController
public class AdminNoticeController {

    private StorageService storageService;
    private NoticeRepository noticeRepository;

    @Autowired
    public AdminNoticeController(StorageService storageService, NoticeRepository noticeRepository) {
        this.storageService = storageService;
        this.noticeRepository = noticeRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/notices")
    public void updateItem(@RequestParam(value = "ir1", required = false) String ir1,
                           @RequestParam(value = "id", required = false) Long boardContentId,
                           @RequestParam("title") String title,
                           @RequestParam(value = "subtitle", required = false) String subtitle,
                           @RequestParam(value = "mainImageUrl", required = false) String mainImageUrl,
                           @RequestParam(value = "linkUrl", required = false) String linkUrl,
                           @RequestParam(value = "type", required = false) NoticeType type,
                           HttpServletResponse response) throws IOException {
        Notice notice = null;
        if (boardContentId == null) {
            notice = new Notice();
        } else {
            notice = noticeRepository.getOne(boardContentId);
        }

        notice.setTitle(title);
        notice.setSubtitle(subtitle);
        notice.setMainImageUrl(mainImageUrl);
        notice.setType(type);
        notice.setLinkUrl(linkUrl);
        notice.setContent(ir1);

        noticeRepository.save(notice);

        response.sendRedirect("/notice/");
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/notices/{id}")
    public CommonResponse deleteItem(@PathVariable("id") Long id) {
        noticeRepository.deleteById(id);

        return CommonResponse.ok();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/files")
    public void insertFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = UUID.randomUUID().toString();
        String url = storageService.store(name, request.getInputStream(), Integer.parseInt(request.getHeader("file-size")));

        String sFileInfo = "&bNewLine=true";
        // img 태그의 title 속성을 원본파일명으로 적용시켜주기 위함
        sFileInfo += "&sFileName="+ name;
        sFileInfo += "&sFileURL="+ url;

        PrintWriter print = response.getWriter();
        print.print(sFileInfo);
        print.flush();
        print.close();

    }
}
