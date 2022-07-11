package com.freelancerk.service.impl;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.freelancerk.AWSS3Wrapper;
import com.freelancerk.Constant;
import com.freelancerk.service.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    AWSS3Wrapper awss3Wrapper;

    @Value("${cloud.aws.s3.bucket.url}") String prefixUrl;

    @Override
    public String storeFile(InputStream is, String originalFileName) {
        String fileName = UUID.randomUUID().toString();
        awss3Wrapper.upload(is, fileName, null, originalFileName, null);
        return prefixUrl +  String.format("%s.%s", fileName, FilenameUtils.getExtension(originalFileName));
    }

    @Override
    public String storeFile(MultipartFile file) {
        return store(file);
    }

    @Override
    public List<String> storeFiles(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        if (files == null || files.isEmpty()){
            return urls;
        }
        for (MultipartFile file: files) {
            if (file == null) continue;
            urls.add(store(file));
        }
        return urls;
    }

    @Override
    public String store(String key, InputStream is, int fileLength) {
        awss3Wrapper.upload(is, key, fileLength);
        return prefixUrl + key;
    }

    public String store(MultipartFile multipartFile) {

        MultipartFile[] files = new MultipartFile[1];
        files[0] = multipartFile;
        String fileName = UUID.randomUUID().toString();
        List<PutObjectResult> resultsList = awss3Wrapper.upload(files, fileName, multipartFile.getContentType());
        return prefixUrl +  String.format("%s.%s", fileName, FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
    }
}
