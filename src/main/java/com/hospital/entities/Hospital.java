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
    private String province;
    private String category;
    private String addresse;
    private String email;
    private String telephone;
    private String siteInternet;



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

    /*@OneToMany(mappedBy = "hospital")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Examen> examens;*/

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Hospital() {
    }

    public Hospital(String name, String province, String category, String addresse, String email,
                    String telephone, String siteInternet, String city, String pays, String neighborhood) {
        this.name = name;
        this.province = province;
        this.category = category;
        this.addresse = addresse;
        this.email = email;
        this.telephone = telephone;
        this.siteInternet = siteInternet;
        this.city = city;
        this.pays = pays;
        this.neighborhood = neighborhood;
    }

    public String getAddresse() {
        return addresse;
    }

    public void setAddresse(String addresse) {
        this.addresse = addresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSiteInternet() {
        return siteInternet;
    }

    public void setSiteInternet(String siteInternet) {
        this.siteInternet = siteInternet;
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

    /*public Collection<Examen> getExamens() {
        return examens;
    }

    public void setExamens(Collection<Examen> examens) {
        this.examens = examens;
    }*/

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
