package com.freelancerk.controller.view;

import com.freelancerk.TimeUtil;
import com.freelancerk.controller.RootController;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.domain.specification.ProjectBidSpecifications;
import com.freelancerk.domain.specification.ProjectFavoriteSpecifications;
import com.freelancerk.domain.specification.ProjectSpecifications;
import com.freelancerk.model.SortBy;
import com.freelancerk.service.ProjectBidService;
import com.freelancerk.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/freelancer/project/")
public class FreelancerProjectViewController extends RootController {

	private UserService userService;
	private ProjectRepository projectRepository;
	private ProjectBidService projectBidService;
	private PickMeUpRepository pickMeUpRepository;
	private ProjectBidRepository projectBidRepository;
	private ProjectRateRepository projectRateRepository;
	private PaymentToUserRepository paymentToUserRepository;
	private ProjectCommentRepository projectCommentRepository;
	private ProjectCompleteRepository projectCompleteRepository;
	private ProjectFavoriteRepository projectFavoriteRepository;

	@Autowired
	public FreelancerProjectViewController(UserService userService,
										   ProjectRepository projectRepository,
										   ProjectBidService projectBidService,
										   PickMeUpRepository pickMeUpRepository,
										   ProjectBidRepository projectBidRepository,
										   ProjectRateRepository projectRateRepository,
										   PaymentToUserRepository paymentToUserRepository,
										   ProjectCommentRepository projectCommentRepository,
										   ProjectCompleteRepository projectCompleteRepository,
										   ProjectFavoriteRepository projectFavoriteRepository) {

		this.userService = userService;
		this.projectBidService = projectBidService;
		this.projectRepository = projectRepository;
		this.pickMeUpRepository = pickMeUpRepository;
		this.projectBidRepository = projectBidRepository;
		this.projectRateRepository = projectRateRepository;
		this.paymentToUserRepository = paymentToUserRepository;
		this.projectCommentRepository = projectCommentRepository;
		this.projectCompleteRepository = projectCompleteRepository;
		this.projectFavoriteRepository = projectFavoriteRepository;
	}
	
	@GetMapping("/completedList")
	public String completedList(
			@RequestParam(value = "startDate", defaultValue = "", required = false) String startDate,
			@RequestParam(value = "endDate", defaultValue = "", required = false) String endDate,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) final int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) final int pageSize,
			@RequestParam(value = "projectId", required = false) final Long projectId,
			final HttpServletRequest request,
			Model model) {

		User user = userService.getCurrentUser();
		Long userId = user.getId();

		LocalDateTime startAt = TimeUtil.convertStrToLocalDateTime(startDate);
		LocalDateTime endAt = TimeUtil.convertStrToLocalDateTime(endDate);

		long inProgressProjectCount = projectRepository.count(ProjectSpecifications.filterForFreelancer(
				getSessionUserId(), null, Collections.singletonList(Project.Status.IN_PROGRESS), null));
		long favoriteProjectCount = projectFavoriteRepository.countByUserId(getSessionUserId());

		Page<ProjectBid> page = projectBidRepository.findAll(ProjectBidSpecifications.filter(
				getSessionUserId(), null, null, ProjectBid.BidStatus.PICKED, Project.Status.COMPLETED, null, SortBy.CREATED_AT),
				PageRequest.of(pageNumber, pageSize));

		List<Project> projects = page.getContent().stream().map(ProjectBid::getProject).collect(Collectors.toList());

		long totalIncomeAmount = paymentToUserRepository.findByUserIdAndStatus(getSessionUserId(), PaymentToUser.Status.PAYED).stream().map(PaymentToUser::getAmount).mapToLong(Long::longValue).sum();

		for (Project project : projects) {
			ProjectBid myBid = projectBidService.getMyContestEntry(userId, project.getId());
			project.setMyProjectBid(myBid);
			project.setPickMeUpRegistered(pickMeUpRepository.findByProjectIdAndUserIdAndTempFalse(project.getId(), userId) != null);
			if(Project.Type.CONTEST.equals(project.getProjectType())) {
				int myRank = myBid.getPickedRank();
				int myPrizeMoney = 0;
				if (myRank == 0) {
					myPrizeMoney = project.getPrizeFor1st();
				} else if (myRank == 1) {
					myPrizeMoney = project.getPrizeFor2nd();
				} else if (myRank == 2) {
					myPrizeMoney = project.getPrizeFor3rd();
				}
				List<String> thumbUrl = projectBidService.getAllContestEntryFile(myBid.getId()).stream().map(ContestEntryFile::getFileUrl).collect(Collectors.toList());
				project.setEntryFileThumbnailUrl(thumbUrl);
				project.setMyPrizeMoney(myPrizeMoney);
				totalIncomeAmount += myPrizeMoney;
			} else {
				project.setTotalIncomeAmount(
						paymentToUserRepository.findByProjectIdAndStatus(project.getId(), PaymentToUser.Status.PAYED)
								.stream().map(PaymentToUser::getAmount).mapToLong(Long::longValue).sum()
				);
			}
		}


		List<ProjectRate> projectRates = projectRateRepository.findByRatedUserIdAndRaterTypeAndProjectStatus(
				getSessionUserId(), ProjectRate.RaterType.CLIENT, Project.Status.COMPLETED);

		model.addAttribute("type1Rate", projectRates.size() == 0?0:100* projectRates.stream().map(ProjectRate::getType1Rate).mapToInt(Integer::intValue).sum() / (5*projectRates.size()));
		model.addAttribute("type2Rate", projectRates.size() == 0?0:100* projectRates.stream().map(ProjectRate::getType2Rate).mapToInt(Integer::intValue).sum() / (5*projectRates.size()));
		model.addAttribute("type3Rate", projectRates.size() == 0?0:100* projectRates.stream().map(ProjectRate::getType3Rate).mapToInt(Integer::intValue).sum() / (5*projectRates.size()));
		model.addAttribute("type4Rate", projectRates.size() == 0?0:100* projectRates.stream().map(ProjectRate::getType4Rate).mapToInt(Integer::intValue).sum() / (5*projectRates.size()));
		model.addAttribute("type5Rate", projectRates.size() == 0?0:100* projectRates.stream().map(ProjectRate::getType5Rate).mapToInt(Integer::intValue).sum() / (5*projectRates.size()));
		model.addAttribute("totalIncomeAmount", totalIncomeAmount);
		model.addAttribute("page", page);
		model.addAttribute("items", projects);
		model.addAttribute("inProgressProjectCount", inProgressProjectCount);
		model.addAttribute("favoriteProjectCount", favoriteProjectCount);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);

		return "freelancer/project/completedList";
	}

	// Dispatcher
	@GetMapping("/pickList")
	public String pickList(
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) final int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) final int pageSize,
			Model model) {
		Page<ProjectFavorite> page = projectFavoriteRepository.findAll(ProjectFavoriteSpecifications.filter(getSessionUserId()), PageRequest.of(pageNumber, pageSize));
		setPaginationModelData(model, pageNumber, page);

		return "freelancer/project/pickList";
	}

	@GetMapping("/onGoingList")
	public String freelancerProjectOnGoingList(
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) final int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) final int pageSize,
			@RequestParam(value = "projectId", required = false) final Long projectId, final HttpServletRequest request,
			Model model) {

		User user = userService.getCurrentUser();
		Long userId = user.getId();

		Page<ProjectBid> page = projectBidRepository.findAll(ProjectBidSpecifications.filter(
				getSessionUserId(), null, null, ProjectBid.BidStatus.PICKED, Project.Status.IN_PROGRESS, null, SortBy.CREATED_AT),
				PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "project.startAt")));

		List<Project> projects = page.getContent().stream().map(ProjectBid::getProject).collect(Collectors.toList());

		for (Project project : projects) {
			if (Project.Type.PROJECT.equals(project.getProjectType())) {
				project.setPaymentToUsers(paymentToUserRepository.findByProjectIdOrderByCreatedAtDesc(project.getId()));
				if (!project.getPaymentToUsers().isEmpty()) {
					project.setLastPaymentToUser(project.getPaymentToUsers().get(0));
				}
				project.setProjectComplete(projectCompleteRepository.findByProjectId(project.getId()));
			} else if (Project.Type.CONTEST.equals(project.getProjectType())) {
				ProjectBid myBid = projectBidService.getMyContestEntry(userId, project.getId());
				List<String> thumbUrl = projectBidService.getAllContestEntryFile(myBid.getId()).stream().map(ContestEntryFile::getFileUrl).collect(Collectors.toList());
				project.setEntryFileThumbnailUrl(thumbUrl);
				project.setMyPrizeMoney(myBid.getAmountOfMoney());
				project.setMyProjectBid(myBid);
			}
			project.setMessageCountByClient(
							(project.getContractedFreelancer() != null?(projectCommentRepository.countByProjectIdAndUserId(project.getId(), project.getPostingClient().getId())):0)
			);
			project.setPayedPaymentToUsers(paymentToUserRepository.findByProjectIdAndStatus(project.getId(), PaymentToUser.Status.PAYED));
		}

		model.addAttribute("page", page);
		model.addAttribute("projects", projects);
		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("pageSize", pageSize);

		return "freelancer/project/onGoingList";
	}
}