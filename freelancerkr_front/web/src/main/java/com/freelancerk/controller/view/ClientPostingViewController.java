package com.freelancerk.controller.view;

import com.freelancerk.ProjectViewMode;
import com.freelancerk.TimeUtil;
import com.freelancerk.controller.RootController;
import com.freelancerk.controller.io.OptionNamePrice;
import com.freelancerk.controller.io.ProjectProductOptionResponse;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.service.MessageService;
import com.freelancerk.service.ProjectService;
import com.freelancerk.service.UserService;
import com.freelancerk.util.OptionDiscountCalculator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client/posting")
public class ClientPostingViewController extends RootController {

    @Value("${server.url}") String serverUrl;
    private UserService userService;
    private UserRepository userRepository;
    private ProjectService projectService;
    private MessageService messageService;
    private ProjectRepository projectRepository;
    private ProjectCategoryRepository projectCategoryRepository;
    private ProjectProductItemTypeRepository projectProductItemTypeRepository;
    private ContestSectorMetaTypeRepository contestSectorMetaTypeRepository;
    private ProjectItemTicketRepository projectItemTicketRepository;
    private ContestSectorRepository contestSectorRepository;
    private BankTypeRepository bankTypeRepository;

    @Autowired
    public ClientPostingViewController(UserService userService, UserRepository userRepository, ProjectService projectService,
                                       ProjectRepository projectRepository, MessageService messageService,
                                       ProjectProductItemTypeRepository projectProductItemTypeRepository,
                                       ContestSectorMetaTypeRepository contestSectorMetaTypeRepository,
                                       ProjectCategoryRepository projectCategoryRepository,
                                       ProjectItemTicketRepository projectItemTicketRepository,
                                       ContestSectorRepository contestSectorRepository, BankTypeRepository bankTypeRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.messageService = messageService;
        this.projectRepository = projectRepository;
        this.projectCategoryRepository = projectCategoryRepository;
        this.projectProductItemTypeRepository = projectProductItemTypeRepository;
        this.contestSectorMetaTypeRepository = contestSectorMetaTypeRepository;
        this.projectItemTicketRepository = projectItemTicketRepository;
        this.contestSectorRepository = contestSectorRepository;
        this.bankTypeRepository = bankTypeRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/project/uuid")
    public String getProjectView(Model model, @RequestParam(value = "id", required = false) Long id,
                                 @RequestParam(value = "token", required = false) String token,
                                 @RequestParam(value = "uuid", required = false) String uuid,
                                 @RequestParam(value = "mode", required = false) ProjectViewMode mode) {
        if (!isLoggedIn()) {
            return String.format("redirect:%s/auth/login", serverUrl);
        }
        Project project = null;
        if (id != null) {
            project = projectService.getById(id);
            model.addAttribute("item", project);
        } else if (StringUtils.isNotEmpty(uuid)) {
            project = projectRepository.findByUuid(uuid);
            model.addAttribute("item", project);
        }

        if (mode != null) {
            model.addAttribute("mode", mode.name());
        }
        List<ProjectCategory> projectCategories = projectCategoryRepository.findByProjectId(project.getId());
        Map<Category, List<Category>> categoryMap = new HashMap<>();

        for (ProjectCategory projectCategory: projectCategories) {
            categoryMap.computeIfAbsent(projectCategory.getCategory().getParentCategory(), k -> new ArrayList<>());
            List<Category> projectCategoryList = categoryMap.get(projectCategory.getCategory().getParentCategory());
            projectCategoryList.add(projectCategory.getCategory());
        }

        User user = userRepository.getOne(getSessionUserId());
        if (user.isSampleLogoImage()) {
            user.setLogoImageUrl(null);
        }
        user.setPoints(userService.getPoints(user.getId(), ((User) SecurityContextHolder.getContext().getAuthentication()).isLoginAsClient()?User.Role.ROLE_CLIENT:User.Role.ROLE_FREELANCER));

        model.addAttribute("user", user);
        model.addAttribute("token", token);
        model.addAttribute("categoryMap", categoryMap);
        model.addAttribute("projectCategories", projectCategories);

        model.addAttribute("allBanks", bankTypeRepository.findAll());

        return "client/posting/project/view";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/project")
    public String getIndexView(RedirectAttributes attributes, @RequestParam(value = "id", required = false) Long id,
                               @RequestParam(value = "token", required = false) String token,
                               @RequestParam(value = "mode", required = false) ProjectViewMode mode) {
        String uuid = null;

        if (id == null) {
            Project project = new Project();
            project.setUuid(UUID.randomUUID().toString());
            project.setProjectType(Project.Type.PROJECT);
            project.setStatus(Project.Status.VOLATILE);
            project.setUseEscrow(true);
            project.setPostingClient(userRepository.getOne(getSessionUserId()));
            project = projectRepository.save(project);

            uuid = project.getUuid();

            return String.format("redirect:%s/client/posting/project/uuid?mode=" + (mode!=null?mode:"") + "&uuid=" + uuid + "&token=" + token, serverUrl);
        } else {
            return String.format("redirect:%s/client/posting/project/uuid?id=" + id + "&mode=" + (mode!=null?mode:"") + "&token=" + token, serverUrl);
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/projectOption")
    public String getOptionView(Model model, @RequestParam("projectId") Long projectId,
                                @RequestParam(value = "token", required = false) String token,
                                @RequestParam(value = "mode", required = false) String mode) {
        Project project = projectService.getById(projectId);

        if (!Project.Status.TEMP.equals(project.getStatus()) && !"extend".equalsIgnoreCase(mode)) {
            return String.format("redirect:%s/client/posting/project", serverUrl);
        }
        model.addAttribute("token", token);
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", project);
        model.addAttribute("options", projectProductItemTypeRepository.findByPackFalseAndValidTrueAndProjectType(Project.Type.PROJECT));

        if ("EXTEND".equalsIgnoreCase(mode)) {
            Map<Long, ProjectItemTicket> productTypeIdTicketMap = new LinkedHashMap<>();
            Map<Long, String> projectOptionIdEndAtMap = new LinkedHashMap<>();

            List<ProjectItemTicket> projectItemTicketList = projectItemTicketRepository.findByProjectId(projectId);
            for (ProjectItemTicket ticket: projectItemTicketList) {
                productTypeIdTicketMap.put(ticket.getProjectProductItemType().getId(), ticket);
                projectOptionIdEndAtMap.put(ticket.getProjectProductItemType().getId(), TimeUtil.convertLocalDateToStr(ticket.getExpiredAt().toLocalDate()));
            }
            List<Long> projectProductItemIds = projectItemTicketList
                    .stream().map(ProjectItemTicket::getProjectProductItemType).map(ProjectProductItemType::getId).collect(Collectors.toList());
            project.setProjectOptionItemIds(projectProductItemIds);
            project.setProjectOptionTicketEndAtMap(projectOptionIdEndAtMap);
            model.addAttribute("mode", "EXTEND");
            model.addAttribute("productTypeIdTicketMap", productTypeIdTicketMap);
        }

        return "client/posting/project/option";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/projectPayment")
    public String getPaymentView(@RequestParam("projectId") Long projectId,
                                 @RequestParam(value = "mode", required = false) String mode,
                                 @RequestParam(value = "postingPeriodExtend", required = false) Boolean postingPeriodExtend,
                                 @RequestParam(value = "postingEndAt", required = false) String postingEndAt,
                                 @RequestParam(value = "projectPaymentOptionId[]", required = false) Long[] optionIds,
                                 @RequestParam(value = "projectPaymentOptionCount[]", required = false) Integer[] optionCounts,
                                 @RequestParam("optionAmountOfMoney") Integer optionAmountOfMoney,
                                 Model model) {
        Project project = projectService.getById(projectId);

        if (!Project.Status.TEMP.equals(project.getStatus()) && !"EXTEND".equalsIgnoreCase(mode)) {
            return String.format("redirect:%s/client/posting/project", serverUrl);
        }

        List<String> freeOptionNames = new ArrayList<>();
        List<OptionNamePrice> optionNamePriceList = new ArrayList<>();
        List<ProjectProductItemType> itemTypeList = projectProductItemTypeRepository.findByIdIn(optionIds);

        int i = 0;
        int chargedOptionCount = 0;
        for (ProjectProductItemType type: itemTypeList) {
            if (type.getMountOfMoneyUnit() == 0) {
                i++;
                freeOptionNames.add(type.getName());
                continue;
            }
            chargedOptionCount += optionCounts[i];
            LocalDateTime expiredAt = LocalDateTime.now().plusWeeks(optionCounts[i]);
            String optionValidationSpan = type.getMountOfMoneyUnit() == 0?"무료적용":TimeUtil.convertLocalDateTimeToStrWithTime(expiredAt);
            optionNamePriceList.add(
                    OptionNamePrice.builder()
                            .optionName(type.getName())
                            .optionValidationSpan(optionValidationSpan)
                            .optionPrice((long) (type.getMountOfMoneyUnit() * optionCounts[i]))
                            .optionIncludedInPack(false)
                            .optionCount(optionCounts[i])
                            .build());
            i++;
        }

        int totalOptionPrice = projectService.calculateOptionPrice(itemTypeList, optionCounts, Project.Type.PROJECT);
        int discountAmount = OptionDiscountCalculator.getOptionDiscountAmountForClient(chargedOptionCount, totalOptionPrice);
        int supplyAmount = totalOptionPrice - discountAmount;

        ProjectProductOptionResponse response =  ProjectProductOptionResponse.builder()
                .optionNamePriceList(optionNamePriceList)
                .totalChargedOptionPrice(supplyAmount)
                .totalDiscountOptionPrice(discountAmount)
                .vat((int) (supplyAmount*0.1))
                .totalPrice((int) (supplyAmount + supplyAmount*0.1))
                .build();

        model.addAttribute("freeChargedOptionNames", String.join(",", freeOptionNames));
        model.addAttribute("freeChargedOptionValidationSpan", postingEndAt);
        model.addAttribute("data", response);
        model.addAttribute("user", userRepository.getOne(getSessionUserId()));
        model.addAttribute("serverUrl", serverUrl);
        model.addAttribute("project", project);
        if (StringUtils.isNotEmpty(mode)) {
            model.addAttribute("mode", mode);
        }

        int maxOptionCount = 0;
        if (optionIds != null) {
            i = 0;
            Map<Long, Integer> optionIdCountMap = new LinkedHashMap<>();
            for (Long optionId: optionIds) {
                if (maxOptionCount < optionCounts[i]) {
                    maxOptionCount = optionCounts[i];
                }
                optionIdCountMap.put(optionId, optionCounts[i]);
                i++;
            }
            Gson gson = new GsonBuilder().create();
            String jsonString = gson.toJson(optionIdCountMap);

            long days = project.getPostingStartAt().until(project.getPostingEndAt(), ChronoUnit.DAYS);
            model.addAttribute("optionLongerThanPostingPeriod", maxOptionCount*7 > days);
            model.addAttribute("optionIdCountJson", jsonString);
        }

        model.addAttribute("optionAmountOfMoney", optionAmountOfMoney);
        model.addAttribute("postingEndAt", postingEndAt);
        model.addAttribute("postingPeriodExtend", postingPeriodExtend);

        return "client/posting/project/payment";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/projectPaymentDone")
    public String getPaymentDoneView(Model model, @RequestParam(value = "mode", required = false) ProjectViewMode[] mode) {

        ProjectViewMode newMode = null;
        if (mode != null) {
            for (ProjectViewMode viewMode: mode) {
                if (viewMode != null) {
                    newMode = viewMode;
                }
            }
        } else {
            messageService.sendMessage(userRepository.getOne(getSessionUserId()), AligoKakaoMessageTemplate.Code.TA_3174, null);
        }
        model.addAttribute("mode", newMode);
        return "client/posting/project/paymentDone";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/contest")
    public String getContestView(Model model, HttpServletResponse response,
                                 @RequestParam(value = "id", required = false) Long id,
                                 @RequestParam(value = "withProject", required = false) Boolean withProject) throws IOException {
        if (!isLoggedIn()) {
            return String.format("redirect:%s/auth/login", serverUrl);
        }

        Project contest = null;
        if (withProject != null) {
            model.addAttribute("withProject", withProject);
        }
        if (id != null) {
            List<ContestSector> contestSectors = contestSectorRepository.findByContestId(id);
            List<Long> contestSectorMetaIds = contestSectors.stream()
                    .map(ContestSector::getContestSectorType)
                    .map(ContestSectorType::getContestSectorMetaType)
                    .map(ContestSectorMetaType::getId)
                    .collect(Collectors.toList());
            contest = projectService.getById(id);
            if (!getSessionUserId().equals(contest.getPostingClient().getId())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
            contest.setSectorMetaIds(contestSectorMetaIds);
            contest.setSectorIds(contestSectors.stream().map(ContestSector::getContestSectorType).map(ContestSectorType::getId).collect(Collectors.toList()));
            model.addAttribute("item", contest);
        }

        model.addAttribute("modifyMode", contest != null && !Project.Status.TEMP.equals(contest.getStatus()));
        model.addAttribute("metas", contestSectorMetaTypeRepository.findAll(new Sort(Sort.Direction.ASC, "seq")));
        return "client/posting/contest/view";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/contestOption")
    public String getContestOptionView(@RequestParam("contestId") Long contestId, @RequestParam(value = "mode", required = false) String mode,
                                       Model model) {
        Map<Long, ProjectItemTicket> productTypeIdTicketMap = new LinkedHashMap<>();
        Map<Long, String> projectOptionIdEndAtMap = new LinkedHashMap<>();

        Project project = projectService.getById(contestId);

        model.addAttribute("project", project);
        model.addAttribute("projectId", project.getId());
        model.addAttribute("contestId", contestId);
        model.addAttribute("serverUrl", serverUrl);
        model.addAttribute("minPrize", project.getMinPrize());
        model.addAttribute("contestName", project.getTitle());
        model.addAttribute("options", projectProductItemTypeRepository.findByPackFalseAndValidTrueAndProjectType(Project.Type.CONTEST));

        if ("EXTEND".equalsIgnoreCase(mode)) {
            List<ProjectItemTicket> projectItemTicketList = projectItemTicketRepository.findByProjectIdAndInvalidFalseAndExpiredAtAfterOrderByExpiredAtAsc(contestId, LocalDateTime.now());
            for (ProjectItemTicket ticket: projectItemTicketList) {
                productTypeIdTicketMap.put(ticket.getProjectProductItemType().getId(), ticket);
                projectOptionIdEndAtMap.put(ticket.getProjectProductItemType().getId(), TimeUtil.convertLocalDateToStr(ticket.getExpiredAt().toLocalDate()));
            }
            List<Long> projectProductItemIds = projectItemTicketList
                    .stream().map(ProjectItemTicket::getProjectProductItemType).map(ProjectProductItemType::getId).collect(Collectors.toList());
            project.setProjectOptionItemIds(projectProductItemIds);
            project.setProjectOptionTicketEndAtMap(projectOptionIdEndAtMap);
            model.addAttribute("mode", "EXTEND");
            model.addAttribute("productTypeIdTicketMap", productTypeIdTicketMap);
            return "client/posting/contest/paymentOption";
        }

        return "client/posting/contest/option";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/contestPayment")
    public String getContestOptionPaymentConfirmView(@RequestParam("projectId") Long projectId,
                                 @RequestParam(value = "mode", required = false) String mode,
                                 @RequestParam(value = "projectPaymentOptionId[]", required = false) Long[] optionIds,
                                 @RequestParam(value = "projectPaymentOptionCount[]", required = false) Integer[] optionCounts,
                                 @RequestParam(value = "projectPaymentOptionIncludedInPack[]", required = false) Boolean[] optionIncludedInPacks,
                                 Model model) {
        Project project = projectService.getById(projectId);

        if (!Project.Status.TEMP.equals(project.getStatus()) && !"EXTEND".equalsIgnoreCase(mode)) {
            return String.format("redirect:%s/client/posting/contest", serverUrl);
        }

        List<OptionNamePrice> optionNamePriceList = new ArrayList<>();
        List<ProjectProductItemType> itemTypeList = projectProductItemTypeRepository.findByIdIn(optionIds);

        int i = 0;
        int chargedOptionCount = 0;
        for (ProjectProductItemType type: itemTypeList) {
            if (type.getMountOfMoneyUnit() == 0) {
                i++;
                continue;
            }
            chargedOptionCount++;
            optionNamePriceList.add(
                    OptionNamePrice.builder()
                            .optionName(type.getName())
                            .optionPrice((long) (type.getMountOfMoneyUnit() * optionCounts[i]))
                            .optionIncludedInPack(optionIncludedInPacks[i])
                            .build());
            i++;
        }

        int totalOptionPrice = projectService.calculateOptionPrice(itemTypeList, optionCounts, Project.Type.CONTEST);

        int discountAmount = 0;

        if (chargedOptionCount == 2) {
            discountAmount = (int) (totalOptionPrice * 0.1);
        } else if (chargedOptionCount == 3) {
            discountAmount = (int) (totalOptionPrice * 0.15);
        } else if (chargedOptionCount >= 4) {
            discountAmount = (int) (totalOptionPrice * 0.20);
        }

        int supplyAmount = totalOptionPrice - discountAmount;

        ProjectProductOptionResponse response =  ProjectProductOptionResponse.builder()
                .optionNamePriceList(optionNamePriceList)
                .totalChargedOptionPrice(supplyAmount)
                .totalDiscountOptionPrice(discountAmount)
                .vat((int) (supplyAmount*0.1))
                .totalPrice((int) (supplyAmount + supplyAmount*0.1))
                .build();

        model.addAttribute("data", response);
        model.addAttribute("user", userRepository.getOne(getSessionUserId()));
        model.addAttribute("serverUrl", serverUrl);
        model.addAttribute("project", project);
        if (StringUtils.isNotEmpty(mode)) {
            model.addAttribute("mode", mode);
        }

        int maxOptionCount = 0;
        if (optionIds != null) {
            i = 0;
            Map<Long, Integer> optionIdCountMap = new LinkedHashMap<>();
            for (Long optionId: optionIds) {
                if (maxOptionCount < optionCounts[i]) {
                    maxOptionCount = optionCounts[i];
                }
                optionIdCountMap.put(optionId, optionCounts[i]);
                i++;
            }
            Gson gson = new GsonBuilder().create();
            String jsonString = gson.toJson(optionIdCountMap);

            long days = project.getPostingStartAt().until(project.getPostingEndAt(), ChronoUnit.DAYS);
            model.addAttribute("optionLongerThanPostingPeriod", maxOptionCount*7 > days);
            model.addAttribute("optionIdCountJson", jsonString);
        }

        return "client/posting/project/paymentOptionConfirm";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/contestPaymentDone")
    public String getContestPaymentDoneView(Model model, @RequestParam(value = "mode", required = false) ProjectViewMode mode) {

        if (mode == null) {
            messageService.sendMessage(userRepository.getOne(getSessionUserId()), AligoKakaoMessageTemplate.Code.TA_3175, null);
        }

        model.addAttribute("mode", mode);
        return "client/posting/contest/paymentDone";
    }
}
