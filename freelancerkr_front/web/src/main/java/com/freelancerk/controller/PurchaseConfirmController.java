package com.freelancerk.controller;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.Purchase;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.service.ContestApplyService;
import com.freelancerk.service.KeywordOrSectorAlarmService;
import com.freelancerk.service.MessageService;
import com.freelancerk.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
public class PurchaseConfirmController extends RootController {

    @Value("${server.url}") String serverUrl;
    private MessageService messageService;
    private PurchaseService purchaseService;
    private ProjectRepository projectRepository;
    private ContestApplyService contestApplyService;
    private KeywordOrSectorAlarmService keywordOrSectorAlarmService;

    @Autowired
    public PurchaseConfirmController(MessageService messageService,
                                     PurchaseService purchaseService, ProjectRepository projectRepository,
                                     ContestApplyService contestApplyService, KeywordOrSectorAlarmService keywordOrSectorAlarmService) {
        this.messageService = messageService;
        this.purchaseService = purchaseService;
        this.projectRepository = projectRepository;
        this.contestApplyService = contestApplyService;
        this.keywordOrSectorAlarmService = keywordOrSectorAlarmService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/confirm/purchases/{administrationId}") // todo check iamport
    public void purchaseCallback(@PathVariable("administrationId") String administrationId,
                                 @RequestParam("postingPeriodExtend") boolean postingPeriodExtend,
                                 @RequestParam("imp_uid") String impUid,
                                 @RequestParam("merchant_uid") String merchantUid,
                                 @RequestParam(value = "postingEndAt", required = false) String postingEndAt,
                                 @RequestParam("imp_success") boolean impSuccess,
                                 HttpServletResponse response) {
        log.info("<<< purchase callback. adminId: {}, img_uid: {}, merchantId: {}, imp_success:{} ", administrationId, impUid,
                merchantUid, impSuccess);
        if (impSuccess) {
            Purchase purchase = purchaseService.confirmPurchaseViaCallback(administrationId, impUid, postingPeriodExtend, postingEndAt);
            if (!purchase.getProject().isSendKeywordDuplicatedMail()) {
                keywordOrSectorAlarmService.sendMail(purchase.getProject());
            }

            messageService.sendMessage(purchase.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3174, null);

        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/confirm/purchases/{administrationId}/no-amount") // todo 임시토큰을 발급할 필요가 있음
    public void purchaseNoAmountCallback(@PathVariable("administrationId") String administrationId,
                                 HttpServletResponse response) throws IOException {
        log.info("<<< purchase no amount callback. adminId: {}", administrationId);
        Purchase purchase = purchaseService.confirmPurchaseViaCallback(administrationId, null, false, null);
        if (!purchase.getProject().isSendKeywordDuplicatedMail()) {
            keywordOrSectorAlarmService.sendMail(purchase.getProject());
        }
        messageService.sendMessage(purchase.getProject().getPostingClient(), AligoKakaoMessageTemplate.Code.TA_3174, null);
        String redirectUrl = String.format("%s/view/pick-me-ups?afterPosting=true", serverUrl);
        if (purchase.getProject().getProjectCategories() != null && purchase.getProject().getProjectCategories().size() > 0) {
            redirectUrl += "&category1stId=" + purchase.getProject().getFirstTopCategoryId();
        }
        response.sendRedirect(redirectUrl);
    }

    @Transactional
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/confirm/purchases/contest-entry")
    public RedirectView purchaseContestEntryCallback(@RequestParam("contestId") long contestId,
                                                     @RequestParam(value = "administrationId", required = false) String administrationId,
                                                     @RequestParam(value = "imp_uid", required = false) String impUid,
                                                     @RequestParam(value = "merchant_uid", required = false) String merchantUid,
                                                     @RequestParam(value = "payment", required = false) Boolean payment,
                                                     @RequestParam("mainPiecesFileUrl") String mainPiecesFileUrl,
                                                     @RequestParam("croppedMainPiecesFileUrl") String croppedMainPiecesFileUrl,
                                                     @RequestParam(value = "subPiecesFileUrl[]", required = false) String[] subPiecesFileUrls,
                                                     @RequestParam(value = "croppedSubPiecesFileUrl[]", required = false) String[] croppedSubPiecesFileUrls,
                                                     @RequestParam(value = "videoThumbnailImageUrl", required = false) String videoThumbnailImageUrl) {

        Project contest = projectRepository.getOne(contestId);
        ProjectBid projectBid = contestApplyService.apply(contest, getSessionUserId(), mainPiecesFileUrl, croppedMainPiecesFileUrl, subPiecesFileUrls, croppedSubPiecesFileUrls, videoThumbnailImageUrl);
        purchaseService.confirmPurchaseContestEntry(administrationId, impUid, merchantUid, projectBid);

        return new RedirectView("/freelancer/contestDetail/payDone");
    }
}
