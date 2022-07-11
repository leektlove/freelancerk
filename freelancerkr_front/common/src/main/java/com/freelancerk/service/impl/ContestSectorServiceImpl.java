package com.freelancerk.service.impl;

import com.freelancerk.domain.Category;
import com.freelancerk.domain.ContestSector;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.service.ContestSectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ContestSectorServiceImpl implements ContestSectorService {

    private CategoryRepository categoryRepository;

    @Autowired
    public ContestSectorServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Set<Category> convertContestSectorToUserCategory(ContestSector contestSector) {
        if (contestSector == null) return null;

        Set<Category> result = new HashSet<>();
        if ("Contents".equalsIgnoreCase(contestSector.getContestSectorType().getContestSectorMetaType().getName())) {
            result.add(categoryRepository.findByOriginalCode("60"));
            result.add(categoryRepository.findByOriginalCode("4070"));
        } else if ("Identity".equalsIgnoreCase(contestSector.getContestSectorType().getContestSectorMetaType().getName())) {
            result.add(categoryRepository.findByOriginalCode("4020"));
            result.add(categoryRepository.findByOriginalCode("40b0"));
        } else if ("Product".equalsIgnoreCase(contestSector.getContestSectorType().getContestSectorMetaType().getName())) {
            result.add(categoryRepository.findByOriginalCode("4040"));
        } else if ("Web/App".equalsIgnoreCase(contestSector.getContestSectorType().getContestSectorMetaType().getName())) {
            result.add(categoryRepository.findByOriginalCode("4010"));
        } else if ("Illustration".equalsIgnoreCase(contestSector.getContestSectorType().getContestSectorMetaType().getName())) {
            result.add(categoryRepository.findByOriginalCode("4020"));
            result.add(categoryRepository.findByOriginalCode("4080"));
        } else if ("Print".equalsIgnoreCase(contestSector.getContestSectorType().getContestSectorMetaType().getName())) {
            result.add(categoryRepository.findByOriginalCode("4030"));
            result.add(categoryRepository.findByOriginalCode("4070"));
        } else {
            result.add(categoryRepository.findByOriginalCode("40"));
        }

        return result;
    }
}
