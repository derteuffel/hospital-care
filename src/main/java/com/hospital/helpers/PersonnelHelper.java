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

    @NotBlank(message = "name field must not be blank")
    private String lastName;

    @NotBlank(message = "name field must not be blank")
    private String firstName;
    @Email
    @NotBlank
    private String email;

    @NotBlank(message = "phone field must not be blank")
    private String phone;

    @NotBlank(message = "city field must not be blank")
    private String city;

    @NotBlank(message = "function field must not be blank")
    private String function;

    private String address;
    private String avatar;
    private String localisation;
    @Pattern(regexp = "^[0-9]{9}$", message = "code is a sequence of 9 digits")
    @NotBlank(message = "code must not be blank")
    private String code;

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

    @NotNull(message = "you must specify a date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    private String hereditaryDiseases;

    private String description;



}
