package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "hospital")
@Data
public class Hospital implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String city;

    private String pays;

    private String neighborhood;

    @NotNull
    private ArrayList<String> types = new ArrayList<String>();

   @OneToMany(mappedBy = "hospital")
   @OnDelete(action= OnDeleteAction.NO_ACTION)
   @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Personnel> personnels;

    @OneToMany(mappedBy = "hospital")
    private Collection<BloodBank>bloodBanks;


    @OneToMany(mappedBy = "hospital")
    private Collection<Incubator>incubators;

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
}
