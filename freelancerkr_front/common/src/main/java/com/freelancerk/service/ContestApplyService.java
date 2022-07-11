package com.freelancerk.service;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.Purchase;

public interface ContestApplyService {

    ProjectBid apply(Project project, Long userId, String mainFileUrl, String croppedMainPiecesFileUrl, String[] subFileUrls, String[] croppedSubPiecesFileUrls, String videoThumbnailImageUrl);
}
