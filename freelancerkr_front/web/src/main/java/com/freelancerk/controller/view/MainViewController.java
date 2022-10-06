package com.freelancerk.controller.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.Advertisement;
import com.freelancerk.domain.DailyAccessLog;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.PickMeUpTicket;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.ProjectFavorite;
import com.freelancerk.domain.ProjectItemTicket;
import com.freelancerk.domain.ProjectProductItemType;
import com.freelancerk.domain.ProjectProposition;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.AdvertisementRepository;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.DailyAccessLogRepository;
import com.freelancerk.domain.repository.NoticeRepository;
import com.freelancerk.domain.repository.PickMeUpTicketRepository;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectFavoriteRepository;
import com.freelancerk.domain.repository.ProjectItemTicketRepository;
import com.freelancerk.domain.repository.ProjectProductItemTypeRepository;
import com.freelancerk.domain.repository.ProjectPropositionRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.domain.specification.PickMeUpTicketSpecifications;
import com.freelancerk.domain.specification.ProjectItemTicketSpecifications;
import com.freelancerk.domain.specification.ProjectSpecifications;
import com.freelancerk.service.PickMeUpService;
import com.freelancerk.type.NoticeType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MainViewController extends RootController {

    private UserRepository userRepository;
    private PickMeUpService pickMeUpService;
    private NoticeRepository noticeRepository;
    private ProjectRepository projectRepository;
    private CategoryRepository categoryRepository;
    private ProjectBidRepository projectBidRepository;
    private AdvertisementRepository advertisementRepository;
    private DailyAccessLogRepository dailyAccessLogRepository;
    private PickMeUpTicketRepository pickMeUpTicketRepository;
    private ProjectFavoriteRepository projectFavoriteRepository;
    private ProjectItemTicketRepository projectItemTicketRepository;
    private ProjectPropositionRepository projectPropositionRepository;
    private ProjectProductItemTypeRepository projectProductItemTypeRepository;

    @Autowired
    public MainViewController(UserRepository userRepository, PickMeUpService pickMeUpService, NoticeRepository noticeRepository,
                              ProjectRepository projectRepository,
                              CategoryRepository categoryRepository, ProjectBidRepository projectBidRepository,
                              AdvertisementRepository advertisementRepository,
                              DailyAccessLogRepository dailyAccessLogRepository,
                              PickMeUpTicketRepository pickMeUpTicketRepository,
                              ProjectFavoriteRepository projectFavoriteRepository,
                              ProjectItemTicketRepository projectItemTicketRepository,
                              ProjectPropositionRepository projectPropositionRepository,
                              ProjectProductItemTypeRepository projectProductItemTypeRepository) {
        this.userRepository = userRepository;
        this.pickMeUpService = pickMeUpService;
        this.noticeRepository = noticeRepository;
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.projectBidRepository = projectBidRepository;
        this.advertisementRepository = advertisementRepository;
        this.dailyAccessLogRepository = dailyAccessLogRepository;
        this.pickMeUpTicketRepository = pickMeUpTicketRepository;
        this.projectFavoriteRepository = projectFavoriteRepository;
        this.projectItemTicketRepository = projectItemTicketRepository;
        this.projectPropositionRepository = projectPropositionRepository;
        this.projectProductItemTypeRepository = projectProductItemTypeRepository;
    }

    @RequestMapping(value = {"/"})
    public String getIntroView(Model model) {
        return "main/intro";
    }
    
    @RequestMapping(value = {"/main"})
    public String getMainView(Model model,
                              HttpServletRequest request,
                              @RequestParam(value = "message", required = false) String message,
                              @RequestParam(value = "category1stId", required = false) Long category1stId,
                              @RequestParam(value = "category2ndId", required = false) Long category2ndId) {

        DailyAccessLog dailyAccessLog = new DailyAccessLog();
        dailyAccessLog.setAccessTime(LocalTime.now());
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = request.getRemoteAddr();
        dailyAccessLog.setRemoteAddress(ip);
        dailyAccessLog.setReferer(request.getHeader("referer"));
        dailyAccessLog.setUserAgent(request.getHeader("User-Agent"));
        dailyAccessLog.setDate(LocalDate.now());
        if (isLoggedIn()) {
            dailyAccessLog.setUser(userRepository.getOne(getSessionUserId()));
        }
        dailyAccessLogRepository.save(dailyAccessLog);


        List<ProjectProductItemType> priorityItems = projectProductItemTypeRepository.findByPriorityTrueAndCategory(ProjectProductItemType.Category.EXTERNAL);
        List<ProjectProductItemType> urgentItems = projectProductItemTypeRepository.findByUrgentTrueAndCategory(ProjectProductItemType.Category.EXTERNAL);
        List<ProjectProductItemType> externalItems = projectProductItemTypeRepository.findByCode(ProjectProductItemType.Code.EXTERNAL);

        List<PickMeUp> pickMeUpPriorities
                = pickMeUpTicketRepository.findAll(
                PickMeUpTicketSpecifications.filter(
                        category1stId != null ? categoryRepository.getOne(category1stId) : null,
                        category2ndId != null ? categoryRepository.getOne(category2ndId) : null,
                        null, null, false),
                PageRequest.of(0, 24)).getContent()
                .stream()
                .map(PickMeUpTicket::getPickMeUp)
                .collect(Collectors.toList());

        for (PickMeUp pickMeUp : pickMeUpPriorities) {
            pickMeUpService.setValidTicketItem(pickMeUp);
        }

        List<Project> projects = projectItemTicketRepository
                .findAll(ProjectItemTicketSpecifications.filter(externalItems, Arrays.asList(Project.Status.IN_PROGRESS, Project.Status.COMPLETED, Project.Status.CANCELLED), true, null, false))
                .stream().map(ProjectItemTicket::getProject).collect(Collectors.toList());

        List<Long> likedProjectIds = new ArrayList<>();
        if (isLoggedIn()) {
            likedProjectIds = projectFavoriteRepository.findByUserId(getSessionUserId()).stream().map(ProjectFavorite::getProject).map(Project::getId).collect(Collectors.toList());
        }
        for (Project project : projects) {
            List<Long> projectProductItemIds = projectItemTicketRepository.findByProjectIdAndInvalidFalseAndExpiredAtAfterOrderByExpiredAtAsc(project.getId(), LocalDateTime.now())
                    .stream().map(ProjectItemTicket::getProjectProductItemType).map(ProjectProductItemType::getId).collect(Collectors.toList());
            project.setUrgency(projectProductItemIds.stream().anyMatch((o) -> urgentItems.stream().map(ProjectProductItemType::getId).collect(Collectors.toList()).contains(o)));
            project.setLiked(likedProjectIds.contains(project.getId()));
            ProjectBid projectBid = null;
            ProjectProposition projectProposition = null;
            if (isLoggedIn()) {
                projectBid = projectBidRepository.findTop1ByParticipantIdAndProjectIdOrderByCreatedAtDesc(getSessionUserId(), project.getId());
                projectProposition = projectPropositionRepository.findByProjectIdAndFreelancerId(project.getId(), getSessionUserId());
                project.setDenyable(projectProposition != null && "PROPOSE".equalsIgnoreCase(projectProposition.getStatus().name()) && projectBid == null);
            }

            if (isLoggedIn() && !isLoggedIsAsClient()) {
                project.setMyProjectBid(projectBidRepository.findTop1ByParticipantIdAndProjectIdAndBidStatusNotOrderByCreatedAtDesc(getSessionUserId(), project.getId(), ProjectBid.BidStatus.CANCEL));
            }
        }

        model.addAttribute("message", message);
        model.addAttribute("category1stId", category1stId);
        model.addAttribute("category2ndId", category2ndId);

        model.addAttribute("totalUserCount", userRepository.count());
        model.addAttribute("totalProjectCount", projectRepository.countByStatusNotIn(Arrays.asList(Project.Status.VOLATILE, Project.Status.TEMP)));

        model.addAttribute("pickMeUps", pickMeUpPriorities);
        ;
        model.addAttribute("pickMeUpIds", StringUtils.join(pickMeUpPriorities.stream().map(PickMeUp::getId).collect(Collectors.toList()), ","));

        model.addAttribute("projects", projects);

        model.addAttribute("category1stList", categoryRepository.findByParentCategoryIsNullOrderBySeqAsc());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("loggedIn", authentication instanceof User);
        if (authentication instanceof User) {
            User loggedInUser = ((User) authentication);
            model.addAttribute("loginAsClient", loggedInUser.isLoginAsClient());

            if (loggedInUser.isLoginAsClient()) {
                model.addAttribute("postedProjectList", projectRepository.findAll(ProjectSpecifications.filter(loggedInUser.getId(), null, Project.Status.POSTED, null, null)));
            }
        }

        model.addAttribute("lalaFreelancerPosts", noticeRepository.findByTypeOrderByIdDesc(NoticeType.LALA));

        List<Advertisement> advertisements = advertisementRepository.findByStartAtLessThanEqualAndEndAtGreaterThan(LocalDate.now(), LocalDate.now());
        model.addAttribute("advertisementActive", !advertisements.isEmpty());

        return "main/main";
    }

    @RequestMapping("/findPassword")
    public String getFindPasswordView(Model model) {
        return "main/findPassword";
    }

    @RequestMapping("/aboutus")
    public String getAboutUsView(Model model) {
        return "main/aboutUs";
    }
    
    @RequestMapping("/infoPosting")
    public String getInfoPostingView(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("loggedIn", authentication instanceof User);
        if (authentication instanceof User) {
            User loggedInUser = ((User) authentication);
            model.addAttribute("loginAsClient", loggedInUser.isLoginAsClient());

        }

        return "main/infoPosting";
    }
    
    @RequestMapping("/lalaBlog")
    public String lalaBlog(Model model) {
        return "main/lalaBlog";
    }
}
