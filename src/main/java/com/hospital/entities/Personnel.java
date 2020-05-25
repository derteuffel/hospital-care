package com.hospital.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "personnel")
public class Personnel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastName;
    private String firstName;
    private String phone;
    private String city;
    private String fuction;
}
