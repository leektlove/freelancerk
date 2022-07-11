package com.freelancerk.controller;

import com.freelancerk.domain.ProjectContractFile;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.ProjectContractFileRepository;
import com.freelancerk.domain.repository.ProjectRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonResponse;
import com.freelancerk.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProjectContractController extends RootController {

    private UserRepository userRepository;
    private StorageService storageService;
    private ProjectRepository projectRepository;
    private ProjectContractFileRepository projectContractFileRepository;

    @Autowired
    public ProjectContractController(UserRepository userRepository, StorageService storageService,
                                     ProjectRepository projectRepository, ProjectContractFileRepository projectContractFileRepository) {
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.projectRepository = projectRepository;
        this.projectContractFileRepository = projectContractFileRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/projects/{id}/contract-files")
    public CommonResponse<ProjectContractFile> saveProjectContractFiles(@PathVariable("id") Long projectId,
                                                                        @RequestParam("file") MultipartFile multipartFile) {
        String fileUrl = storageService.storeFile(multipartFile);
        ProjectContractFile file = new ProjectContractFile();
        file.setUserRole(isLoggedIsAsClient()?User.Role.ROLE_CLIENT:User.Role.ROLE_FREELANCER);
        file.setFileUrl(fileUrl);
        file.setFileName(multipartFile.getOriginalFilename());
        file.setProject(projectRepository.getOne(projectId));
        file.setUser(userRepository.getOne(getSessionUserId()));
        file = projectContractFileRepository.save(file);

        return new CommonResponse.Builder<ProjectContractFile>().data(file).build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/project-contract-files/{id}")
    public CommonResponse deleteFile(@PathVariable("id") long id) {

        ProjectContractFile file = projectContractFileRepository.getOne(id);
        file.setInvalid(true);
        projectContractFileRepository.save(file);

        return CommonResponse.ok();
    }
}
