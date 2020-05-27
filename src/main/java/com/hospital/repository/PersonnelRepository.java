package com.hospital.repository;

import com.hospital.entities.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

    Personnel findByEmail(String email);

    Personnel findByPhone(String phone);

    Personnel findByLastNameOrEmailOrPhone(String lastname, String Email, String phone);

    List<Personnel>findAllByFunction(String function);
}
