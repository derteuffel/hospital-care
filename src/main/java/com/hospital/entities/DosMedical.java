package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;


@Entity
public class DosMedical implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Size(min = 1, max = 1)
    private String rhesus;


    private double weight;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    private String hereditaryDiseases;

    private Integer height;


    private String description;

    private String code;

    private String sex;

    private String bloodType;

    private String name;

    @OneToOne
    private Compte compte;

    @JsonIgnoreProperties("dosMedical")
    @OneToMany(mappedBy = "dosMedical")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Consultation>consultations;

    @OneToMany(mappedBy = "dosMedical")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnoreProperties("dosMedical")
    private Collection<Rdv> rdvs;





    public DosMedical() {

    }

    public DosMedical(String rhesus, double weight, Integer height, Date birthDate, String hereditaryDiseases,
                      String description, String code, String bloodType, String name, String sex) {
        this.rhesus = rhesus;
        this.weight = weight;
        this.height = height;
        this.birthDate = birthDate;
        this.hereditaryDiseases = hereditaryDiseases;
        this.description = description;
        this.code = code;
        this.bloodType = bloodType;
        this.name = name;
        this.sex = sex;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
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


    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Collection<Rdv> getRdvs() {
        return rdvs;
    }

    public void setRdvs(Collection<Rdv> rdvs) {
        this.rdvs = rdvs;
    }
}
