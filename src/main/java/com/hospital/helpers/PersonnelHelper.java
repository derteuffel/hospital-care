package com.hospital.helpers;

import com.hospital.entities.Hospital;
import com.hospital.entities.Personnel;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.*;
import java.util.Date;

@Data
public class PersonnelHelper {

    @NotNull(message = "name field must not be blank")
    private String lastName;

    @NotNull(message = "name field must not be blank")
    private String firstName;
    @Email
    private String email;

    @NotNull(message = "phone field must not be blank")
    private String phone;

    @NotNull(message = "city field must not be blank")
    private String city;

    private String address;
    private String avatar;
    private String localisation;
    @Pattern(regexp = "^[0-9]{9}$", message = "code is a sequence of 9 digits")
    @NotNull(message = "code must not be blank")
    private String code;

    @NotNull(message = "blood Type must not be null")
    private String bloodType;

    @Size(min = 1, max = 1, message = "rhesus must only be one character")
    private String rhesus;

    @NotNull(message = "sex must not be null")
    @Size(min = 1, max = 1, message = "sex one character")
    private String sex;

    // @NotBlank(message = "weight must not be null")
    private String weight;


    private String height;

    @NotNull(message = "you must specify a date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    private String hereditaryDiseases;

    private String description;


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
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

    public String getCode() {
        return code;
    }

    public String getPhone() {
        return phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getRhesus() {
        return rhesus;
    }

    public void setRhesus(String rhesus) {
        this.rhesus = rhesus;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return this.city;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
