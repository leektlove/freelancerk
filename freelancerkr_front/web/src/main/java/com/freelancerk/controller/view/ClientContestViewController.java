package com.freelancerk.controller.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.ContestEntryFile;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.ProjectComment;
import com.freelancerk.domain.ProjectItemTicket;
import com.freelancerk.domain.ProjectProductItemType;
import com.freelancerk.domain.repository.ProjectBidRepository;
import com.freelancerk.domain.repository.ProjectCommentRepository;
import com.freelancerk.domain.repository.ProjectItemTicketRepository;
import com.freelancerk.domain.repository.ProjectProductItemTypeRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.specification.ProjectBidSpecifications;
import com.freelancerk.service.ProjectBidService;
import com.freelancerk.service.ProjectService;

@Controller
@RequestMapping("/client/contest")
public class ClientContestViewController extends RootController {

    @Value("${server.url}") String serverUrl;
    private ProjectService projectService;
    private ProjectRepository projectRepository;
    private ProjectBidService projectBidService;
    private ProjectBidRepository projectBidRepository;
    private ProjectCommentRepository projectCommentRepository;
    private ProjectItemTicketRepository projectItemTicketRepository;
    private ProjectProductItemTypeRepository projectProductItemTypeRepository;

    @Autowired
    public ClientContestViewController(ProjectService projectService, ProjectRepository projectRepository,
                                       ProjectBidService projectBidService, ProjectBidRepository projectBidRepository,
                                       ProjectCommentRepository projectCommentRepository,
                                       ProjectItemTicketRepository projectItemTicketRepository,
                                       ProjectProductItemTypeRepository projectProductItemTypeRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.projectBidService = projectBidService;
        this.projectBidRepository = projectBidRepository;
        this.projectCommentRepository = projectCommentRepository;
        this.projectItemTicketRepository = projectItemTicketRepository;
        this.projectProductItemTypeRepository = projectProductItemTypeRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/details")
    public String getClientProjectDetailByIdView(@PathVariable("id") Long id, Model model) {
        Project project = projectRepository.getOne(id);
        List<ProjectComment> commentList = projectCommentRepository.findByProjectIdOrderByCreatedAtAsc(id);
        List<ProjectItemTicket> projectItemTickets = projectItemTicketRepository.findByProjectId(project.getId());
        List<Long> projectProductItemIds = projectItemTickets
                .stream().map(ProjectItemTicket::getProjectProductItemType).map(ProjectProductItemType::getId).collect(Collectors.toList());
        Map<Long, String> optionIdSpanMap = new HashMap<>();
        for (ProjectItemTicket ticket: projectItemTickets) {
            optionIdSpanMap.put(ticket.getProjectProductItemType().getId(), ticket.getValidationDateSpans());
        }
        project.setProjectOptionItemIds(projectProductItemIds);
        project.setProjectOptionTicketValidationSpansMap(optionIdSpanMap);

        project.setProjectBids(projectBidRepository.findByProjectIdAndInvalidFalse(project.getId()));

        if (!project.getPostingClient().getId().equals(getSessionUserId())) {
            projectService.increaseHits(id);
        }

        List<ContestEntryFile> entryFileList = projectBidService.getAllContestEntryRepresentativeFileList(project.getId());
        List<String> thumbUrl = new ArrayList<>();
        for (ContestEntryFile file : entryFileList) {
            thumbUrl.add(file.getFileUrl());
        }
        project.setEntryFileThumbnailUrl(thumbUrl);
        project.setNumberOfEntryFiles(entryFileList.size());

        model.addAttribute("contest", project);
        model.addAttribute("commentList", commentList);
        model.addAttribute("contestOptions", projectProductItemTypeRepository.findByPackFalseAndValidTrueAndProjectType(Project.Type.CONTEST));

        return "client/details/contestDetail";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/pick")
    public String getPickView(@PathVariable("id") Long contestId, Model model) {
        Project project = projectRepository.getOne(contestId);

        if (!Project.Status.POSTED.equals(project.getStatus())) {
            return String.format("redirect:%s/client/bid/processingList", serverUrl);
        }
        model.addAttribute("contest", project);
        model.addAttribute("bids",
                projectBidRepository.findAll(ProjectBidSpecifications.filterByPriority(contestId, ProjectBid.BidType.CONTEST_BID, ProjectBid.BidStatus.APPLY, null, null)));

    	return "client/project/ranking";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/entry/full")
    public String getContestEntryFullView(@RequestParam("url") String url) {


        return "";
    }
}
