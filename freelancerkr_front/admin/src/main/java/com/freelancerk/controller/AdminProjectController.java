package com.freelancerk.controller;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.KeywordOrSectorAlarmService;
import com.freelancerk.service.ProjectService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "프로젝트", description = "리스트/등록 등")
@RestController
public class AdminProjectController {

    private KeywordOrSectorAlarmService keywordOrSectorAlarmService;
    private ProjectRepository projectRepository;
    private ProjectService projectService;

    @Autowired
    public AdminProjectController(KeywordOrSectorAlarmService keywordOrSectorAlarmService,
                                  ProjectRepository projectRepository, ProjectService projectService) {
        this.keywordOrSectorAlarmService = keywordOrSectorAlarmService;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
    }

//    @ApiOperation("프로젝트 조회")
//    @RequestMapping(method = RequestMethod.GET, value = "/projects")
//    public CommonListResponse<List<Project>> getProjects(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
//                                                         @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
//        Page<Project> page = projectRepository.findAll(PageRequest.of(pageNumber, pageSize));
//        return new CommonListResponse.Builder<List<Project>>()
//                .totalCount(page.getTotalElements())
//                .totalPages(page.getTotalPages())
//                .currentPage(pageNumber)
//                .data(page.getContent()).build();
//    }

//    @ApiOperation("프로젝트 등록")
//    @RequestMapping(method = RequestMethod.POST, value = "/projects")
//    public CommonResponse insertProjects(
//            @RequestParam("userId") Long userId,
//            @RequestParam("name") String name, @RequestParam("description") String description,
//            @RequestParam("keyword[]") String[] keywords, @RequestParam("expectedPeriod") Project.ExpectedPeriod expectedPeriod,
//            @RequestParam(value = "projectDescriptionFile", required = false) MultipartFile projectDescriptionFile,
//            @RequestParam("budget") Project.Budget budget, @RequestParam("payCriteria") Project.PayCriteria payCriteria,
//            @RequestParam("workPlace") Project.WorkPlace workPlace, @RequestParam("status") Project.Status status,
//            @RequestParam("useEscrow") boolean useEscrow, @RequestParam("exposePeriod") String exposePeriod,
//            @RequestParam(value = "projectFile[]", required = false) MultipartFile[] projectFiles,
//            @RequestParam(value = "projectOption[]", required = false) Long[] optionIds,
//            @RequestParam(value = "projectOptionCount[]", required = false) Integer[] optionCount,
//            @RequestParam(value = "pointUsage", required = false) Integer pointUsage) {
//        projectService.create(userId, name, description, keywords, expectedPeriod, projectDescriptionFile, budget, payCriteria,
//                workPlace, status, useEscrow, exposePeriod, optionIds, optionCount, pointUsage, projectFiles);
//
//        return CommonResponse.ok();
//    }

    @PostMapping("/projects/{id}/keyword-mail")
    public CommonResponse sendKeywordMail(@PathVariable long id) {
        Project project = projectRepository.getOne(id);
        keywordOrSectorAlarmService.sendMail(project);

        return CommonResponse.ok();
    }
}
