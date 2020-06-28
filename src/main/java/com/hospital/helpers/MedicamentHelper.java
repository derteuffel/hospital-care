package com.hospital.helpers;

import com.hospital.entities.Medicament;
import com.hospital.entities.Pharmacy;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MedicamentHelper {

    private Integer quantity;
    private Double grammage;
    private String drugType;
    private String name;
    private Integer stockQuantity;
    private Double pricingUnit;
    private Boolean status;

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
}
