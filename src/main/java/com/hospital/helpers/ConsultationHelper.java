package com.hospital.helpers;

import com.hospital.entities.Consultation;
import com.hospital.entities.DosMedical;
import com.hospital.entities.Hospital;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class ConsultationHelper {

    @NotNull(message = "you must specify a date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date date;

    @NotBlank(message = "code field must not be blank")
    private String code;

    @NotBlank(message = "hospitalName must not be blank")
    private String hospitalName;

    public Consultation getConsultationInstance(Hospital hospital, DosMedical dosMedical){
        Consultation consultation = new Consultation();
        consultation.setDate(getDate());
        consultation.setHospital(hospital);
        consultation.setDosMedical(dosMedical);
        return consultation;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
