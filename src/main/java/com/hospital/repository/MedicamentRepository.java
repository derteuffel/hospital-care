package com.hospital.repository;

import com.hospital.entities.Medicament;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentRepository extends JpaRepository<Medicament,Long> {

    List<Medicament> findAllByPharmacy_Id(Long id, Sort sort);
}
