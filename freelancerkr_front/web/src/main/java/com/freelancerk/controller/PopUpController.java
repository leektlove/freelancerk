package com.freelancerk.controller;

import com.freelancerk.io.CommonListResponse;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.domain.AdminUser;
import com.freelancerk.domain.Popup;
import com.freelancerk.domain.repository.AdminUserRepository;
import com.freelancerk.domain.repository.PopupRepository;
import com.freelancerk.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "팝업관리", description = "등록/조회 등")
@RestController
public class PopUpController {

    private PopupRepository popupRepository;
    private AdminUserRepository adminUserRepository;
    private StorageService storageService;

    @Autowired
    public PopUpController(PopupRepository popupRepository, AdminUserRepository adminUserRepository,
                           StorageService storageService) {
        this.popupRepository = popupRepository;
        this.adminUserRepository = adminUserRepository;
        this.storageService = storageService;
    }

    @ApiOperation("팝업 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/popups")
    public CommonListResponse<List<Popup>> getPopups(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                               @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        Page<Popup> page = popupRepository.findAll(PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        return new CommonListResponse.Builder<List<Popup>>()
                .totalCount(page.getTotalElements())
                .currentPage(pageNumber)
                .totalPages(page.getTotalPages())
                .data(page.getContent())
                .build();
    }

    @ApiOperation("팝업 등록")
    @RequestMapping(method = RequestMethod.POST, value = "/popups")
    public CommonResponse insertPopups(@RequestParam("imageFile") MultipartFile imageFile,
                                       @RequestParam("linkUrl") String linkUrl) {
        Long userId = ((AdminUser) SecurityContextHolder.getContext().getAuthentication()).getId();
        AdminUser adminUser = adminUserRepository.getOne(userId);
        Popup popup = new Popup();
        popup.setAdminUser(adminUser);
        popup.setImageUrl(storageService.storeFile(imageFile));
        popup.setLinkUrl(linkUrl);
        popupRepository.save(popup);

        return CommonResponse.ok();
    }
}
