package com.hospital.repository;

import com.hospital.entities.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonnelRepositry extends JpaRepository<Personnel, Long> {
}
