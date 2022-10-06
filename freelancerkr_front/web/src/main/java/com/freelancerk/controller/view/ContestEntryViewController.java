package com.freelancerk.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.freelancerk.controller.RootController;
import com.freelancerk.domain.ContestEntryFile;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.repository.ContestEntryFileRepository;
import com.freelancerk.domain.repository.ProjectBidRepository;

@Controller
public class ContestEntryViewController extends RootController {

    private ProjectBidRepository projectBidRepository;
    private ContestEntryFileRepository contestEntryFileRepository;

    @Autowired
    public ContestEntryViewController(ProjectBidRepository projectBidRepository, ContestEntryFileRepository contestEntryFileRepository) {
        this.projectBidRepository = projectBidRepository;
        this.contestEntryFileRepository = contestEntryFileRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/view/contest-entries/{contestEntryId}/details/ajax")
    public String getContestEntriesForAjax(@PathVariable("contestEntryId") long contestEntryId,
                                           Model model) {

        ProjectBid item = null;
        List<ContestEntryFile> contestEntryFileList = contestEntryFileRepository.findByContestEntryIdOrderByRepresentativeDesc(contestEntryId);
        for (ContestEntryFile file: contestEntryFileList) {
            item = file.getContestEntry();
        }
        model.addAttribute("item", item);
        model.addAttribute("items", contestEntryFileList);

        return "contestEntry/contest-entry-for-ajax";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/view/contest-entries/{contestEntryId}/details")
    public String getContestEntries(@PathVariable("contestEntryId") long contestEntryId,
                                           Model model) {

        ProjectBid item = null;
        List<ContestEntryFile> contestEntryFileList = contestEntryFileRepository.findByContestEntryIdOrderByRepresentativeDesc(contestEntryId);
        for (ContestEntryFile file: contestEntryFileList) {
            item = file.getContestEntry();
        }
        model.addAttribute("item", item);
        model.addAttribute("items", contestEntryFileList);

        return "contestEntry/contest-entry";
    }

}
