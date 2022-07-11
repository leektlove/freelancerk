package com.freelancerk.domain.repository;

import com.freelancerk.domain.ContactAvailableDayTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactAvailableDayTimeRepository extends JpaRepository<ContactAvailableDayTime, Long> {
    void deleteByPickMeUpId(Long pickMeUpId);

    ContactAvailableDayTime findByPickMeUpId(Long pickMeUpId);
}
