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


}
