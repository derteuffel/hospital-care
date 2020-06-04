package com.hospital.repository;

import com.hospital.entities.BloodBank;
import com.hospital.entities.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BloodBankRepository extends JpaRepository<BloodBank, Long> {
    List<BloodBank> findByHospital(Hospital hospital);
    Page<BloodBank> findAll(Pageable pageable);
    List<BloodBank> findByReferenceLike(String reference);
}
