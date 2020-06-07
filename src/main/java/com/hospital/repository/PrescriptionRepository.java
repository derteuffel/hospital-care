package com.hospital.repository;

import com.hospital.entities.Consultation;
import com.hospital.entities.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription,Long> {
    List<Prescription> findByConsultation(Consultation consultation);
}