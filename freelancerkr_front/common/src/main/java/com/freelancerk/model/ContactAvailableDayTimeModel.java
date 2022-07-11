package com.freelancerk.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
public class ContactAvailableDayTimeModel {
    private List<String> days;
    private List<String> times;

    public boolean isChecked(String index) {
        return days.contains(index);
    }
}
