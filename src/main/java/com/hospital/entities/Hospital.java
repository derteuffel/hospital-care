package com.hospital.entities;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data
@Table(name = "hospital")
public class Hospital implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String city;
    private String type;
    private String province;

    @OneToMany(mappedBy = "hospital")
    private Collection<BloodBank>bloodBanks;
    @OneToMany(mappedBy = "hospital")
    private Collection<Incubator>incubators;

}
