package com.freelancerk.service;

import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.Project;

public interface PickMeUpService {

	PickMeUp create(PickMeUp pickMeUp, String mainImageUrl, String croppedMainImageUrl, String[] subImagesUrls, String[] croppedSubImagesUrls, Long projectId, PickMeUp.PayType payType, Integer hopePay, Project.WorkPlace workPlace, String workPlaceAddress1, String workPlaceAddress2);

    void update(PickMeUp pickMeUp, String mainImageUrl, String croppedMainImageUrl, String[] subImagesUrls, String[] croppedSubImagesUrls, PickMeUp.PayType payType, Integer hopePay, Project.WorkPlace workPlace, String workPlaceAddress1, String workPlaceAddress2);

    int countPortfolio(Long userId);

    void setValidTicketItem(PickMeUp pickMeUp);
}