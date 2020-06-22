package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "rdv")
@Data
public class Rdv implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @NotNull
    private String motif;

    private String doctor;
    private String patient;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "rdvs_comptes",
            joinColumns = @JoinColumn(
                    name = "compte_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "rdv_id", referencedColumnName = "id"))
    private List<Compte> comptes;

    private Boolean status;


    public Rdv() {
    }

    public Rdv(Date date, @NotNull String motif, String doctor, String patient, Boolean status) {
        this.date = date;
        this.motif = motif;
        this.doctor = doctor;
        this.patient = patient;
        this.status = status;
    }

    public Date getDate() {
        return date;
    }


    public Long getId() {
        return id;
    }

    public String getMotif() {
        return motif;
    }



    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }



    public List<Compte> getComptes() {
        return comptes;
    }

    public void setComptes(List<Compte> comptes) {
        this.comptes = comptes;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
