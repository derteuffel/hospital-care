package com.hospital.helpers;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import java.util.Date;

@Data
public class IncubatorHelper {


    private String type;

    private String number;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateObtained;
    private int quantity;
    @NotNull
    private String status;

    private Boolean state;



}
