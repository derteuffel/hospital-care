package com.hospital.repository;

import com.hospital.entities.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Hospital findByName(String name);
}
