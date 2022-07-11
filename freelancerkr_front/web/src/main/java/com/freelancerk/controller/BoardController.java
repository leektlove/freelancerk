package com.freelancerk.controller;

import com.freelancerk.service.StorageService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "공지사항", description = "등록/조회 등")
@RestController
public class BoardController {

//    private AdminUserRepository adminUserRepository;
//    private NoticeRepository noticeRepository;
//    private StorageService storageService;
//
//    @Autowired
//    public BoardController(AdminUserRepository adminUserRepository, NoticeRepository noticeRepository,
//                           StorageService storageService) {
//        this.adminUserRepository = adminUserRepository;
//        this.noticeRepository = noticeRepository;
//        this.storageService = storageService;
//    }
//
//    @ApiOperation("공지사항 조회")
//    @RequestMapping(method = RequestMethod.GET, value = "/admin/notices")
//    public CommonListResponse<List<Notice>> getNotices(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
//                                                       @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
//        Page<Notice> page = noticeRepository.findAll(PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
//        return new CommonListResponse.Builder<List<Notice>>()
//                .totalCount(page.getTotalElements())
//                .totalPages(page.getTotalPages())
//                .currentPage(pageNumber)
//                .data(page.getContent()).build();
//    }
//
//    @ApiOperation("공지사항 등록")
//    @RequestMapping(method = RequestMethod.POST, value = "/admin/notices")
//    public CommonResponse insertNotice(@RequestParam("title") String title, @RequestParam("content") String content,
//                                       @RequestParam("file") MultipartFile file) {
//
//        Notice notice = new Notice();
//        notice.setContent(content);
//        notice.setCreatedAt(LocalDateTime.now());
//        notice.setContent(content);
//        notice.setFileUrl(storageService.storeFile(file));
//        notice.setTitle(title);
//        noticeRepository.save(notice);
//
//        return CommonResponse.ok();
//    }
}
