package com.hospital.helpers;

import com.hospital.entities.BloodBank;
import com.hospital.entities.Hospital;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class BloodBankHelper {

    private String groupeSanguin;

    private String rhesus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date date;

    @NotNull(message = "idHospital field must not be null")
    private Long idHospital;

    public BloodBank getBloodBankInstance(Hospital hospital){
        BloodBank incubator = new BloodBank();
        incubator.setGroupeSanguin(getGroupeSanguin());
        incubator.setRhesus(getRhesus());
        incubator.setHospital(hospital);
        return incubator;
    }








}
