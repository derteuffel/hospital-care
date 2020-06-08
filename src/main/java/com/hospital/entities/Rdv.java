package com.hospital.entities;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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
}
