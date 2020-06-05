package com.hospital.helpers;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BloodBankHelper {

    private String reference;

    private Integer quantity;

    private Boolean status;



}
