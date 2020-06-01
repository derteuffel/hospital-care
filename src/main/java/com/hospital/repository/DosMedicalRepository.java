package com.hospital.repository;


import com.hospital.entities.DosMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DosMedicalRepository extends JpaRepository<DosMedical, Long> {
    DosMedical findByCode(String code);
}
