package com.hospital.repository;

import com.hospital.entities.Consultation;
import com.hospital.entities.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

    Personnel findByEmail(String email);


    Personnel findByPhone(String phone);

    Personnel findByLastNameOrEmailOrPhone(String lastname, String Email, String phone);

    Personnel findByLastName(String lastName);

    List<Personnel>findAllByFunction(String function);
    List<Personnel> findAllByFunctionAndHospital_Id(String function, Long id);

    List<Personnel> findAllByHospital_Id(Long id);


}

