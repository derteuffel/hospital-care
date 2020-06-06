package com.hospital.helpers;

import com.hospital.entities.Compte;
import com.hospital.entities.DosMedical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.*;
import java.util.Date;

public class DosMedicalHelper {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @NotNull(message = "blood Type must not be null")
    private String bloodType;

    @Size(min = 1, max = 1, message = "rhesus must only be one character")
    private String rhesus;

    @NotBlank(message = "sex must not be null")
    @Size(min = 1, max = 1, message = "sex one character")
    private String sex;

   // @NotBlank(message = "weight must not be null")
    private String weight;

    @NotBlank(message = "height must not be null")
    private String height;

    @NotBlank(message = "name must not be null")
    private String name;
    @NotBlank(message = "age must not be null")
    private Integer age;

    @NotNull(message = "you must specify a date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    private String hereditaryDiseases;

    private String description;

    @Pattern(regexp = "^[0-9]{9}$", message = "code is a sequence of 9 digits")
    @NotBlank(message = "code must not be blank")
    private String code;

    @NotBlank(message = "email must not be blank")
    @Email(message = "this is not a valid email address")
    private String email;

    public DosMedicalHelper() {
    }

    public DosMedical getDosMedicalInstance(){
        return new DosMedical(rhesus, Double.parseDouble(weight), Integer.parseInt(height), birthDate, hereditaryDiseases,
                description, code, bloodType, name, sex, age);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRhesus() {
        return rhesus;
    }

    public void setRhesus(String rhesus) {
        this.rhesus = rhesus;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHereditaryDiseases() {
        return hereditaryDiseases;
    }

    public void setHereditaryDiseases(String hereditaryDiseases) {
        this.hereditaryDiseases = hereditaryDiseases;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
