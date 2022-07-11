package com.freelancerk.service.impl;

import com.freelancerk.FileUtil;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.PickMeUpRepository;
import com.freelancerk.domain.repository.PickMeUpTicketRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.specification.PickMeUpTicketSpecifications;
import com.freelancerk.service.ImageService;
import com.freelancerk.service.PickMeUpService;
import com.freelancerk.service.UserService;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Service
public class PickMeUpServiceImpl implements PickMeUpService {

	private UserService userService;
	private ImageService imageService;
	private ProjectRepository projectRepository;
	private PickMeUpRepository pickMeUpRepository;
	private PickMeUpTicketRepository pickMeUpTicketRepository;

	@Autowired
	public PickMeUpServiceImpl(UserService userService, ImageService imageService, ProjectRepository projectRepository,
							   PickMeUpRepository pickMeUpRepository, PickMeUpTicketRepository pickMeUpTicketRepository) {
		this.userService = userService;
		this.imageService = imageService;
		this.projectRepository = projectRepository;
		this.pickMeUpRepository = pickMeUpRepository;
		this.pickMeUpTicketRepository = pickMeUpTicketRepository;
	}

	@Override
	public PickMeUp create(PickMeUp pickMeUp, String mainImageUrl, String croppedMainImageUrl, String[] subImageUrls,
						   String[] croppedSubImagesUrls, Long projectId, PickMeUp.PayType payType, Integer hopePay,
						   Project.WorkPlace workPlace, String workPlaceAddress1, String workPlaceAddress2) {
		pickMeUp.setUser(userService.getCurrentUser());

		pickMeUp.setMainImageUrl(mainImageUrl);
		pickMeUp.setCroppedMainImageUrl(croppedMainImageUrl);

		if (mainImageUrl.startsWith("http")) {
			pickMeUp.setCompressedImageUrl(imageService.getCompressedImageUrl(mainImageUrl));
		}

		pickMeUp.setPayType(payType);
		pickMeUp.setMinimumPay(hopePay != null?hopePay:0);

		pickMeUp.setWorkPlace(workPlace);
		pickMeUp.setWorkPlaceAddress1(workPlaceAddress1);
		pickMeUp.setWorkPlaceAddress2(workPlaceAddress2);

		if (projectId != null) {
			pickMeUp.setProject(projectRepository.getOne(projectId));
		}

		List<PickMeUpDetailFile> detailImages = new ArrayList<>();
		if (subImageUrls != null && subImageUrls.length > 0) {
			int i = 0;
			for (String subImageUrl : subImageUrls) {
				if (StringUtils.isEmpty(subImageUrl)) continue;
				PickMeUpDetailFile detailImage = new PickMeUpDetailFile();
				detailImage.setFileUrl(subImageUrl);
				detailImage.setFileType(FileUtil.getFileType(subImageUrl));
				detailImage.setPickMeUp(pickMeUp);
				detailImage.setCreatedAt(LocalDateTime.now());

				if (StringUtils.isNotEmpty(subImageUrl)) {
					Header[] headers = FileUtil.getHeadersFromUrl(subImageUrl);
					if (headers != null) {
						for (Header header: headers) {
							if ("Content-Length".equalsIgnoreCase(header.getName())) {
								detailImage.setFileSize(Long.parseLong(header.getValue()));
							}
							if ("Content-Disposition".equalsIgnoreCase(header.getName())) {
								detailImage.setFileOriginName(FileUtil.getFileNameFromContentDisposition(header.getValue()));
							}
						}
					}
				}

				detailImages.add(detailImage);
			}
		}

		pickMeUp.setDetailFiles(detailImages);
		pickMeUp.setInvalid(false);
		if (pickMeUp.getCategory2nd() != null) {
			pickMeUp.setCategory1st(pickMeUp.getCategory2nd().getParentCategory());
		}

		if (PickMeUp.ContentType.BLOG.equals(pickMeUp.getContentType())) {
			pickMeUp.setDescription(pickMeUp.getEditordata());
			Document document = Jsoup.parse(pickMeUp.getEditordata());
			Elements imageElements = document.select("img");
			if (imageElements != null && imageElements.size() > 0) {
				pickMeUp.setMainImageUrl(imageElements.get(0).attr("src"));
				pickMeUp.setCroppedMainImageUrl(imageElements.get(0).attr("src"));
			}
		}
		return pickMeUpRepository.save(pickMeUp);
	}
	
    @Override
    public void update(PickMeUp pickMeUpParam, String mainImageUrl, String croppedMainImageUrl, String[] subImageUrls, String[] croppedSubImagesUrls, PickMeUp.PayType payType, Integer hopePay, Project.WorkPlace workPlace, String workPlaceAddress1, String workPlaceAddress2) {
		PickMeUp pickMeUp = pickMeUpRepository.getOne(pickMeUpParam.getId());
		pickMeUp.setTitle(pickMeUpParam.getTitle());
		pickMeUp.setDescription(pickMeUpParam.getDescription());
		pickMeUp.setWorkStartAt(pickMeUpParam.getWorkStartAt());
		pickMeUp.setWorkEndAt(pickMeUpParam.getWorkEndAt());
		if (StringUtils.isNotEmpty(mainImageUrl)) {
			pickMeUp.setMainImageUrl(mainImageUrl);
			pickMeUp.setCroppedMainImageUrl(mainImageUrl);
			if (mainImageUrl.startsWith("http")) {
				pickMeUp.setCompressedImageUrl(imageService.getCompressedImageUrl(mainImageUrl));
			}
		}
		pickMeUp.setPayType(payType);
		pickMeUp.setMinimumPay(hopePay != null?hopePay:0);

		pickMeUp.setWorkPlace(workPlace);
		pickMeUp.setWorkPlaceAddress1(workPlaceAddress1);
		pickMeUp.setWorkPlaceAddress2(workPlaceAddress2);

		List<PickMeUpDetailFile> detailImages = new ArrayList<>();
		if (subImageUrls != null && subImageUrls.length > 0) {
			int i = 0;
			for (String subImageUrl : subImageUrls) {
				PickMeUpDetailFile detailImage = new PickMeUpDetailFile();
				detailImage.setFileUrl(subImageUrl);
				detailImage.setFileType(FileUtil.getFileType(subImageUrl));
				detailImage.setPickMeUp(pickMeUp);
				detailImage.setCreatedAt(LocalDateTime.now());

				if (StringUtils.isNotEmpty(subImageUrl)) {
					Header[] headers = FileUtil.getHeadersFromUrl(subImageUrl);
					if (headers != null) {
						for (Header header: headers) {
							if ("Content-Length".equalsIgnoreCase(header.getName())) {
								detailImage.setFileSize(Long.parseLong(header.getValue()));
							}
							if ("Content-Disposition".equalsIgnoreCase(header.getName())) {
								detailImage.setFileOriginName(FileUtil.getFileNameFromContentDisposition(header.getValue()));
							}
						}
					}
				}

				detailImages.add(detailImage);
			}
		}

        if (pickMeUp.getDetailFiles() != null) {
        	pickMeUp.getDetailFiles().clear();
        	pickMeUp.getDetailFiles().addAll(detailImages);
		} else {
			pickMeUp.setDetailFiles(detailImages);
		}
		if (pickMeUpParam.getCategory2nd() != null) {
			pickMeUp.setCategory2nd(pickMeUpParam.getCategory2nd());
			pickMeUp.setCategory1st(pickMeUpParam.getCategory2nd().getParentCategory());
		}

		if (PickMeUp.ContentType.BLOG.equals(pickMeUpParam.getContentType())) {
			pickMeUp.setDescription(pickMeUpParam.getEditordata());
			Document document = Jsoup.parse(pickMeUpParam.getEditordata());
			Elements imageElements = document.select("img");
			if (imageElements != null && imageElements.size() > 0) {
				pickMeUp.setMainImageUrl(imageElements.get(0).attr("src"));
				pickMeUp.setCroppedMainImageUrl(imageElements.get(0).attr("src"));
			}
		}

		pickMeUpRepository.save(pickMeUp);
    }

	@Override
	public int countPortfolio(Long userId) {
		return pickMeUpRepository.countByUserIdAndInvalidFalseAndTempFalse(userId);
	}

	@Override
	public void setValidTicketItem(PickMeUp pickMeUp) {
		List<FreelancerProductItemType> pickMeUpPayProducts
				= pickMeUpTicketRepository.findAll(PickMeUpTicketSpecifications.filterForActiveTicket(pickMeUp.getId()))
				.stream()
				.map(PickMeUpTicket::getFreelancerProductItemType)
				.collect(Collectors.toList());

		pickMeUp.setExpose(pickMeUpPayProducts.stream().map(FreelancerProductItemType::getCode).collect(Collectors.toList()).contains(FreelancerProductItemType.Code.PICK_ME_UP));
		pickMeUp.setFeatured(pickMeUpPayProducts.stream().map(FreelancerProductItemType::getCode).collect(Collectors.toList()).contains(FreelancerProductItemType.Code.FEATURED));
		pickMeUp.setHighQuality(pickMeUpPayProducts.stream().map(FreelancerProductItemType::getCode).collect(Collectors.toList()).contains(FreelancerProductItemType.Code.HIGH_QUALITY));
		pickMeUp.setCreative(pickMeUpPayProducts.stream().map(FreelancerProductItemType::getCode).collect(Collectors.toList()).contains(FreelancerProductItemType.Code.CREATIVE));
		pickMeUp.setDirectDealAvailable(pickMeUpPayProducts.stream().map(FreelancerProductItemType::getCode).collect(Collectors.toList()).contains(FreelancerProductItemType.Code.DIRECT_DEAL));
	}
}
