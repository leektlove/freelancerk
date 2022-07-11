package com.freelancerk.controller.view;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectCommentRepository;
import com.freelancerk.domain.repository.ProjectPropositionRepository;
import com.freelancerk.domain.specification.ProjectBidSpecifications;
import com.freelancerk.domain.specification.ProjectPropositionSpecifications;
import com.freelancerk.model.SortBy;
import com.freelancerk.service.ProjectBidService;
import com.freelancerk.service.UserService;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Controller
@RequestMapping("/freelancer/bid/")
public class FreelancerBidViewController extends RootController {
	private static int ITEM_PER_PAGE = 10;

	private UserService userService;
	private ProjectBidService projectBidService;
	private ProjectBidService contestEntryService;
	private ProjectBidRepository projectBidRepository;
	private ProjectCommentRepository projectCommentRepository;
	private ProjectPropositionRepository projectPropositionRepository;

	public FreelancerBidViewController(UserService userService, ProjectBidService projectBidService, ProjectBidService contestEntryService,
									   ProjectBidRepository projectBidRepository, ProjectCommentRepository projectCommentRepository,
									   ProjectPropositionRepository projectPropositionRepository) {
		this.userService = userService;
		this.projectBidService = projectBidService;
		this.contestEntryService = contestEntryService;
		this.projectBidRepository = projectBidRepository;
		this.projectCommentRepository = projectCommentRepository;
		this.projectPropositionRepository = projectPropositionRepository;
	}

	@GetMapping("/onGoingList")
	public String onGoingList(@RequestParam(value = "sortBy", required = false, defaultValue = "CREATED_AT") final SortBy sortBy,
							  @RequestParam(value = "pageNumber", defaultValue = "0", required = false) final int pageNumber,
							  @RequestParam(value = "pageSize", defaultValue = "10", required = false) final int pageSize,
							  Model model) {
		User user = userService.getCurrentUser();
		Long userId = user.getId();

		Page<ProjectBid> page = projectBidRepository.findAll(
				ProjectBidSpecifications.filter(userId, null, null, ProjectBid.BidStatus.APPLY, Project.Status.POSTED, null, sortBy),
				PageRequest.of(pageNumber, pageSize));

		for (ProjectBid bid : page) {
			Project project = bid.getProject();
			project.setMessageCountByClient(projectCommentRepository.countByProjectIdAndUserId(project.getId(), project.getPostingClient().getId()));
			bid.setAvgCareerYear(String.format("%.1f", projectBidService.getAvgOfProjectBidCareerYear(project.getId())));

			if(Project.Type.PROJECT.equals(project.getProjectType())) {

			}
			else if(Project.Type.CONTEST.equals(project.getProjectType())) {
				List<ContestEntryFile> entryFileList = contestEntryService.getAllContestEntryFile(bid.getId());
				List<String> thumbUrl = new ArrayList<>();
				for (ContestEntryFile file : entryFileList) {
					thumbUrl.add(file.getFileUrl());
				}
				bid.getProject().setEntryFileThumbnailUrl(thumbUrl);
			}


			project.setCommentCountVisibleToMe(
					projectCommentRepository.countByProjectIdAndTargetUserIsNull(project.getId()) +
							projectCommentRepository.countByProjectIdAndTargetUserId(project.getId(), getSessionUserId()) +
							projectCommentRepository.countByProjectIdAndTargetUserIdAndUserId(project.getId(), project.getPostingClient().getId(), getSessionUserId()));
		}

		model.addAttribute("page", page);
		model.addAttribute("sortBy", sortBy);

		setPaginationModelData(model, pageNumber, page);

		return "freelancer/bid/onGoingList";
	}
	
	@GetMapping("/successList")
	public String successList(
			@RequestParam(value = "sortBy", required = false, defaultValue = "CREATED_AT") final SortBy sortBy,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) final int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) final int pageSize,
			Model model) {

		User user = userService.getCurrentUser();

		Page<ProjectBid> page = projectBidRepository.findAll(ProjectBidSpecifications.filter(
				getSessionUserId(), null, null, ProjectBid.BidStatus.PICKED,
				null, null, sortBy), PageRequest.of(pageNumber, pageSize));
		
		for (ProjectBid bid : page) {
			Project project = bid.getProject();
			// 공통부분
			bid.setAvgCareerYear(String.format("%.1f", projectBidService.getAvgOfProjectBidCareerYear(project.getId())));
			
			if(Project.Type.PROJECT.equals(project.getProjectType())) {
				// 낙찰가격
				int avgBidMoney = projectBidService.getAvgOfProjectBidMoney(project.getId());
				bid.setAverageBidMoney(avgBidMoney);
			} 
			else if(Project.Type.CONTEST.equals(project.getProjectType())) {
				int myRank = bid.getPickedRank();
				bid.setMyRank(myRank + 1);
				int myPrizeMoney = 0;
				if (bid.getMyRank() == 1) {
					myPrizeMoney = project.getPrizeFor1st();
				} else if (bid.getMyRank() == 2) {
					myPrizeMoney = project.getPrizeFor2nd();
				} else if (bid.getMyRank() == 3) {
					myPrizeMoney = project.getPrizeFor3rd();
				}
				bid.setMyPrizeMoney(myPrizeMoney);

				List<ContestEntryFile> entryFileList = contestEntryService.getAllContestEntryFile(bid.getId());
				List<String> thumbUrl = new ArrayList<>();
				for (ContestEntryFile file : entryFileList) {
					thumbUrl.add(file.getFileUrl());
				}
				bid.getProject().setEntryFileThumbnailUrl(thumbUrl);
			}
		}

		model.addAttribute("page", page);
		model.addAttribute("sortBy", sortBy);

		setPaginationModelData(model, pageNumber, page);

		return "freelancer/bid/successList";
	}
	
	
	@GetMapping("/failList")
	public String failList(
			@RequestParam(value = "projectType", required = false) final Project.Type projectType,
			@RequestParam(value = "sortBy", required = false, defaultValue = "CREATED_AT") final SortBy sortBy,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) final int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) final int pageSize,
			Model model) {

		Page<ProjectBid> page = projectBidRepository.findAll(
				ProjectBidSpecifications.filterForCancelOrFail(getSessionUserId(), projectType, sortBy),
				PageRequest.of(pageNumber, pageSize));

		for (ProjectBid bid : page) {
			Project project = bid.getProject();
			bid.setAvgCareerYear(String.format("%.1f", projectBidService.getAvgOfProjectBidCareerYear(project.getId())));
			
			if(Project.Type.PROJECT.equals(bid.getProject().getProjectType())) {
				bid.setAverageBidMoney(projectBidService.getAvgOfProjectBidMoney(project.getId()));
				bid.setNumberOfBids(projectBidRepository.countByProjectIdAndInvalidFalse(project.getId()));
			} 
		}

		model.addAttribute("page", page);
		model.addAttribute("sortBy", sortBy);
		model.addAttribute("projectType", projectType);

		setPaginationModelData(model, pageNumber, page);

		return "freelancer/bid/failList";
	}
	

	@GetMapping("/suggestedList")
	public String suggestedList(
			@RequestParam(value = "sortBy", required = false, defaultValue = "CREATED_AT") final SortBy sortBy,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) final int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) final int pageSize,
			Model model) {

		User user = userService.getCurrentUser();
		Long userId = user.getId();

		Page<ProjectProposition> page = projectPropositionRepository.findAll(
				ProjectPropositionSpecifications.filterForFreelancer(getSessionUserId(), sortBy),
				PageRequest.of(pageNumber, pageSize));

		for (ProjectProposition bid : page.getContent()) {
			ProjectBid myBid = projectBidService.getMyContestEntry(userId, bid.getProject().getId());
			if (myBid != null) {
				bid.getProject().setEntryFileThumbnailUrl(
						projectBidService.getAllContestEntryFile(myBid.getId()).stream().map(ContestEntryFile::getFileUrl).collect(Collectors.toList())
				);
			}
			bid.setMyBid(myBid);
		}

		model.addAttribute("page", page);
		model.addAttribute("sortBy", sortBy);

		setPaginationModelData(model, pageNumber, page);

		return "freelancer/bid/suggestedList";
	}
}
