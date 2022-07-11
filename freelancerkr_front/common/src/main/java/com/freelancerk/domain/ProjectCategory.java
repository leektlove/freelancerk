package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.gson.JsonObject;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class ProjectCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonBackReference
    @ManyToOne
    private Project project;
    @ManyToOne
    private Category category;

    public String getKeywordJsonString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", category.getParentCategory().getId());
        jsonObject.addProperty("keyword", category.getName());
        return jsonObject.toString();
    }
}
