package com.hospital.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
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

    private String type;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "hospitals_personnels",
            joinColumns = @JoinColumn(
                    name = "hospital_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "personnel_id", referencedColumnName = "id"))
    private Collection<Personnel> personnels;

    @OneToMany(mappedBy = "hospital")
    private Collection<BloodBank> bloodBanks;

    @OneToMany(mappedBy = "hospital")
    private Collection<Incubator>incubators;
}
