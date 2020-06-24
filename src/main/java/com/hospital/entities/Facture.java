package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@Entity
@Data
public class Facture implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private String billNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date date;
    private Double montantT;
    private Double montantVerse;
    private Double remboursement;
    private ArrayList<String> articles = new ArrayList<>();
    private ArrayList<Float> prices = new ArrayList<>();
    private ArrayList<Integer> quantities = new ArrayList<>();

    @ManyToOne
    @JsonIgnoreProperties("pharmacies")
    private Pharmacy pharmacy;
}
