package com.freelancerk.controller.view;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.ContactAvailableDayTime;
import com.freelancerk.domain.DirectDeal;
import com.freelancerk.domain.FreelancerProductItemType;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.PickMeUpMemo;
import com.freelancerk.domain.PickMeUpTicket;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.ProjectRate;
import com.freelancerk.domain.repository.ContactAvailableDayTimeRepository;
import com.freelancerk.domain.repository.DirectDealRepository;
import com.freelancerk.domain.repository.FreelancerProductItemTypeRepository;
import com.freelancerk.domain.repository.PickMeUpMemoRepository;
import com.freelancerk.domain.repository.PickMeUpTicketRepository;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectRateRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.domain.specification.DirectDealSpecifications;
import com.freelancerk.domain.specification.ProjectSpecifications;
import com.freelancerk.model.ContactAvailableDayTimeModel;

@Controller
@RequestMapping("/client/directMarket")
public class ClientDirectMarketViewController extends RootController {

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private DirectDealRepository directDealRepository;
    private ProjectBidRepository projectBidRepository;
    private ProjectRateRepository projectRateRepository;
    private PickMeUpMemoRepository pickMeUpMemoRepository;
    private PickMeUpTicketRepository pickMeUpTicketRepository;
    private ContactAvailableDayTimeRepository contactAvailableDayTimeRepository;
    private FreelancerProductItemTypeRepository freelancerProductItemTypeRepository;

    @Autowired
    public ClientDirectMarketViewController(DirectDealRepository directDealRepository, ProjectRepository projectRepository,
                                            PickMeUpMemoRepository pickMeUpMemoRepository, ProjectBidRepository projectBidRepository,
                                            UserRepository userRepository, ProjectRateRepository projectRateRepository,
                                            PickMeUpTicketRepository pickMeUpTicketRepository, ContactAvailableDayTimeRepository contactAvailableDayTimeRepository,
                                            FreelancerProductItemTypeRepository freelancerProductItemTypeRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectBidRepository = projectBidRepository;
        this.directDealRepository = directDealRepository;
        this.projectRateRepository = projectRateRepository;
        this.pickMeUpMemoRepository = pickMeUpMemoRepository;
        this.pickMeUpTicketRepository = pickMeUpTicketRepository;
        this.contactAvailableDayTimeRepository = contactAvailableDayTimeRepository;
        this.freelancerProductItemTypeRepository = freelancerProductItemTypeRepository;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.GET, value = "/index")
    public String getClientDirectMarket(Model model) {

        FreelancerProductItemType pickMeUpDirectDealItem = freelancerProductItemTypeRepository.findByCode(FreelancerProductItemType.Code.DIRECT_DEAL);

        Page<DirectDeal> directDeals = directDealRepository.findAll(DirectDealSpecifications.filter(
                getSessionUserId(), pickMeUpDirectDealItem), PageRequest.of(0, 12, new Sort(Sort.Direction.DESC, "id")));

        for (DirectDeal directDeal: directDeals.getContent()) {
            PickMeUp pickMeUp = directDeal.getPickMeUp();
            List<FreelancerProductItemType> pickMeUpPayProducts
                    = pickMeUpTicketRepository.findByPickMeUpIdAndEndAtAfterOrderByEndAtAsc(pickMeUp.getId(), LocalDateTime.now())
                    .stream()
                    .map(PickMeUpTicket::getFreelancerProductItemType)
                    .collect(Collectors.toList());

            pickMeUp.setDirectDealAvailable(pickMeUpPayProducts.contains(pickMeUpDirectDealItem));
            ContactAvailableDayTime contactAvailableDayTime = contactAvailableDayTimeRepository.findByPickMeUpId(pickMeUp.getId());
            if (contactAvailableDayTime != null) {
                List<ContactAvailableDayTimeModel> dayTimeModels = new ArrayList<>();
                for (String key: contactAvailableDayTime.getDayTimes().keySet()) {
                    if (StringUtils.isEmpty(key)) continue;
                    ContactAvailableDayTimeModel dayTimeModel = new ContactAvailableDayTimeModel();
                    dayTimeModel.setDays(Arrays.asList(key.split(",")));
                    dayTimeModel.setTimes(contactAvailableDayTime.getDayTimes().get(key));
                    dayTimeModels.add(dayTimeModel);
                }
                pickMeUp.setDayTimes(dayTimeModels);
            }

            PickMeUpMemo pickMeUpMemo = pickMeUpMemoRepository.findByUserIdAndPickMeUpId(getSessionUserId(), pickMeUp.getId());
            if (pickMeUpMemo == null) continue;
            pickMeUp.setMemo(pickMeUpMemo.getContent());
        }
        model.addAttribute("data", directDeals.getContent());
        model.addAttribute("pickMeUpIds", directDeals.getContent().stream().map(DirectDeal::getPickMeUp).map(PickMeUp::getId).collect(Collectors.toList()));

        return "client/directMarket/view";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/startWithSelectedSpecialist")
    public String getClientStartWithSelectedSpecialistView(@RequestParam(value = "directDealId", required = false) Long directDealId,
                                                           @RequestParam(value = "projectBidId", required = false) Long projectBidId,
                                                           Model model) {
        String referer = "";
        if (directDealId != null) {
            referer = "DIRECT_MARKET";
        } else if (projectBidId != null) {
            referer = "PROJECT_COMPLETE";
        }

        ProjectBid projectBid = null;
        if (projectBidId != null) {
            projectBid = projectBidRepository.getOne(projectBidId);
        }

        Long freelancerUserId = directDealId != null?directDealRepository.getOne(directDealId).getPickMeUp().getUser().getId():projectBid.getParticipant().getId();

        List<ProjectRate> projectRates =  projectRateRepository.findByRatedUserIdAndRaterTypeAndProjectStatus(
                getSessionUserId(), ProjectRate.RaterType.CLIENT, Project.Status.COMPLETED);
        int rateSum = 0;
        for (ProjectRate projectRate: projectRates) {
            rateSum += projectRate.getType1Rate();
            rateSum += projectRate.getType2Rate();
            rateSum += projectRate.getType3Rate();
            rateSum += projectRate.getType4Rate();
            rateSum += projectRate.getType5Rate();
        }
        long numberOfRatedProjectsAsFreelancer = projectRateRepository.countByRatedUserIdAndRaterType(freelancerUserId, ProjectRate.RaterType.CLIENT);
        double averageProjectRateAsFreelancer = 0.0;
        if (numberOfRatedProjectsAsFreelancer > 0) {
            averageProjectRateAsFreelancer = (1.0*rateSum) / (5*numberOfRatedProjectsAsFreelancer);
        }

        if (projectBidId != null) {
            model.addAttribute("projectId", projectBid.getProject().getId());
        }

        model.addAttribute("referer", referer);
        model.addAttribute("projectType", Project.Type.PROJECT);
        model.addAttribute("freelancer", userRepository.getOne(freelancerUserId));
        model.addAttribute("numberOfCompletedProjects",
                projectRepository.count(ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.COMPLETED, null, null)));
        model.addAttribute("averageProjectRate", (int) (averageProjectRateAsFreelancer + 0.5));
        model.addAttribute("numberOfRatedProjects", numberOfRatedProjectsAsFreelancer);

        return "client/directMarket/startWithSelectedSpecialist";
    }
}
