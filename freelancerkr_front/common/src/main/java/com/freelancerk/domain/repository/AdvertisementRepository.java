package com.freelancerk.domain.repository;

import com.freelancerk.domain.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    @Modifying
    @Query("UPDATE Advertisement a SET hits = hits + 1 WHERE a.id = :id")
    void increaseHit(@Param("id") long id);

    @Modifying
    @Query("UPDATE Advertisement a SET clicks = clicks + 1 WHERE a.id = :id")
    void increaseClick(@Param("id") long id);

    List<Advertisement> findByStartAtLessThanEqualAndEndAtGreaterThan(LocalDate localDate, LocalDate localDate2);
}
