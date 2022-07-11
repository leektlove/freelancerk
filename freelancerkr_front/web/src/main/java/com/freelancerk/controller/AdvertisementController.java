package com.freelancerk.controller;

import com.freelancerk.domain.AdvertisementClick;
import com.freelancerk.domain.AdvertisementHit;
import com.freelancerk.domain.repository.AdvertisementClickRepository;
import com.freelancerk.domain.repository.AdvertisementHitRepository;
import com.freelancerk.domain.repository.AdvertisementRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.io.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdvertisementController extends RootController {

    private UserRepository userRepository;
    private AdvertisementRepository advertisementRepository;
    private AdvertisementHitRepository advertisementHitRepository;
    private AdvertisementClickRepository advertisementClickRepository;

    @Autowired
    public AdvertisementController(UserRepository userRepository, AdvertisementRepository advertisementRepository,
                                   AdvertisementHitRepository advertisementHitRepository,
                                   AdvertisementClickRepository advertisementClickRepository) {
        this.userRepository = userRepository;
        this.advertisementRepository = advertisementRepository;
        this.advertisementHitRepository = advertisementHitRepository;
        this.advertisementClickRepository = advertisementClickRepository;
    }

    @Transactional
    @PostMapping("/advertisements/{id}/hits")
    public CommonResponse increaseHit(@PathVariable long id) {
        AdvertisementHit hit = new AdvertisementHit();
        hit.setAdvertisement(advertisementRepository.getOne(id));
        if (isLoggedIn()) {
            hit.setUser(userRepository.getOne(getSessionUserId()));
        }
        advertisementHitRepository.save(hit);

        advertisementRepository.increaseHit(id);

        return CommonResponse.ok();
    }

    @Transactional
    @PostMapping("/advertisements/{id}/clicks")
    public CommonResponse increaseClick(@PathVariable long id) {
        AdvertisementClick click = new AdvertisementClick();
        click.setAdvertisement(advertisementRepository.getOne(id));
        if (isLoggedIn()) {
            click.setUser(userRepository.getOne(getSessionUserId()));
        }
        advertisementClickRepository.save(click);

        advertisementRepository.increaseClick(id);

        return CommonResponse.ok();
    }
}



