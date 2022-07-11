package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
public class ContestSectorMetaType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean duplicateSelectable;
    private String description;
    private String shortDescription;
    private String imageUrl;
    private int seq;

    @JsonManagedReference
    @OrderBy(" id asc ")
    @OneToMany(mappedBy = "contestSectorMetaType")
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<ContestSectorType> contestSectorTypeSet;

    @Override
    public String toString() {
        return "ContestSectorMetaType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", duplicateSelectable=" + duplicateSelectable +
                ", description='" + description + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ContestSectorMetaType that = (ContestSectorMetaType) o;
        return duplicateSelectable == that.duplicateSelectable &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(shortDescription, that.shortDescription);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, name, duplicateSelectable, description, shortDescription);
    }
}
