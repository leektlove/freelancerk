package com.freelancerk.controller;

import com.freelancerk.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectHitController {

    private ProjectService projectService;

    @Autowired
    public ProjectHitController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/projects/{id}/hits")
    public void increaseProjectHits(@PathVariable("id") Long projectId) {
        projectService.increaseHits(projectId);
    }
}
