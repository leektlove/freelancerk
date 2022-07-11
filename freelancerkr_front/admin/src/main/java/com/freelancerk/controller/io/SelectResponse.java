package com.freelancerk.controller.io;

import lombok.Data;

import java.util.List;

@Data
public class SelectResponse {
    private List<SelectResponseItem> results;
}
