package com.hospital.helpers;

import com.hospital.entities.Compte;
import com.hospital.entities.DosMedical;

import javax.validation.constraints.*;

public class DosMedicalHelper {

    @Size(min = 1, max = 1, message = "rhesus must only be one character")
    private String rhesus;

    @NotBlank(message = "weight must not be null")
    private String weight;

    @NotBlank(message = "age must not be null")
    private String age;

    private String hereditaryDiseases;

    private String description;

    @Pattern(regexp = "^[0-9]{9}$", message = "code is a sequence of 9 digits")
    @NotBlank(message = "code must not be blank")
    private String code;

    @NotBlank(message = "email must not be blank")
    @Email(message = "this is not a valid email address")
    private String email;

    public DosMedical getDosMedicalInstance(Compte compte){
        return new DosMedical(rhesus,Integer.parseInt(weight),Integer.parseInt(age),hereditaryDiseases,description,code,compte);
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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
}
