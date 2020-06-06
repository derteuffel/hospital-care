package com.hospital.helpers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.entities.Consultation;
import com.hospital.entities.Examen;
import com.hospital.entities.Hospital;
import com.hospital.entities.Prescription;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class PrescriptionHelper {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @NotNull(message = "you must specify a date")
    private Date date;

    @NotBlank(message = "dosage must not be blank")
    private String dosage;

    @NotNull(message = "idConsultation field must not be null")
    private Long idConsultation;

    public Prescription getPrescriptionInstance(Consultation consultation){
       return new Prescription(date,dosage,consultation);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public Long getIdConsultation() {
        return idConsultation;
    }

    public void setIdConsultation(Long idConsultation) {
        this.idConsultation = idConsultation;
    }
}
