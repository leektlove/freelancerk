package com.freelancerk.controller;


import com.freelancerk.domain.Reference;
import com.freelancerk.domain.ReferenceFile;
import com.freelancerk.domain.repository.ReferenceFileRepository;
import com.freelancerk.domain.repository.ReferenceRepository;
import com.freelancerk.io.CommonListResponse;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Api(tags = "자료실", description = "등록/조회 등")
@RestController
public class AdminReferenceController {

    private ReferenceRepository referenceRepository;
    private StorageService storageService;
    private ReferenceFileRepository referenceFileRepository;

    @Autowired
    public AdminReferenceController(ReferenceRepository referenceRepository, StorageService storageService) {
        this.referenceRepository = referenceRepository;
        this.storageService = storageService;
    }

    @ApiOperation("자료실 목록 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/references")
    public CommonListResponse<List<Reference>> getReferences(@RequestParam(value = "keyword", required = false) String keyword,
                                                             @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                             @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        Page<Reference> referencePage = null;
        if (StringUtils.isEmpty(keyword)) {
            referencePage = referenceRepository.findAll(PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        } else {
            referencePage = referenceRepository.findByTitleContainingOrContentContaining(
                    keyword,
                    keyword,
                    PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
        }
        return new CommonListResponse.Builder<List<Reference>>()
                .totalCount(referencePage.getTotalElements())
                .currentPage(pageNumber)
                .totalPages(referencePage.getTotalPages())
                .data(referencePage.getContent())
                .build();
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value = "/reference/{id}")
    public CommonResponse deleteItem(@PathVariable("id") Long id) {
    	referenceRepository.deleteById(id);
        return CommonResponse.ok();
    }
    
//  @ApiOperation("자료실 자료 등록")
//  @Transactional
//  @RequestMapping(method = RequestMethod.POST, value = "/add_references")
//  public CommonResponse insertReferences(@RequestParam("title") String title,
//                                         @RequestParam("content") String content,
//                                         @RequestParam("file[]") MultipartFile[] files) {
//      Reference reference = new Reference();
//      reference.setTitle(title);
//      reference.setCreatedAt(LocalDateTime.now());
//      reference.setContent(content);
//      reference = referenceRepository.save(reference);
//
//      for (MultipartFile file: files) {
//          ReferenceFile referenceFile = new ReferenceFile();
//          referenceFile.setCreatedAt(LocalDateTime.now());
//          referenceFile.setFileUrl(storageService.storeFile(file));
//          referenceFile.setReference(reference);
//          referenceFileRepository.save(referenceFile);
//      }
//
//      return CommonResponse.ok();
//  }
}
