package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class ContestSectorType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @JsonBackReference
    @ManyToOne
    private ContestSectorMetaType contestSectorMetaType;
    private int minAmountOfPrice;
}
