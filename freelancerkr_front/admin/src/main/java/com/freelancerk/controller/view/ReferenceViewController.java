package com.freelancerk.controller.view;

import com.freelancerk.domain.Apply;
import com.freelancerk.domain.Audition;
import com.freelancerk.domain.Reference;
import com.freelancerk.domain.ReferenceFile;
import com.freelancerk.domain.repository.ReferenceFileRepository;
import com.freelancerk.domain.repository.ReferenceRepository;
import com.freelancerk.service.StorageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("reference")
@Controller
public class ReferenceViewController {

    private ReferenceRepository referenceRepository;
    private StorageService storageService;
    private ReferenceFileRepository referenceFileRepository;

    @Autowired
    public ReferenceViewController(ReferenceRepository referenceRepository, StorageService storageService, ReferenceFileRepository referenceFileRepository) {
        this.referenceRepository = referenceRepository;
        this.storageService = storageService;
        this.referenceFileRepository = referenceFileRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", "/list"})
    public String getListView(Model model,
                              @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                              @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize) {

        Page<Reference> page = referenceRepository.findAll(PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));

        model.addAttribute("page", page);

        return "reference/list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/detail/{id}")
    public String getReferenceDetailView(Model model,@PathVariable("id")Long id) {

    	Reference reference = referenceRepository.findById(id).get();
    	List<ReferenceFile> fileList = referenceFileRepository.findByReferenceId(id);
    	
    	model.addAttribute("reference", reference);
    	model.addAttribute("fileList", fileList);
    	model.addAttribute("fileListSize", fileList.size());

        return "reference/detail";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/add")
    public String getAddView() {

        return "reference/add";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/modify/{id}")
    public String getModifyView(Model model,@PathVariable("id")Long id) {

    	Reference reference = referenceRepository.findById(id).get();
    	List<ReferenceFile> fileList = referenceFileRepository.findByReferenceId(id);
    	
    	model.addAttribute("reference", reference);
    	model.addAttribute("fileList", fileList);
    	model.addAttribute("fileListSize", fileList.size());
    	
        return "reference/modify";
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/add_references")
    public String insertReferences(Model model,
    										@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
								           @RequestParam("title") String title,
                                           @RequestParam("content") String content,
                                           @RequestParam("file[]") MultipartFile[] files) {
    	
    	Reference reference = new Reference();
    	
    	if(id>0) {
    		reference = referenceRepository.findById(id).get();
    	}else{
    		reference.setCreatedAt(LocalDateTime.now());
    		reference.setHits((long) 0);
    		reference.setType(Reference.Type.FREELANCER);
    	}
        reference.setTitle(title);
        reference.setContent(content);
        reference = referenceRepository.save(reference);

        for (MultipartFile file: files) {
			String fileUrl = storageService.storeFile(file);
			if (fileUrl.length() > 89) {
				ReferenceFile referenceFile = new ReferenceFile();
				referenceFile.setCreatedAt(LocalDateTime.now());
				referenceFile.setFileUrl(fileUrl);
				referenceFile.setFileName(file.getOriginalFilename());
				referenceFile.setReference(reference);
				referenceFileRepository.save(referenceFile);
			}
        }
        return "redirect:/reference/list"; 
//		return getListView(model, 0, 20);
    }
    
    
}
