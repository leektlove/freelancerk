package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonObject;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer seq;
    private String imageAtRegisterUrl;
    private String imageAtMenuUrl;
    private String imageActiveAtMenuUrl;
    private String imageDetailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tags;
    private boolean hidden;
    private String originalCode;
    private boolean validInContest;
    @ManyToOne
    private Category parentCategory;
    @ManyToOne
    private Category topCategory;

    @Transient
    private MultipartFile imageFileAtMenu;
    @Transient
    private MultipartFile imageFileActiveAtMenu;

    public String getKeywordJsonString() {
        JsonObject jsonObject = new JsonObject();
        if (parentCategory != null) {
            jsonObject.addProperty("id", getParentCategory().getId());
            jsonObject.addProperty("categoryName", getParentCategory().getName());
        }
        jsonObject.addProperty("keyword", getName());
        return jsonObject.toString();
    }

    @JsonIgnore
    @Transient
    private List<Keyword> popularKeywords;

    @JsonIgnore
    @Transient
    private List<Category> children;

    @JsonIgnore
    @Transient
    private int numberOfProjects;


}
