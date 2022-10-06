package com.freelancerk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.PickMeUp;
import com.freelancerk.domain.PickMeUpComment;
import com.freelancerk.domain.repository.PickMeUpCommentRepository;
import com.freelancerk.domain.repository.PickMeUpRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.io.ResponseCode;
import com.freelancerk.service.MessageService;

@RestController
public class PickMeUpCommentController extends RootController {

    private MessageService messageService;
    private UserRepository userRepository;
    private PickMeUpRepository pickMeUpRepository;
    private PickMeUpCommentRepository pickMeUpCommentRepository;

    @Autowired
    public PickMeUpCommentController(MessageService messageService, UserRepository userRepository,
                                     PickMeUpRepository pickMeUpRepository, PickMeUpCommentRepository pickMeUpCommentRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.pickMeUpRepository = pickMeUpRepository;
        this.pickMeUpCommentRepository = pickMeUpCommentRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/pick-me-ups/{id}/comments")
    public CommonResponse insertPickMeUpComments(@PathVariable("id") Long id, @RequestParam("content") String contents) {
        PickMeUpComment pickMeUpComment = new PickMeUpComment();
        pickMeUpComment.setPickMeUp(pickMeUpRepository.getOne(id));
        pickMeUpComment.setContent(contents);
        pickMeUpComment.setUser(userRepository.getOne(getSessionUserId()));
        pickMeUpComment.setUserRole(getUserRole(pickMeUpComment.getPickMeUp(), getSessionUserId()));
        pickMeUpCommentRepository.save(pickMeUpComment);

        messageService.sendMessage(pickMeUpComment.getPickMeUp().getUser(), AligoKakaoMessageTemplate.Code.TA_3200, null);

        return CommonResponse.ok();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/pick-me-ups/comments/{id}")
    public CommonResponse deletePickMeUpComment(@PathVariable("id") Long pickMeUpCommentId) {
        PickMeUpComment comment = pickMeUpCommentRepository.getOne(pickMeUpCommentId);
        if (!comment.getUser().getId().equals(getSessionUserId())) {
            return new CommonResponse.Builder<String>().message("권한이 없습니다.").responseCode(ResponseCode.FAIL).build();
        }
        pickMeUpCommentRepository.deleteById(pickMeUpCommentId);
        return CommonResponse.ok();
    }

    private PickMeUpComment.UserRole getUserRole(PickMeUp pickMeUp, long userId) {
        if (userId == pickMeUp.getUser().getId()) {
            return PickMeUpComment.UserRole.ONESELF;
        }

        return isLoggedIsAsClient()?PickMeUpComment.UserRole.CLIENT:PickMeUpComment.UserRole.FREELANCER;
    }
}
