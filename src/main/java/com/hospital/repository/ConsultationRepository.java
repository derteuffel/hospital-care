package com.hospital.repository;

import com.hospital.entities.Consultation;
import com.hospital.entities.DosMedical;
import com.hospital.entities.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation,Long> {
    List<Consultation> findByDosMedical(DosMedical dosMedical);
    List<Consultation> findByHospital(Optional<Hospital> hospital);
}
