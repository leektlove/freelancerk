package com.freelancerk.service;

import com.freelancerk.domain.Category;
import com.freelancerk.domain.ContestSector;

import java.util.List;
import java.util.Set;

public interface ContestSectorService {

    Set<Category> convertContestSectorToUserCategory(ContestSector contestSector);
}
