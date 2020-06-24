package com.hospital.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data
public class Medicament implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;
    private Double grammage;
    private String drugType;
    private String name;
    private Integer stockQuantity;
    private Double pricingUnit;
    private Boolean status;

    @ManyToOne
    @JsonIgnoreProperties("medicaments")
    private Pharmacy pharmacy;
}
