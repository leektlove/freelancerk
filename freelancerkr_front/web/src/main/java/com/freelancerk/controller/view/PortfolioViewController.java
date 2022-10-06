package com.freelancerk.controller.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.ContactAvailableDayTime;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.PickMeUpTicket;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.ContactAvailableDayTimeRepository;
import com.freelancerk.domain.repository.PickMeUpCommentRepository;
import com.freelancerk.domain.repository.PickMeUpLikeRepository;
import com.freelancerk.domain.repository.PickMeUpRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.specification.PickMeUpTicketSpecifications;
import com.freelancerk.domain.specification.ProjectSpecifications;
import com.freelancerk.model.ContactAvailableDayTimeModel;
import com.freelancerk.service.PickMeUpService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PortfolioViewController extends RootController {

    @Value("${server.url}") String serverUrl;
    private EntityManager entityManager;
    private PickMeUpService pickMeUpService;
    private ProjectRepository projectRepository;
    private PickMeUpRepository pickMeUpRepository;
    private CategoryRepository categoryRepository;
    private PickMeUpLikeRepository pickMeUpLikeRepository;
    private PickMeUpCommentRepository pickMeUpCommentRepository;
    private ContactAvailableDayTimeRepository contactAvailableDayTimeRepository;

    @Autowired
    public PortfolioViewController(EntityManager entityManager,
                                   PickMeUpService pickMeUpService,
                                   ProjectRepository projectRepository,
                                   PickMeUpRepository pickMeUpRepository,
                                   CategoryRepository categoryRepository,
                                   PickMeUpLikeRepository pickMeUpLikeRepository,
                                   PickMeUpCommentRepository pickMeUpCommentRepository,
                                   ContactAvailableDayTimeRepository contactAvailableDayTimeRepository) {
        this.entityManager = entityManager;
        this.pickMeUpService = pickMeUpService;
        this.projectRepository = projectRepository;
        this.pickMeUpRepository = pickMeUpRepository;
        this.categoryRepository = categoryRepository;
        this.pickMeUpLikeRepository = pickMeUpLikeRepository;
        this.pickMeUpCommentRepository = pickMeUpCommentRepository;
        this.contactAvailableDayTimeRepository = contactAvailableDayTimeRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/portfolios/{portfolioId}/details")
    public String getPortfolioDetailView(@PathVariable("portfolioId") Long portfolioId,
                                         @RequestParam(value = "portfolioIds", required = false) String portfolioIds,
                                         @RequestParam(value = "category1stId", required = false) Long category1stId,
                                         @RequestParam(value = "category2ndId", required = false) Long category2ndId,
                                         @RequestParam(value = "directMarketAvailable", required = false, defaultValue = "false") boolean directMarketAvailable,
                                         @RequestParam(value = "fromProfile", required = false, defaultValue = "false") boolean fromProfile,
                                         @RequestParam(value = "keyword", required = false) String keyword,
                                         HttpServletResponse response,
                                         Model model) throws IOException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<PickMeUpTicket> root = cq.from(PickMeUpTicket.class);

        Predicate[] predicates = PickMeUpTicketSpecifications.getPredicate(cq, cb, root,
                category1stId != null ? categoryRepository.getOne(category1stId) : null,
                category2ndId != null ? categoryRepository.getOne(category2ndId) : null, keyword,
                null, directMarketAvailable);

        cq.where(predicates);

        List<Long> pickMeUpIds = new ArrayList<>();
        if (StringUtils.isNotEmpty(portfolioIds)) {
            pickMeUpIds = Stream.of(portfolioIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
        } else {
            List<Tuple> resultList = entityManager.createQuery(cq).getResultList();
            for (Tuple tuple : resultList) {
                pickMeUpIds.add((Long) tuple.get(0));
            }
        }

        Long previousPickMeUpId = getPrevious(pickMeUpIds, portfolioId);
        Long nextPickMeUpId = getNext(pickMeUpIds, portfolioId);
        model.addAttribute("previousItem", previousPickMeUpId == null? null:pickMeUpRepository.getOne(previousPickMeUpId));
        model.addAttribute("nextItem", nextPickMeUpId == null? null:pickMeUpRepository.getOne(nextPickMeUpId));

        PickMeUp pickMeUp = pickMeUpRepository.getOne(portfolioId);

        if (pickMeUp.isInvalid()) {
            response.sendRedirect(String.format("%s/view/pick-me-ups", serverUrl));
        }

        if (isLoggedIn() && !pickMeUp.getUser().getId().equals(getSessionUserId()) || !isLoggedIn()) {
            pickMeUp.setHits(pickMeUp.getHits() + 1);
            pickMeUpRepository.save(pickMeUp);
        }

        pickMeUpService.setValidTicketItem(pickMeUp);

        if (fromProfile) {
            pickMeUp.setExpose(false);
        }
//        if (pickMeUp.isDirectDealAvailable() && !isLoggedIn()) {
//            return "redirect:https://www.freelancerk.com/main?message=DIRECT_DEAL_NEED_LOGIN";
//        }

        model.addAttribute("portfolioIds", portfolioIds);
        model.addAttribute("keyword", keyword);
        model.addAttribute("directMarketAvailable", directMarketAvailable);
        model.addAttribute("category1stId", category1stId);
        model.addAttribute("category2ndId", category2ndId);

        model.addAttribute("item", pickMeUp);
        if (isLoggedIn()) {
            model.addAttribute("loginAsClient", ((User) SecurityContextHolder.getContext().getAuthentication()).isLoginAsClient());
            if (isLoggedIsAsClient()) {
                model.addAttribute("postedProjectList", projectRepository.findAll(ProjectSpecifications.filter(getSessionUserId(), null, Project.Status.POSTED, null, null)));
            }
        }
        model.addAttribute("isLoggedIn", isLoggedIn());
        if (isLoggedIn()) {
            pickMeUp.setLiked(pickMeUpLikeRepository.countByPickMeUpIdAndUserId(pickMeUp.getId(), getSessionUserId()) > 0);
        }
        model.addAttribute("comments", pickMeUpCommentRepository.findByPickMeUpIdOrderByCreatedAtDesc(portfolioId));

        ContactAvailableDayTime contactAvailableDayTime = contactAvailableDayTimeRepository.findByPickMeUpId(pickMeUp.getId());
        if (contactAvailableDayTime != null) {
            List<ContactAvailableDayTimeModel> dayTimeModels = new ArrayList<>();
            for (String key: contactAvailableDayTime.getDayTimes().keySet()) {
                if (StringUtils.isEmpty(key)) continue;
                ContactAvailableDayTimeModel dayTimeModel = new ContactAvailableDayTimeModel();
                dayTimeModel.setDays(Arrays.asList(key.split(",")));
                dayTimeModel.setTimes(contactAvailableDayTime.getDayTimes().get(key));
                if (dayTimeModel.getDays() == null || dayTimeModel.getDays().isEmpty()) continue;
                dayTimeModels.add(dayTimeModel);
            }
            model.addAttribute("dayTimes", dayTimeModels);
        }

        return "portfolios/portfolio-details";
    }

    public Long getNext(List<Long> list, Long id) {
        int idx = list.indexOf(id);
        if (idx < 0 || idx+1 == list.size()) return null;
        return list.get(idx + 1);
    }

    public Long getPrevious(List<Long> list, Long id) {
        int idx = list.indexOf(id);
        if (idx <= 0) return null;
        return list.get(idx - 1);
    }
}
