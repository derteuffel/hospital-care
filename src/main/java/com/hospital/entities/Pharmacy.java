package com.hospital.entities;


import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Data
@Entity
public class Pharmacy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;



    @OneToMany(mappedBy = "pharmacy")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Facture> factures;

    @OneToMany(mappedBy = "pharmacy")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Others>others;

    @OneToOne
    private Hospital hospital;

    @OneToMany(mappedBy = "pharmacy")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Medicament>medicaments;

    public Pharmacy() {
    }

    public Pharmacy(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Facture> getFactures() {
        return factures;
    }

    public void setFactures(Collection<Facture> factures) {
        this.factures = factures;
    }

    public Collection<Others> getOthers() {
        return others;
    }

    public void setOthers(Collection<Others> others) {
        this.others = others;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public Collection<Medicament> getMedicaments() {
        return medicaments;
    }

    public void setMedicaments(Collection<Medicament> medicaments) {
        this.medicaments = medicaments;
    }
}
