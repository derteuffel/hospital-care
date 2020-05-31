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
    private Long compteId;
    private Long personnelId;

    public Rdv() {
    }

    public Rdv(Date date, String motif, Long compteId, Long personnelId) {
        this.date = date;
        this.motif = motif;
        this.compteId = compteId;
        this.personnelId = personnelId;
    }
}
