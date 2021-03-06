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

    @NotNull(message = "blood Type must not be null")
    private String bloodType;

    @Size(min = 1, max = 1, message = "rhesus must only be one character")
    private String rhesus;

    @NotNull(message = "sex must not be null")
    @Size(min = 1, max = 1, message = "sex one character")
    private String sex;

    // @NotBlank(message = "weight must not be null")
    private String weight;

    @NotNull(message = "height must not be null")
    private String height;

    @NotNull(message = "name must not be null")
    private String name;

    @NotNull(message = "you must specify a date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    private String hereditaryDiseases;

    private String description;

    @Pattern(regexp = "^[0-9]{9}$", message = "code is a sequence of 9 digits")
    @NotNull(message = "code must not be blank")
    private String code;

    @NotNull(message = "email must not be blank")
    @Email(message = "this is not a valid email address")
    private String email;

    public DosMedicalHelper() { }


    public DosMedicalHelper(String bloodType, String rhesus, String sex, String weight, String height, String name, Date birthDate, String hereditaryDiseases, String description,  String code, String email) {
        this.bloodType = bloodType;
        this.rhesus = rhesus;
        this.sex = sex;
        this.weight = weight;
        this.height = height;
        this.name = name;
        this.birthDate = birthDate;
        this.hereditaryDiseases = hereditaryDiseases;
        this.description = description;
        this.code = code;
        this.email = email;
    }

    public DosMedical getDosMedicalInstance(){
        return new DosMedical(rhesus, Double.parseDouble(weight), Integer.parseInt(height), birthDate, hereditaryDiseases,
                description, code, bloodType, name, sex);
    }
    public static DosMedicalHelper getDosMedicalHelperInstance(DosMedical dosMedical){
        return new DosMedicalHelper(dosMedical.getBloodType(), dosMedical.getRhesus(), dosMedical.getSex(),String.valueOf(dosMedical.getWeight()), String.valueOf(dosMedical.getHeight()), dosMedical.getName(),
                dosMedical.getBirthDate(), dosMedical.getHereditaryDiseases(), dosMedical.getDescription(),dosMedical.getCode(),dosMedical.getCompte().getEmail());
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
}
