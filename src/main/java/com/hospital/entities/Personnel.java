package com.hospital.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data
@Table(name = "personnel")
public class Personnel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastName;
    private String firstName;
    private String phone;
    private String city;
    private String district;
    private String email;
    private String occupation;
    private String photo;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "personnels_hospitals",
            joinColumns = @JoinColumn(
                    name = "personnel_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "hospital_id", referencedColumnName = "id"))
    private Collection<Hospital>hospitals;
}
