package com.hospital.helpers;

import lombok.Data;

@Data
public class PharmacyHelper {


    private String name;

    private String drug;
    private Integer quantity;
    private Double grammage;
    private String drugType;
    private Integer stockQuantity;
    private Double pricingUnit;
    private Boolean status;
}
