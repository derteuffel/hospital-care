package com.hospital.repository;

import com.hospital.entities.Hospital;
import com.hospital.entities.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy,Long> {
    List<Pharmacy> findByHospital(Optional<Hospital> hospital);
}
