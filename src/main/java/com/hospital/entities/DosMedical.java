package com.hospital.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;


@Entity
@Data
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

    @OneToMany(mappedBy = "dosMedical")
    private Collection<Consultation>consultations;

    @OneToMany(mappedBy = "dosMedical")
    private Collection<Ordonnace>ordonnaces;

}
