package com.freelancerk.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancerk.TimeUtil;
import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.*;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.io.ResponseCode;
import com.freelancerk.service.FreelancerPayService;
import com.freelancerk.service.PickMeUpService;
import com.freelancerk.service.PurchaseService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class PickMeUpController extends RootController {

    private UserRepository userRepository;
    private PickMeUpService pickMeUpService;
    private PurchaseService purchaseService;
    private ProjectRepository projectRepository;
    private PickMeUpRepository pickMeUpRepository;
    private FreelancerPayService freelancerPayService;
    private PickMeUpLikeRepository pickMeUpLikeRepository;
    private PickMeUpTicketRepository pickMeUpTicketRepository;
    private ContestEntryFileRepository contestEntryFileRepository;
    private PickMeUpTicketLogRepository pickMeUpTicketLogRepository;
    private ContactAvailableDayTimeRepository contactAvailableDayTimeRepository;

    @Autowired
    public PickMeUpController(UserRepository userRepository, PickMeUpService pickMeUpService, PickMeUpRepository pickMeUpRepository,
                              FreelancerPayService freelancerPayService, ProjectRepository projectRepository,
                              PurchaseService purchaseService,
                              PickMeUpLikeRepository pickMeUpLikeRepository,
                              PickMeUpTicketRepository pickMeUpTicketRepository,
                              ContestEntryFileRepository contestEntryFileRepository,
                              PickMeUpTicketLogRepository pickMeUpTicketLogRepository,
                              ContactAvailableDayTimeRepository contactAvailableDayTimeRepository) {
        this.userRepository = userRepository;
        this.pickMeUpService = pickMeUpService;
        this.purchaseService = purchaseService;
        this.projectRepository = projectRepository;
        this.pickMeUpRepository = pickMeUpRepository;
        this.freelancerPayService = freelancerPayService;
        this.pickMeUpLikeRepository = pickMeUpLikeRepository;
        this.pickMeUpTicketRepository = pickMeUpTicketRepository;
        this.contestEntryFileRepository = contestEntryFileRepository;
        this.pickMeUpTicketLogRepository = pickMeUpTicketLogRepository;
        this.contactAvailableDayTimeRepository = contactAvailableDayTimeRepository;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/pick-me-ups/temp")
    public RedirectView create(@ModelAttribute PickMeUp pickMeUp,
                         @RequestParam(value = "mainImageUrl", required = false) String mainImageUrl,
                         @RequestParam(value = "subImageUrl[]", required = false) String[] subImageUrl,
                         @RequestParam(value = "workPlace", required = false) Project.WorkPlace workPlace,
                         @RequestParam(value = "workPlaceAddress1", required = false) String workPlaceAddress1,
                         @RequestParam(value = "workPlaceAddress2", required = false) String workPlaceAddress2,
                         @RequestParam(value = "projectId", required = false) Long projectId,
                         @RequestParam(value = "payTypeNeedAgreement", required = false) Boolean payTypeAgreement,
                         @RequestParam(value = "payType", required = false) PickMeUp.PayType payType,
                         @RequestParam(value = "minimumPay", required = false) Integer minimumPay, HttpServletRequest request) {

        if (payTypeAgreement != null && payTypeAgreement) {
            payType = PickMeUp.PayType.AGREEMENT;
            minimumPay = 0;
        }
        pickMeUp.setTemp(true);
        pickMeUp = pickMeUpService.create(pickMeUp, mainImageUrl, mainImageUrl, subImageUrl, subImageUrl, projectId, payType, minimumPay,
                workPlace, workPlaceAddress1, workPlaceAddress2);

        request.getSession().setAttribute("pickMeUpId", pickMeUp.getId());

        return new RedirectView("/freelancer/pickMeUp/createOption");
    }

    @Transactional
    @PostMapping("/pick-me-ups/options")
    public RedirectView options(@ModelAttribute PickMeUp pickMeUp,
                                @RequestParam(value = "selectProductList", required = false) String jsonSelectProductList,
                                @RequestParam(value = "contactAvailableDayTime", required = false) String contactAvailableDayTimeJson) throws IOException {
        pickMeUp = pickMeUpRepository.getOne(pickMeUp.getId());

        if (StringUtils.isNotEmpty(contactAvailableDayTimeJson)) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<String>> parsedMap = mapper.readValue(contactAvailableDayTimeJson, new TypeReference<Map<String, List<String>>>() {});
            contactAvailableDayTimeRepository.deleteByPickMeUpId(pickMeUp.getId());
            ContactAvailableDayTime contactAvailableDayTime = new ContactAvailableDayTime();
            contactAvailableDayTime.setPickMeUp(pickMeUp);
            contactAvailableDayTime.setDayTimes(parsedMap);
            contactAvailableDayTimeRepository.save(contactAvailableDayTime);
        }

        List<FreelancerPayProduct> payProductList = new Gson().fromJson(jsonSelectProductList, new TypeToken<ArrayList<FreelancerPayProduct>>(){}.getType());
        if (payProductList == null || payProductList.isEmpty()) {
            pickMeUp.setInvalid(false);
            pickMeUp.setTemp(false);
            pickMeUpRepository.save(pickMeUp);
            return new RedirectView("/freelancer/pickMeUp/list");
        }

        int totalAmount = 0;
        for (FreelancerPayProduct freelancerPayProduct: payProductList) {
            totalAmount += freelancerPayProduct.getAmount() == null?0:freelancerPayProduct.getAmount();
        }

        if (totalAmount == 0) {
            return new RedirectView("/freelancer/pickMeUp/list");
        }

        freelancerPayService.insertOptionTemp(pickMeUp, payProductList);


        return new RedirectView("/freelancer/pickMeUp/paymentConfirm");
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/pick-me-ups/{id}/modifications")
    public RedirectView modify(@ModelAttribute PickMeUp pickMeUp,
                               @RequestParam(value = "mainImageUrl", required = false) String mainImageUrl,
                               @RequestParam(value = "croppedMainImageUrl", required = false) String croppedMainImageUrl,
                               @RequestParam(value = "subImageUrl[]", required = false) String[] subImagesUrls,
                               @RequestParam(value = "workPlace", required = false) Project.WorkPlace workPlace,
                               @RequestParam(value = "workPlaceAddress1", required = false) String workPlaceAddress1,
                               @RequestParam(value = "workPlaceAddress2", required = false) String workPlaceAddress2,
                               @RequestParam(value = "payTypeNeedAgreement", required = false) Boolean payTypeAgreement,
                               @RequestParam(value = "payType", required = false) PickMeUp.PayType payType,
                               @RequestParam(value = "minimumPay", required = false) Integer minimumPay,
                               @RequestParam(value = "contactAvailableDayTime", required = false) String contactAvailableDayTimeJson,
                               HttpServletRequest request) throws IOException {
        if (payTypeAgreement != null && payTypeAgreement) {
            payType = PickMeUp.PayType.AGREEMENT;
            minimumPay = 0;
        }
        pickMeUpService.update(pickMeUp, mainImageUrl, croppedMainImageUrl, subImagesUrls, subImagesUrls, payType, minimumPay,
                workPlace, workPlaceAddress1, workPlaceAddress2);

        if (StringUtils.isNotEmpty(contactAvailableDayTimeJson)) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<String>> parsedMap = mapper.readValue(contactAvailableDayTimeJson, new TypeReference<Map<String, List<String>>>() {});
            contactAvailableDayTimeRepository.deleteByPickMeUpId(pickMeUp.getId());
            ContactAvailableDayTime contactAvailableDayTime = new ContactAvailableDayTime();
            contactAvailableDayTime.setPickMeUp(pickMeUp);
            contactAvailableDayTime.setDayTimes(parsedMap);
            contactAvailableDayTimeRepository.save(contactAvailableDayTime);
        }

        return new RedirectView("/freelancer/pickMeUp/list");
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/pick-me-ups/tickets")
    public RedirectView createPickMeUpTickets(
            @ModelAttribute Purchase purchase) {
        if (purchase.getTotalAmountOfMoney() > 0) {
            boolean extend = purchase.isExtend();
            long pickMeUpId = purchase.getSelectedPickMeUpId();
            purchase = purchaseService.confirmPurchasePickMeUp(getSessionUserId(), purchase);
            freelancerPayService.purchasePickMeUp(getSessionUserId(), pickMeUpId, purchase.getUsedPoints(), purchase.getUser(), purchase);

            String redirectUrl = "/freelancer/pickMeUp/enrollmentDone";
            if (extend) {
                redirectUrl += "?mode=EXTEND";
            }
            return new RedirectView(redirectUrl);
        } else {
            long pickMeUpId = purchase.getSelectedPickMeUpId();
            purchase = purchaseService.confirmPurchasePickMeUp(getSessionUserId(), purchase);
            freelancerPayService.purchasePickMeUp(getSessionUserId(), pickMeUpId, purchase.getUsedPoints(), userRepository.getOne(getSessionUserId()), purchase);
            return new RedirectView("/freelancer/pickMeUp/list");
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/pick-me-ups/{id}")
    public void deletePickMeUp(@PathVariable("id") Long pickMeUpId, HttpServletResponse response) throws IOException {
        if (!getSessionUserId().equals(pickMeUpRepository.getOne(pickMeUpId).getUser().getId())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        PickMeUp pickMeUp = pickMeUpRepository.getOne(pickMeUpId);
        pickMeUp.setInvalid(true);
        pickMeUpRepository.save(pickMeUp);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.DELETE, value = "/pick-me-ups/{id}/purchase-records")
    public CommonResponse<Void> invalidatePickMeUpTickets(@PathVariable("id") Long pickMeUpId) {
        PickMeUp pickMeUp = pickMeUpRepository.getOne(pickMeUpId);
        pickMeUp.setPurchaseRecordDeleted(true);
        pickMeUpRepository.save(pickMeUp);

        List<PickMeUpTicket> tickets = pickMeUpTicketRepository.findByPickMeUpId(pickMeUpId);
        for (PickMeUpTicket ticket: tickets) {
            ticket.setInvalid(true);
            pickMeUpTicketRepository.save(ticket);
        }

        List<PickMeUpTicketLog> logs = pickMeUpTicketLogRepository.findByPickMeUpIdAndInvalidFalse(pickMeUpId);
        for (PickMeUpTicketLog log: logs) {
            log.setInvalid(true);
            pickMeUpTicketLogRepository.save(log);
        }

        return CommonResponse.ok();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/pick-me-ups/{id}/likes")
    public CommonResponse<Void> likePickMeUp(@PathVariable("id") Long pickMeUpId) {
        if (pickMeUpLikeRepository.countByPickMeUpIdAndUserId(pickMeUpId, getSessionUserId()) > 0) {
            return new CommonResponse.Builder<Void>().responseCode(ResponseCode.FAIL).message("이미 좋아요 한 픽미업 입니다.").build();
        }

        User user = userRepository.getOne(getSessionUserId());

        PickMeUp pickMeUp = pickMeUpRepository.getOne(pickMeUpId);
        PickMeUpLike like = new PickMeUpLike();
        like.setPickMeUp(pickMeUp);
        like.setUser(user);
        pickMeUpLikeRepository.save(like);

        return CommonResponse.ok();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/pick-me-ups/{id}/likes")
    public CommonResponse<Void> unlikePickMeUp(@PathVariable("id") Long pickMeUpId) {
        PickMeUpLike like = pickMeUpLikeRepository.findByPickMeUpIdAndUserId(pickMeUpId, getSessionUserId());
        pickMeUpLikeRepository.delete(like);

        return CommonResponse.ok();
    }

    @PostMapping("/pick-me-ups/from-projects/{projectId}")
    public CommonResponse createFromProject(@PathVariable long projectId) {
        Project project = projectRepository.getOne(projectId);
        PickMeUp pickMeUp = new PickMeUp();
        pickMeUp.setContentType(PickMeUp.ContentType.IMAGE);
        pickMeUp.setTitle(project.getTitle());
        pickMeUp.setPayType(PickMeUp.PayType.AGREEMENT);
        pickMeUp.setWorkPlace(Project.WorkPlace.NO_IDEA);
        pickMeUp.setProject(project);

        User user = userRepository.getOne(getSessionUserId());
        if (!user.getSectorsByParent().isEmpty()) {
            Category category = user.getSectorsByParent().keySet().iterator().next();
            pickMeUp.setCategory1st(category.getParentCategory());
            pickMeUp.setCategory2nd(category);
        }

        if (project.getStartAt() != null) {
            pickMeUp.setWorkStartAt(TimeUtil.convertLocalDateToStr(project.getStartAt().toLocalDate()));
        }
        if (project.getPostingEndAt() != null) {
            pickMeUp.setWorkEndAt(TimeUtil.convertLocalDateToStr(project.getPostingEndAt().toLocalDate()));
        }

        if (Project.Type.PROJECT.equals(project.getProjectType())) {
            pickMeUp.setMainImageUrl("https://freelancerk.s3.amazonaws.com/certified_portfolio.jpg");
            pickMeUp.setCroppedMainImageUrl("https://freelancerk.s3.amazonaws.com/certified_portfolio.jpg");
        } else if (Project.Type.CONTEST.equals(project.getProjectType())) {
            ContestEntryFile contestEntryFile = contestEntryFileRepository.findByUserIdAndContestEntryProjectIdAndRepresentativeTrue(
                    getSessionUserId(), projectId);
            if (ContestEntryFile.Type.IMAGE.equals(contestEntryFile.getFileType())) {
                pickMeUp.setMainImageUrl(contestEntryFile.getFileUrl());
                pickMeUp.setCroppedMainImageUrl(contestEntryFile.getCroppedFileUrl());
            } else {
                pickMeUp.setMainImageUrl("https://freelancerk.s3.amazonaws.com/certified_portfolio.jpg");
                pickMeUp.setCroppedMainImageUrl("https://freelancerk.s3.amazonaws.com/certified_portfolio.jpg");
            }
        }

        pickMeUp.setUser(user);

        pickMeUpRepository.save(pickMeUp);

        return CommonResponse.ok();
    }
}
