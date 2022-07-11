package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.annotations.Expose;
import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
public class UserSkill {

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Expose
    private String skillName;
    @Expose
    private String skillLevel;

    @Expose(serialize = false)
    @JsonManagedReference
    @JsonIgnore
    @ManyToOne
    private User user;

    public int getLevelLength() {
        if ("초급".equalsIgnoreCase(skillLevel)) {
            return 30;
        } else if ("중급".equalsIgnoreCase(skillLevel)) {
            return 70;
        } else if ("고급".equalsIgnoreCase(skillLevel)) {
            return 100;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserSkill userSkill = (UserSkill) o;
        return Objects.equals(id, userSkill.id) &&
                Objects.equals(skillName, userSkill.skillName) &&
                Objects.equals(skillLevel, userSkill.skillLevel);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, skillName, skillLevel);
    }

    @Override
    public String toString() {
        return "UserSkill{" +
                "id=" + id +
                ", skillName='" + skillName + '\'' +
                ", skillLevel='" + skillLevel + '\'' +
                '}';
    }
}
