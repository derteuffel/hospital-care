package com.hospital.helpers;

import com.hospital.entities.Hospital;
import com.hospital.entities.Personnel;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

    private String gender;

    private Integer age;

    private String address;
    private String avatar;
    @NotNull(message = "idHospital field must not be null")
    private Long idHospital;

    public PersonnelHelper() {
    }

    public PersonnelHelper(String lastName,  String firstName, String email,String phone, String city,  String function,
                           String gender, Integer age, String address, String avatar,Long idHospital) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.function = function;
        this.gender = gender;
        this.age = age;
        this.address = address;
        this.avatar = avatar;
        this.idHospital = idHospital;
    }

    public Personnel getPersonnelInstance(Hospital hospital){
        Personnel per = new Personnel();
        per.setLastName(getLastName());
        per.setFirstName(getFirstName());
        per.setEmail(getEmail());
        per.setCity(getCity());
        per.setGender(getGender());
        per.setAddress(getAddress());
        per.setAge(getAge());
        per.setFunction(getFunction());
        per.setAvatar(getAvatar());

        per.setHospital(hospital);
        return per;
    }
}
