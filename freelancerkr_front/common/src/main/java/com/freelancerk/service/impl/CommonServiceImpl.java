package com.freelancerk.service.impl;

import com.freelancerk.domain.Inquiry;
import com.freelancerk.domain.Notice;
import com.freelancerk.domain.Reference;
import com.freelancerk.domain.ReferenceFile;
import com.freelancerk.domain.repository.*;
import com.freelancerk.service.CommonService;
import com.freelancerk.vo.TotalPagesInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {
	private final static int ITEM_PER_PAGE = 20;
	private UserRepository userRepository;
	private NoticeRepository noticeRepository;
	private ReferenceRepository referenceRepository;
	private ReferenceFileRepository referenceFileRepository;
	private InquiryRepository inquiryRepository;
	private InquiryAnswerRepository inquiryAnswerRepository;

	@Autowired
	public CommonServiceImpl(UserRepository userRepository, NoticeRepository noticeRepository,
			ReferenceRepository referenceRepository, ReferenceFileRepository referenceFileRepository,
			InquiryRepository inquiryRepository, InquiryAnswerRepository inquiryAnswerRepository) {
		this.userRepository = userRepository;
		this.noticeRepository = noticeRepository;
		this.referenceRepository = referenceRepository;
		this.referenceFileRepository = referenceFileRepository;
		this.inquiryRepository = inquiryRepository;
		this.inquiryAnswerRepository = inquiryAnswerRepository;
	}

	// 공지사항
	@Override
	public Notice getThisNotice(Long noticeId) {
		return noticeRepository.getOne(noticeId);
	}

	@Override
	public Page<Notice> getNoticePages(String searchWord, int currentPageNumber) {
		return noticeRepository.findByTitleContainingOrContentContaining(searchWord, searchWord,
				PageRequest.of(currentPageNumber, ITEM_PER_PAGE, new Sort(Sort.Direction.DESC, "createdAt")));
	}

	@Override
	public TotalPagesInfoVO getNoticePagesInfo(String searchWord) {
		TotalPagesInfoVO vo = new TotalPagesInfoVO();
		int countOfTotalElements = noticeRepository.countByTitleContainingOrContentContaining(searchWord, searchWord);
		int countOfTotalPages = (int) Math.ceil(((double) countOfTotalElements / ITEM_PER_PAGE));
		vo.setCountOfTotalElements(countOfTotalElements);
		vo.setCountOfTotalPages(countOfTotalPages);

		return vo;
	}

	@Override
	public void updateNoticeHitsCount(Long noticeId) {
		Notice notice = noticeRepository.getOne(noticeId);
		Long currentHits = notice.getHits();
		notice.setHits(currentHits + 1);
		noticeRepository.save(notice);
	}

	// 자료실
	@Override
	public Reference getThisReference(Long referenceId) {
		return referenceRepository.getOne(referenceId);
	}

	@Override
	public Page<Reference> getReferencePages(String searchWord, int currentPageNumber) {
		return referenceRepository.findByTitleContainingOrContentContaining(searchWord, searchWord,
				PageRequest.of(currentPageNumber, ITEM_PER_PAGE, new Sort(Sort.Direction.DESC, "createdAt")));
	}

	@Override
	public TotalPagesInfoVO getReferencePagesInfo(String searchWord) {
		TotalPagesInfoVO vo = new TotalPagesInfoVO();
		int countOfTotalElements = referenceRepository.countByTitleContainingOrContentContaining(searchWord,
				searchWord);
		int countOfTotalPages = (int) Math.ceil(((double) countOfTotalElements / ITEM_PER_PAGE));

		vo.setCountOfTotalElements(countOfTotalElements);
		vo.setCountOfTotalPages(countOfTotalPages);

		return vo;
	}

	@Override
	public void updateReferenceHitsCount(Long referenceId) {
		Reference notice = referenceRepository.getOne(referenceId);
		Long currentHits = notice.getHits();
		notice.setHits(currentHits + 1);
		referenceRepository.save(notice);
	}

	@Override
	public List<ReferenceFile> getThisReferenceFiles(Long referenceId) {
		return referenceFileRepository.findByReferenceId(referenceId);
	}

	// 고객센터
	@Override
	public void insertInquiry(Inquiry inquiry, Inquiry.Status status) {
		inquiry.setStatus(status);
		inquiry.setCreatedAt(LocalDateTime.now());
		inquiryRepository.save(inquiry);
	}

	@Override
	public Inquiry getThisInquiry(Long inquiryId) {
		return inquiryRepository.getOne(inquiryId);
	}

	@Override
	public Page<Inquiry> getInquiryPages(Long userId, String searchWord, int currentPageNumber) {
		return inquiryRepository.findByUserIdAndTitleContainingOrContentContaining(userId, searchWord, searchWord, PageRequest.of(currentPageNumber, ITEM_PER_PAGE, new Sort(Sort.Direction.DESC, "createdAt")));
	}

	@Override
	public Page<Inquiry> getInquiryPages(Long userId, String searchWord, int currentPageNumber, LocalDateTime startDate,
			LocalDateTime endDate) {
		return inquiryRepository.findByUserIdAndTitleContainingOrContentContainingAndCreatedAtAfterAndCreatedAtBefore(
				userId, searchWord, searchWord, startDate, endDate,
				PageRequest.of(currentPageNumber, ITEM_PER_PAGE, new Sort(Sort.Direction.DESC, "createdAt")));
	}

	@Override
	public TotalPagesInfoVO getInquiryPagesInfo(Long userId, String searchWord) {
		TotalPagesInfoVO vo = new TotalPagesInfoVO();
		int countOfTotalElements = inquiryRepository.countByUserIdAndTitleContainingOrContentContaining(userId, searchWord,	searchWord);
		int countOfTotalPages = (int) Math.ceil(((double) countOfTotalElements / ITEM_PER_PAGE));
		vo.setCountOfTotalElements(countOfTotalElements);
		vo.setCountOfTotalPages(countOfTotalPages);

		return vo;
	}

	@Override
	public TotalPagesInfoVO getInquiryPagesInfo(Long userId, String searchWord, LocalDateTime startDate, LocalDateTime endDate) {
		TotalPagesInfoVO vo = new TotalPagesInfoVO();
		int countOfTotalElements = inquiryRepository
				.countByUserIdAndTitleContainingOrContentContainingAndCreatedAtAfterAndCreatedAtBefore(userId, searchWord,
						searchWord, startDate, endDate);
		int countOfTotalPages = (int) Math.ceil(((double) countOfTotalElements / ITEM_PER_PAGE));
		vo.setCountOfTotalElements(countOfTotalElements);
		vo.setCountOfTotalPages(countOfTotalPages);

		return vo;
	}
}
