package com.freelancerk.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FeeCalculator {

    public static float getFeeRate(LocalDateTime createdAt) {
        if (createdAt == null) return 0.05f;
        if (createdAt.isBefore(LocalDate.of(2020, 5, 26).atStartOfDay())) {
            return 0.03f;
        }
        return 0.05f;
    }
}
