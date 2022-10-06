package com.freelancerk.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.freelancerk.FileUtil;
import com.freelancerk.controller.io.JqueryUploaderResponse;
import com.freelancerk.controller.io.JqueryUploaderResponseItem;
import com.freelancerk.domain.ContestEntryFile;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.StorageService;
import com.freelancerk.service.VideoThumbnailService;

@RestController
public class StaticController {

    private StorageService storageService;
    private VideoThumbnailService videoThumbnailService;

    @Autowired
    public StaticController(StorageService storageService, VideoThumbnailService videoThumbnailService) {
        this.storageService = storageService;
        this.videoThumbnailService = videoThumbnailService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/files") // todo token
    public String insertImages(@RequestParam(value = "file", required = false) MultipartFile file,
                               MultipartHttpServletRequest request) {
        return file != null?storageService.storeFile(file):storageService.storeFile(request.getFileMap().values().iterator().next());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/files/for-jquery-upload") // todo token
    public JqueryUploaderResponse insertImagesForJqueryUploader(@RequestParam(value = "files[]", required = false) MultipartFile[] files,
                                               MultipartHttpServletRequest request) {
        JqueryUploaderResponse response = new JqueryUploaderResponse();
        JqueryUploaderResponseItem item = new JqueryUploaderResponseItem();
        List<JqueryUploaderResponseItem> items = new ArrayList<>();
        for (MultipartFile file: files) {
            String url = file != null ? storageService.storeFile(file) : storageService.storeFile(request.getFileMap().values().iterator().next());
            item.setUrl(url);
            if (ContestEntryFile.Type.VIDEO.equals(FileUtil.getFileType(url))) {
                item.setVideoUrl(url);
            }
            item.setThumbnailUrl(url);
            item.setSize((int) file.getSize());
            item.setDeleteType("DELETE");
            item.setDeleteType(url);
            item.setName(file.getOriginalFilename());
            items.add(item);
        }
        response.setFiles(items);
        return response;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/files/thumbnails")
    public CommonResponse<List<String>> getThumbnails(@RequestParam("fileUrl") String fileUrl) {
        return new CommonResponse.Builder<List<String>>().data(videoThumbnailService.getThumbnailUrls(fileUrl)).build();
    }
}
