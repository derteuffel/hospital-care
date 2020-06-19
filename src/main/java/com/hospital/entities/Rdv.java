package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

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
    @NotNull
    private Long compteId;
    @NotNull
    private Long personnelId;

//    @ManyToOne
//    @JsonIgnoreProperties("rdvs")
//    private DosMedical dosMedical;
//
//    @ManyToOne
//    @JsonIgnoreProperties("rdvs")
//    private Personnel personnel;

    private Boolean status;


    public Rdv() {
    }

    public Rdv(Date date, String motif, Long compteId, Long personnelId) {
        this.date = date;
        this.motif = motif;
        this.compteId = compteId;
        this.personnelId = personnelId;
    }


    public Long getCompteId() {
        return compteId;
    }

    public Date getDate() {
        return date;
    }

    public Long getPersonnelId() {
        return personnelId;
    }

    public Long getId() {
        return id;
    }

    public String getMotif() {
        return motif;
    }

    public boolean getStatus(){
        return status;
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

    public void setCompteId(Long compteId) {
        this.compteId = compteId;
    }

    public void setPersonnelId(Long personnelId) {
        this.personnelId = personnelId;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
