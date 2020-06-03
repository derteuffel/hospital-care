package com.hospital.helpers;

import com.hospital.entities.Consultation;
import com.hospital.entities.Examen;
import com.hospital.entities.Hospital;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class ExamenHelper {

    @NotBlank(message = "name field must not be blank")
    private String name;

    @NotBlank(message = "test type must not be blank")
    private String testType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date deliverDate;

    @NotNull(message = "you must specify a date of testing")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfTesting;

    private String results;

    private String description;

    @NotNull(message = "idConsultation field must not be null")
    private Long idConsultation;

    @NotBlank(message = "hospital name must not be blank")
    private String hospitalName;

    public Examen getExamInstance(Hospital hospital, Consultation consultation){
        Examen examen = new Examen();
        examen.setDateOfTesting(getDateOfTesting());
        examen.setDeliverDate(getDeliverDate());
        examen.setName(getName());
        examen.setResults(getResults());
        examen.setTestType(getTestType());
        examen.setDescription(getDescription());
        examen.setConsultation(consultation);
        examen.setHospital(hospital);
        return examen;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getIdConsultation() {
        return idConsultation;
    }

    public void setIdConsultation(Long idConsultation) {
        this.idConsultation = idConsultation;
    }

    public Date getDateOfTesting() {
        return dateOfTesting;
    }

    public void setDateOfTesting(Date dateOfTesting) {
        this.dateOfTesting = dateOfTesting;
    }

    public Date getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(Date deliverDate) {
        this.deliverDate = deliverDate;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
}
