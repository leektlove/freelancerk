package com.freelancerk.controller.view;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.Category;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.ProjectFavorite;
import com.freelancerk.domain.ProjectItemTicket;
import com.freelancerk.domain.ProjectProductItemType;
import com.freelancerk.domain.ProjectProposition;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectFavoriteRepository;
import com.freelancerk.domain.repository.ProjectItemTicketRepository;
import com.freelancerk.domain.repository.ProjectProductItemTypeRepository;
import com.freelancerk.domain.repository.ProjectPropositionRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.specification.ProjectSpecifications;
import com.freelancerk.model.SortBy;
import com.freelancerk.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/freelancer/findProject")
public class FreelancerFindProjectViewController extends RootController {

	@Value("${server.url}") String serverUrl;
	private UserService userService;
	private ProjectRepository projectRepository;
	private CategoryRepository categoryRepository;
	private ProjectBidRepository projectBidRepository;
	private ProjectFavoriteRepository projectFavoriteRepository;
	private ProjectItemTicketRepository projectItemTicketRepository;
	private ProjectPropositionRepository projectPropositionRepository;
	private ProjectProductItemTypeRepository projectProductItemTypeRepository;

	@Autowired
	public FreelancerFindProjectViewController(UserService userService, ProjectRepository projectRepository,
											   CategoryRepository categoryRepository,
											   ProjectBidRepository projectBidRepository,
											   ProjectFavoriteRepository projectFavoriteRepository,
											   ProjectItemTicketRepository projectItemTicketRepository,
											   ProjectPropositionRepository projectPropositionRepository,
											   ProjectProductItemTypeRepository projectProductItemTypeRepository) {
		this.userService = userService;
		this.projectRepository = projectRepository;
		this.categoryRepository = categoryRepository;
		this.projectBidRepository = projectBidRepository;
		this.projectFavoriteRepository = projectFavoriteRepository;
		this.projectItemTicketRepository = projectItemTicketRepository;
		this.projectPropositionRepository = projectPropositionRepository;
		this.projectProductItemTypeRepository = projectProductItemTypeRepository;
	}

	@GetMapping("/list")
	public String list(Model model, @RequestParam(value = "keyword", required = false) String keyword,
					   @RequestParam(value = "projectType", required = false) Project.Type projectType,
					   @RequestParam(value = "category", required = false) Long categoryId,
					   @RequestParam(value = "sortBy", required = false, defaultValue = "KEYWORDS") SortBy sortBy,
					   @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
					   @RequestParam(value = "pageSize", required = false, defaultValue = "48") Integer pageSize) {
		List<ProjectProductItemType> externalItems = projectProductItemTypeRepository.findByCode(ProjectProductItemType.Code.EXTERNAL);
		externalItems.addAll(projectProductItemTypeRepository.findByCode(ProjectProductItemType.Code.INTERNAL));

		List<ProjectProductItemType> urgentItems = projectProductItemTypeRepository.findByUrgentTrue();

		if (isLoggedIsAsClient()) {
			return String.format("redirect:%s/main", serverUrl);
		}

		User user = userService.getCurrentUser();
		Set<Category> freelancerSectors = user.getCategories();

		ProjectProductItemType productItemUrgentItemType = projectProductItemTypeRepository.findByCodeAndProjectType(ProjectProductItemType.Code.EXTERNAL_URGENT, Project.Type.PROJECT);

		Page<Project> page = projectRepository.findAll(
				ProjectSpecifications.filterSpecificSearch(
						keyword, Project.Status.POSTED, projectType, categoryId,
						sortBy, productItemUrgentItemType, externalItems, new HashSet<>()),
				PageRequest.of(pageNumber, pageSize));

		List<Long> likedProjectIds = projectFavoriteRepository.findByUserId(getSessionUserId()).stream().map(ProjectFavorite::getProject).map(Project::getId).collect(Collectors.toList());

		for (Project item: page) {
			ProjectBid projectBid = projectBidRepository.findTop1ByParticipantIdAndProjectIdOrderByCreatedAtDesc(getSessionUserId(), item.getId());
			ProjectProposition projectProposition = projectPropositionRepository.findByProjectIdAndFreelancerId(item.getId(), getSessionUserId());
			item.setDenyable(projectProposition != null && "PROPOSE".equalsIgnoreCase(projectProposition.getStatus().name()) && projectBid == null);
			item.setLiked(likedProjectIds.contains(item.getId()));
			List<Long> projectProductItemIds = projectItemTicketRepository.findByProjectIdAndInvalidFalseAndExpiredAtAfterOrderByExpiredAtAsc(item.getId(), LocalDateTime.now())
					.stream().map(ProjectItemTicket::getProjectProductItemType).map(ProjectProductItemType::getId).collect(Collectors.toList());
			item.setUrgency(projectProductItemIds.stream().anyMatch((o) -> urgentItems.stream().map(ProjectProductItemType::getId).collect(Collectors.toList()).contains(o)));
			if (isLoggedIn() && !isLoggedIsAsClient()) {
				item.setMyProjectBid(projectBidRepository.findTop1ByParticipantIdAndProjectIdAndBidStatusNotOrderByCreatedAtDesc(getSessionUserId(), item.getId(), ProjectBid.BidStatus.CANCEL));
			}
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("loggedIn", authentication instanceof User);
		if (authentication instanceof User) {
			model.addAttribute("loginAsClient", ((User) authentication).isLoginAsClient());
		}

		model.addAttribute("freelancerSectors", freelancerSectors);

		model.addAttribute("sortBy", sortBy);
		if (categoryId != null) {
			model.addAttribute("selectedCategory", categoryRepository.getOne(categoryId));
		}
		model.addAttribute("keyword", keyword);
		model.addAttribute("projectType", projectType);
		model.addAttribute("categories", categoryRepository.findByParentCategoryIsNullOrderBySeqAsc());

		model.addAttribute("page", page);

		return "freelancer/findProject/list";
	}
}
