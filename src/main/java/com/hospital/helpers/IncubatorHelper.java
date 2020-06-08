package com.hospital.helpers;

import com.hospital.entities.Hospital;
import com.hospital.entities.Incubator;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class IncubatorHelper {


    private String type;

    private String number;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private String dateObtained;

    @NotNull(message = "idHospital field must not be null")
    private Long idHospital;

    @NotNull
    @Size(min = 1, max = 1)
    private String status;

    public Incubator getIncubatorInstance(Hospital hospital){
        Incubator incubator = new Incubator();
        incubator.setDateObtained(getDateObtained());
        incubator.setNumber(getNumber());
        incubator.setType(getType());
        incubator.setStatus(getStatus());
        incubator.setHospital(hospital);
        return incubator;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String incubatorNo) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateObtained() {
        return dateObtained;
    }

    public void setDateObtained(String dateObtained) {
        this.dateObtained = dateObtained;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
