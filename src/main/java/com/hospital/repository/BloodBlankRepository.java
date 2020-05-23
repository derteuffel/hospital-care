package com.hospital.repository;

import com.hospital.entities.BloodBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BloodBlankRepository extends JpaRepository<BloodBank, Long> {
}
