package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Data
@Entity
public class UserCareer {

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Expose
    private LocalDate startDate;
    @Expose
    private LocalDate endDate;
    @Expose
    private String projectName;
    @Expose
    private String jobPosition;
    @Expose
    private String jobDescription;

    @Expose(serialize = false)
    @JsonIgnore
    @ManyToOne
    private User user;

    @Override
    public String toString() {
        return "UserCareer{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", projectName='" + projectName + '\'' +
                ", jobPosition='" + jobPosition + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserCareer that = (UserCareer) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(projectName, that.projectName) &&
                Objects.equals(jobPosition, that.jobPosition) &&
                Objects.equals(jobDescription, that.jobDescription);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, startDate, endDate, projectName, jobPosition, jobDescription);
    }
}
