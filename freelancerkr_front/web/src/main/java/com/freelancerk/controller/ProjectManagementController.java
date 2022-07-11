package com.freelancerk.controller;

import com.freelancerk.domain.Project;
import io.swagger.annotations.Api;

@Api("프로젝트 관리")
public class ProjectManagementController {

//    private ProjectRepository projectRepository;
//
//    @ApiOperation("내 프로젝트 조회")
//    @RequestMapping(method = RequestMethod.GET, value = "/user/me/projects")
//    public CommonListResponse<List<Project>> getProjects(@RequestParam("status") Project.Status status, @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") Sort.Direction sortDirection,
//                                                         @RequestParam(value = "sortProperty", required = false, defaultValue = "successfulBidPrice") String sortProperty,
//                                                         @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
//                                                         @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
//        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
//        Page<Project> projectPage = projectRepository.findByUserIdAndStatus(userId, status, PageRequest.of(pageNumber, pageSize, new Sort(sortDirection,sortProperty)));
//        return new CommonListResponse.Builder<List<Project>>()
//                .totalCount(projectPage.getTotalElements())
//                .currentPage(pageNumber)
//                .totalPages(projectPage.getTotalPages())
//                .data(projectPage.getContent())
//                .build();
//    }
//
//    @ApiOperation("내 컨테스트 조회")
//    @RequestMapping(method = RequestMethod.GET, value = "/contests")
//    public CommonListResponse<List<Project>> getProjects(@RequestParam("status") Contest.Status status, @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") Sort.Direction sortDirection,
//                                                         @RequestParam(value = "sortProperty", required = false, defaultValue = "successfulBidPrice") String sortProperty,
//                                                         @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
//                                                         @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
//        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
//        return new CommonListResponse();
//    }
}
