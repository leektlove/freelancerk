package com.freelancerk.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface StorageService {

    String store(String key, InputStream is, int fileLength);

    String storeFile(InputStream is, String originalFileName);

    String storeFile(MultipartFile file);

    List<String> storeFiles(List<MultipartFile> files);
}
