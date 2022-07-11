package com.freelancerk.domain.repository;

import com.freelancerk.domain.DailyAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DailyAccessLogRepository extends JpaRepository<DailyAccessLog, Long>, JpaSpecificationExecutor<DailyAccessLog> {
    List<DailyAccessLog> findByDateAndUserId(LocalDate now, Long userId);

    @Query(nativeQuery = true, value = "select count(*) from daily_access_log av where access_time >= :start and access_time < :end and date >= :date ")
    long countByAccessTimeStrBetweenAndDate(@Param("start") String start, @Param("end") String end,
                                                              @Param("date") LocalDate date);

    long countByUserId(Long id);

    int countByDate(LocalDate date);

    DailyAccessLog findTop1ByUserIdOrderByIdDesc(Long userId);
}
