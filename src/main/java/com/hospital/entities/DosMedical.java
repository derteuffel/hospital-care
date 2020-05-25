package com.hospital.entities;

import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Consultation>consultations;



    @OneToMany(mappedBy = "dosMedical")
    private Collection<Ordonnace>ordonnaces;

}
