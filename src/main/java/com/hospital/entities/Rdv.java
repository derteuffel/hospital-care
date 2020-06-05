package com.hospital.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Rdv implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private Date date;
    private String motif;
    private String heure;
    private Long compteId;
    private Long personnelId;

    public Rdv() {
    }

    public Rdv(Date date, String motif, String heure, Long compteId, Long personnelId) {
        this.date = date;
        this.motif = motif;
        this.heure = heure;
        this.compteId = compteId;
        this.personnelId = personnelId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public Long getCompteId() {
        return compteId;
    }

    public void setCompteId(Long compteId) {
        this.compteId = compteId;
    }

    public Long getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(Long personnelId) {
        this.personnelId = personnelId;
    }
}
