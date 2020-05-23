package com.hospital.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data
@Table(name = "DosMedical")
public class DosMedical implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rh;
    private String size;
    private String weight;
    private String age;
    private String hereditaryDisease;
    private String description;
    private String code;


    @OneToMany(mappedBy = "DosMedical")
    private Collection<Consultation>consultations;
    @OneToMany(mappedBy = "DosMedical")
    private Collection<Ordonnace>ordonnaces;
}
