package com.hospital.repository;

import com.hospital.entities.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Page<Hospital> findAll(Pageable pageable);
    Hospital findByName(String name);

}
