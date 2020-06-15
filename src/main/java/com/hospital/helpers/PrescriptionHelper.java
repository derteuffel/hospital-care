package com.hospital.helpers;

import com.hospital.entities.Consultation;
import com.hospital.entities.Prescription;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
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
    
    private String dosage;

    @NotNull(message = "idConsultation field must not be null")
    private Long idConsultation;

    public PrescriptionHelper(){

    }

    public PrescriptionHelper(Date date, String dosage, Long idConsultation) {
        this.date = date;
        this.dosage = dosage;
        this.idConsultation = idConsultation;
    }

    public Prescription getPrescriptionInstance(Consultation consultation){
        return new Prescription(date,dosage,consultation);
    }

    public static PrescriptionHelper getPrescriptionHelperInstance(Prescription prescription){
        return new PrescriptionHelper(prescription.getDate(),prescription.getDosage(),prescription.getConsultation().getId());
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
