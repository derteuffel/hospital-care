package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;


@Entity
@Table(name = "consultation")
@Data
public class Consultation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date date;

    private int height;
    private double pressure;
    private double weight;
    private double temperature;
    private String sex;
    private int age;
    private String observations;

    @OneToMany(mappedBy = "consultation")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Examen>examens;

    @ManyToOne
    @JsonIgnoreProperties("consultations")
    private DosMedical dosMedical;

    @ManyToOne
    @JsonIgnoreProperties("consultations")
    private Hospital hospital;


    @ManyToOne
    @JsonIgnoreProperties("consultations")
    private Personnel personnel;

    @OneToMany(mappedBy = "consultation")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Ordonnance>ordonnances;

    public Consultation(Date date, int height, double weight, double temperature, String sex, int age, double pressure, Hospital hospital, DosMedical dosMedical, Personnel personnel, String observations){
        this.date = date;
        this.height = height;
        this.weight = weight;
        this.pressure = pressure;
        this.age = age;
        this.sex = sex;
        this.hospital = hospital;
        this.dosMedical = dosMedical;
        this.temperature = temperature;
        this.observations = observations;
        this.personnel = personnel;
    }

    public Consultation(){}

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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Collection<Examen> getExamens() {
        return examens;
    }

    public void setExamens(Collection<Examen> examens) {
        this.examens = examens;
    }

    public DosMedical getDosMedical() {
        return dosMedical;
    }

    public void setDosMedical(DosMedical dosMedical) {
        this.dosMedical = dosMedical;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public Personnel getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
    }

    public Collection<Ordonnance> getOrdonnances() {
        return ordonnances;
    }

    public void setOrdonnances(Collection<Ordonnance> ordonnances) {
        this.ordonnances = ordonnances;
    }
}
