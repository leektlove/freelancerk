package com.freelancerk.controller.io;

import lombok.Data;

@Data
public class JqueryUploaderResponseItem {
    private String name;
    private int size;
    private String url;
    private String thumbnailUrl;
    private String videoUrl;
    private String deleteUrl;
    private String deleteType;
}
