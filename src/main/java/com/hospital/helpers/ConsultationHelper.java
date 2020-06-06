package com.hospital.helpers;

import com.hospital.entities.Consultation;
import com.hospital.entities.DosMedical;
import com.hospital.entities.Hospital;
import com.hospital.entities.Personnel;
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

    @NotNull(message = "height field must not be null")
    private int height;

    @NotNull(message = "pressure field must not be null")
    private double pressure;

    @NotNull(message = "weight field must not be null")
    private double weight;

    @NotNull(message = "temperature field must not be null")
    private double temperature;

    @NotBlank(message = "sex field must not be blank")
    private String sex;

    @NotNull(message = "age field must not be null")
    private int age;

    @NotBlank(message = "name must not be null")
    private String DoctorName;

    @NotBlank(message = "phone number field must not be null")
    private String DoctorPhone;

    private String observations;

    private String code;

    @NotBlank(message = "hospitalName must not be blank")
    private String hospitalName;

    public ConsultationHelper() {
    }

    public Consultation getConsultationInstance(Hospital hospital, DosMedical dosMedical, Personnel personnel){
        return new Consultation(date,height,weight,temperature,sex,age,pressure,hospital,dosMedical,personnel,observations);
    }

    public String getCode() {
        return code;
    }

    public int getAge() {
        return age;
    }

    public int getHeight() {
        return height;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDoctorPhone() {
        return DoctorPhone;
    }

    public String getSex() {
        return sex;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getWeight() {
        return weight;
    }

    public double getPressure() {
        return pressure;
    }

    public void setDoctorPhone(String doctorPhone) {
        DoctorPhone = doctorPhone;
    }

    public String getDoctorName() {
        return DoctorName;
    }

    public String getObservations() {
        return observations;
    }

    public Date getDate() {
        return date;
    }

    public void setDoctorName(String doctorName) {
        DoctorName = doctorName;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
