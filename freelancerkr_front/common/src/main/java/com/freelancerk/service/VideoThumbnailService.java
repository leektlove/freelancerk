package com.freelancerk.service;

import java.util.List;

public interface VideoThumbnailService {

    List<String> getThumbnailUrls(String videoFileUrl);

    String getOneThumbnailUrl(String fileUrl);
}
