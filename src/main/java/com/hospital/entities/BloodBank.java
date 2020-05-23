package com.hospital.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
@Table(name = "blood_bank")
public class BloodBank implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference;
    private Integer quantity;
    private Boolean state;

    @ManyToOne
    private Hospital hospital;
}
