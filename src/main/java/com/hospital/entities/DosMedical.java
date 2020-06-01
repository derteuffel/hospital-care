package com.hospital.entities;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.OnDeleteAction;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;


@Entity
public class DosMedical implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rhesus;

    private Integer weight;

    private Integer age;

    private String hereditaryDiseases;

    private String description;

    private String code;

    @OneToOne
    private Compte compte;

    @OneToMany
    private Collection<Consultation>consultations;


    @OneToMany(mappedBy = "dosMedical")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Ordonnance>ordonnances;


    public DosMedical() {
    }

    public DosMedical(String rhesus, Integer weight, Integer age, String hereditaryDiseases,
                      String description, String code, Compte compte, Collection<Consultation> consultations,
                      Collection<Ordonnance> ordonnances) {
        this.rhesus = rhesus;
        this.weight = weight;
        this.age = age;
        this.hereditaryDiseases = hereditaryDiseases;
        this.description = description;
        this.code = code;
        this.compte = compte;
        this.consultations = consultations;
        this.ordonnances = ordonnances;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRhesus() {
        return rhesus;
    }

    public void setRhesus(String rhesus) {
        this.rhesus = rhesus;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHereditaryDiseases() {
        return hereditaryDiseases;
    }

    public void setHereditaryDiseases(String hereditaryDiseases) {
        this.hereditaryDiseases = hereditaryDiseases;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    public Collection<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(Collection<Consultation> consultations) {
        this.consultations = consultations;
    }

    public Collection<Ordonnance> getOrdonnances() {
        return ordonnances;
    }

    public void setOrdonnances(Collection<Ordonnance> ordonnances) {
        this.ordonnances = ordonnances;
    }





}
