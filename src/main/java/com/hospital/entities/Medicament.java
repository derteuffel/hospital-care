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

    public Medicament() {
    }

    public Medicament(Integer quantity, Double grammage, String drugType, String name, Integer stockQuantity, Double pricingUnit, Boolean status) {
        this.quantity = quantity;
        this.grammage = grammage;
        this.drugType = drugType;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.pricingUnit = pricingUnit;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getGrammage() {
        return grammage;
    }

    public void setGrammage(Double grammage) {
        this.grammage = grammage;
    }

    public String getDrugType() {
        return drugType;
    }

    public void setDrugType(String drugType) {
        this.drugType = drugType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Double getPricingUnit() {
        return pricingUnit;
    }

    public void setPricingUnit(Double pricingUnit) {
        this.pricingUnit = pricingUnit;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }
}
