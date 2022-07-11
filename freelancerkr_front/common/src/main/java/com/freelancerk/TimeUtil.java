package com.freelancerk;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeUtil {

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.KOREA);
    static final DateTimeFormatter formatterWhTime = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss").withLocale(Locale.KOREA);

    public static LocalDate convertStrToLocalDate(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) return null;
        return LocalDate.parse(dateStr, formatter);
    }

    public static LocalDateTime convertStrToLocalDateTime(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) return null;
        return Timestamp.valueOf(LocalDate.parse(dateStr, formatter).atStartOfDay()).toLocalDateTime();
    }

    public static LocalDateTime convertStrWithTimeToLocalDateTime(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) return null;
        return Timestamp.valueOf(LocalDateTime.parse(dateStr, formatterWhTime)).toLocalDateTime();
    }

    public static LocalDateTime convertLocalDateToLocalDateTime(LocalDate localDate) {
        if (localDate == null) return null;
        return Timestamp.valueOf(localDate.atStartOfDay()).toLocalDateTime();
    }

    public static String convertLocalDateTimeToStr(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return DateTimeFormatter.ofPattern("yyyy년 MM월 dd일").withLocale(Locale.KOREA).format(localDateTime);
    }

    public static String convertLocalDateTimeToStrWithTime(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        Integer year = localDateTime.getYear();
        Integer month = localDateTime.getMonthValue();
        Integer day = localDateTime.getDayOfMonth();
        return String.format("%s-%02d-%02d", year, month, day);
    }

    public static String convertLocalDateToStrWithTime(LocalDate localDate) {
        if (localDate == null) return null;
        Integer year = localDate.getYear();
        Integer month = localDate.getMonthValue();
        Integer day = localDate.getDayOfMonth();
        return String.format("%s-%02d-%02dT00:00:00", year, month, day);
    }

    public static String convertLocalDateToStr(LocalDate localDate) {
        if (localDate == null) return null;
        Integer year = localDate.getYear();
        Integer month = localDate.getMonthValue();
        Integer day = localDate.getDayOfMonth();
        return String.format("%s-%02d-%02d", year, month, day);
    }
}
