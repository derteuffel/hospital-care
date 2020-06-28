package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

@Entity
@Data
@Table(name = "armoire")
public class Armoire implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String HospitalName;
    private String date;

    private ArrayList<String> fichiers = new ArrayList<>();
    @ManyToOne
    @JsonIgnoreProperties("armoires")
    private Compte compte;

    public Armoire() {
    }

    public Armoire(String title, String hospitalName, String date, ArrayList<String> fichiers) {
        this.title = title;
        HospitalName = hospitalName;
        this.date = date;
        this.fichiers = fichiers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHospitalName() {
        return HospitalName;
    }

    public void setHospitalName(String hospitalName) {
        HospitalName = hospitalName;
    }

    public ArrayList<String> getFichiers() {
        return fichiers;
    }

    public void setFichiers(ArrayList<String> fichiers) {
        this.fichiers = fichiers;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
