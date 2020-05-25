package com.hospital.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "bloodbank")
@Data
public class BloodBank implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reference;

    private Integer quantity;

    private Boolean status;


    @ManyToOne
    private Hospital hospital;
}
