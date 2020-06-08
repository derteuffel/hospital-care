package com.hospital.helpers;

import com.hospital.entities.Hospital;
import com.hospital.entities.Incubator;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class IncubatorHelper {


    private String type;

    private String number;

    @NotNull(message = "you must specify a date of testing")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateObtained;

    @NotNull(message = "idHospital field must not be null")
    private Long idHospital;
    @NotNull
    private String status;

    public IncubatorHelper(String type, String number,
                          String status, Date dateObtained, String name) {
    }

    public IncubatorHelper() {
    }

    public Incubator getIncubatorInstance(Hospital hospital){
        Incubator incubator = new Incubator();
        incubator.setDateObtained(getDateObtained());
        incubator.setNumber(getNumber());
        incubator.setType(getType());
        incubator.setStatus(getStatus());
        incubator.setHospital(hospital);
        return incubator;
    }

    public static IncubatorHelper getIncubatorHelperInstance(Incubator incubator){
        return new IncubatorHelper(incubator.getType(), incubator.getNumber(),incubator.getStatus(),
                incubator.getDateObtained(),incubator.getHospital().getName());
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

    public Date getDateObtained() {
        return dateObtained;
    }

    public void setDateObtained(Date dateObtained) {
        this.dateObtained = dateObtained;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
