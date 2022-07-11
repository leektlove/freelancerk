package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Data
@Entity
public class UserCertification {

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Expose
    private LocalDate acqDate;
    @Expose
    private String certificationName;
    @Expose
    private String authOrganization;

    @Expose(serialize = false)
    @JsonIgnore
    @ManyToOne
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserCertification that = (UserCertification) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(acqDate, that.acqDate) &&
                Objects.equals(certificationName, that.certificationName) &&
                Objects.equals(authOrganization, that.authOrganization);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, acqDate, certificationName, authOrganization);
    }

    @Override
    public String toString() {
        return "UserCertification{" +
                "id=" + id +
                ", acqDate=" + acqDate +
                ", certificationName='" + certificationName + '\'' +
                ", authOrganization='" + authOrganization + '\'' +
                '}';
    }
}
