package com.freelancerk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.freelancerk.domain.ProjectComment;
import com.freelancerk.domain.repository.ProjectCommentRepository;
import com.freelancerk.io.CommonResponse;

@RestController
public class ProjectCommentController extends RootController {

    private ProjectCommentRepository projectCommentRepository;

    @Autowired
    public ProjectCommentController(ProjectCommentRepository projectCommentRepository) {
        this.projectCommentRepository = projectCommentRepository;
    }

    @DeleteMapping("/project-comments/{id}")
    public CommonResponse delete(@PathVariable long id) {
        ProjectComment projectComment = projectCommentRepository.getOne(id);
        if (!getSessionUserId().equals(projectComment.getUser().getId())) {
            return CommonResponse.fail();
        }

        projectCommentRepository.deleteById(id);

        return CommonResponse.ok();
    }
}
