package com.freelancerk.controller;

import com.freelancerk.controller.io.ContestPaymentData;
import com.freelancerk.domain.Project;
import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.ProjectBidService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContestPaymentController extends RootController {

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private ProjectBidService projectBidService;

    public ContestPaymentController(UserRepository userRepository, ProjectRepository projectRepository,
                                    ProjectBidService projectBidService) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectBidService = projectBidService;
    }

    @GetMapping("/contests/{id}/payment-data/for-freelancer")
    public CommonResponse<ContestPaymentData> get(@PathVariable long id) {
        Project project = projectRepository.getOne(id);

        ProjectBid myBid = projectBidService.getMyContestEntry(getSessionUserId(), project.getId());
        int myRank = myBid.getPickedRank();
        int myPrizeMoney = 0;
        if (myRank == 0) {
            myPrizeMoney = project.getPrizeFor1st();
        } else if (myRank == 1) {
            myPrizeMoney = project.getPrizeFor2nd();
        } else if (myRank == 2) {
            myPrizeMoney = project.getPrizeFor3rd();
        }

        ContestPaymentData data = new ContestPaymentData();
        data.setProject(project);
        data.setUser(myBid.getParticipant());
        data.setAmount(myPrizeMoney);
        data.setTaxType(myBid.getTaxType());
        if (data.getTaxType() == null) {
            data.setTaxType(myBid.getParticipant().getTaxType());
        }
        return new CommonResponse.Builder<ContestPaymentData>().data(data).build();
    }
}
