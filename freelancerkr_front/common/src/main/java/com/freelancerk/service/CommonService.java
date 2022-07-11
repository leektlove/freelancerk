package com.freelancerk.service;

import com.freelancerk.domain.Inquiry;
import com.freelancerk.domain.Notice;
import com.freelancerk.domain.Reference;
import com.freelancerk.domain.ReferenceFile;
import com.freelancerk.vo.TotalPagesInfoVO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface CommonService {
	// 공지사항
	Notice getThisNotice(Long noticeId);
	Page<Notice> getNoticePages(String searchWord, int currentPageNumber);
	TotalPagesInfoVO getNoticePagesInfo(String searchWord);
	void updateNoticeHitsCount(Long noticeId);

	// 자료실
	Reference getThisReference(Long referenceId);
	Page<Reference> getReferencePages(String searchWord, int currentPageNumber);
	TotalPagesInfoVO getReferencePagesInfo(String searchWord);
	void updateReferenceHitsCount(Long referenceId);
	List<ReferenceFile> getThisReferenceFiles(Long referenceId);

	// 고객센터
	void insertInquiry(Inquiry inquiry, Inquiry.Status status);
	Inquiry getThisInquiry(Long inquiryId);
	Page<Inquiry> getInquiryPages(Long userId, String searchWord, int currentPageNumber);
	Page<Inquiry> getInquiryPages(Long userId, String searchWord, int currentPageNumber, LocalDateTime startDate, LocalDateTime endDate);
	TotalPagesInfoVO getInquiryPagesInfo(Long userId, String searchWord);
	TotalPagesInfoVO getInquiryPagesInfo(Long userId, String searchWord, LocalDateTime startDate, LocalDateTime endDate);
}
