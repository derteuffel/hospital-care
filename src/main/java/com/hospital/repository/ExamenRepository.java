package com.hospital.repository;


import com.hospital.entities.Consultation;
import com.hospital.entities.Examen;
import com.hospital.entities.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamenRepository extends JpaRepository<Examen,Long> {
     List<Examen> findByHospital(Hospital hospital);
     List<Examen> findByConsultation(Consultation consultation);
}
