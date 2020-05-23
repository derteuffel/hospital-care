package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
@Table(name = "BloodBank")
public class BloodBank implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference;
    private Integer quantity;
    private Boolean state;

    @ManyToOne
    @JsonIgnoreProperties("bloodBanks")
    private Hospital hospital;
}
