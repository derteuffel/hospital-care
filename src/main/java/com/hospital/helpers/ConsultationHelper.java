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

    @NotNull(message = "name must not be null")
    private String DoctorName;

    @NotNull(message = "phone number field must not be null")
    private String DoctorPhone;

    private String observations;

    private String code;

    private Double tension;

//    @NotBlank(message = "hospitalName must not be blank")
    private String hospitalName;

    public ConsultationHelper() {
    }

    public ConsultationHelper(Date date, int height, double pressure, double weight, double temperature, String sex, int age, String doctorName, String doctorPhone, String observations,String hospitalName) {
        this.date = date;
        this.height = height;
        this.pressure = pressure;
        this.weight = weight;
        this.temperature = temperature;
        this.sex = sex;
        this.age = age;
        DoctorName = doctorName;
        DoctorPhone = doctorPhone;
        this.observations = observations;
        this.hospitalName = hospitalName;
    }

    public Consultation getConsultationInstance(Hospital hospital, DosMedical dosMedical, Personnel personnel){
        return new Consultation(date,height,weight,temperature,sex,age,pressure,tension,hospital,dosMedical,personnel,observations);
    }

    public static ConsultationHelper getConsultationHelperInstance(Consultation consultation){
            return new ConsultationHelper(consultation.getDate(),consultation.getHeight(),consultation.getPressure(),consultation.getWeight(),
                    consultation.getTemperature(),consultation.getSex(),consultation.getAge(), consultation.getPersonnel().getLastName(),consultation.getPersonnel().getPhone(),
                    consultation.getObservations(),consultation.getHospital().getName());
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

    public Double getTension() {
        return tension;
    }

    public void setTension(Double tension) {
        this.tension = tension;
    }
}
