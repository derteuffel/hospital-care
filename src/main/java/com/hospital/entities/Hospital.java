package com.hospital.entities;

import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;


@Entity
@Table(name = "hospital")
@Data
public class Hospital implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;



    private String city;

    private String pays;

    private String neighborhood;


    private ArrayList<String> types = new ArrayList<String>();

   @OneToMany(mappedBy = "hospital")
   @OnDelete(action= OnDeleteAction.NO_ACTION)
   @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Personnel> personnels;

    @OneToMany(mappedBy = "hospital")
    private Collection<BloodBank>bloodBanks;


    @OneToMany(mappedBy = "hospital")
    private Collection<Incubator>incubators;
    @OneToMany(mappedBy = "hospital")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Rdv> rdvs;


    public String getName() {
        return name;
    }
    public Long getId(){
        return id;
    }

    @OneToMany(mappedBy = "hospital")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Consultation>consultations;

    @OneToMany(mappedBy = "hospital")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Examen> examens;


    public void setName(String name) {
        this.name = name;
    }

    public Hospital() {
    }

    public Hospital(String name, String city, String pays, String neighborhood, ArrayList<String> types) {
        this.name = name;
        this.city = city;
        this.pays = pays;
        this.neighborhood = neighborhood;
        this.types = types;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public Collection<Personnel> getPersonnels() {
        return personnels;
    }

    public void setPersonnels(Collection<Personnel> personnels) {
        this.personnels = personnels;
    }

    public Collection<BloodBank> getBloodBanks() {
        return bloodBanks;
    }

    public void setBloodBanks(Collection<BloodBank> bloodBanks) {
        this.bloodBanks = bloodBanks;
    }

    public Collection<Incubator> getIncubators() {
        return incubators;
    }

    public void setIncubators(Collection<Incubator> incubators) {
        this.incubators = incubators;
    }

    public Collection<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(Collection<Consultation> consultations) {
        this.consultations = consultations;
    }

    public Collection<Examen> getExamens() {
        return examens;
    }

    public void setExamens(Collection<Examen> examens) {
        this.examens = examens;
    }
}
