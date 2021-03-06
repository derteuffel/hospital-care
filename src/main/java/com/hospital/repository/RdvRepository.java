package com.hospital.repository;

import com.hospital.entities.Irdvjointure;
import com.hospital.entities.Rdv;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RdvRepository extends JpaRepository<Rdv, Long> {
    /*@Query(value = "SELECT patient.*,medecin.username AS personnel FROM (SELECT a.motif,a.date,a.compte_id,a.personnel_id,e.username,a.id ,a.status FROM rdv AS a INNER JOIN compte AS e ON a.compte_id = e.id) AS patient INNER JOIN compte AS medecin ON medecin.id = patient.personnel_id WHERE medecin.id = patient.personnel_id", nativeQuery = true)
    List<Irdvjointure> findAllWithJoin();*/

    List<Rdv> findAllByComptes_Id(Long id, Sort sort);
    List<Rdv> findAllByStatusAndComptes_Id(Boolean status, Long id, Sort sort);
    List<Rdv>findByStatus(Boolean status);
    List<Rdv> findAllByHospital_Id(Long id,Sort sort);
    List<Rdv> findAllByHospital_IdAndStatus(Long id,Boolean status,Sort sort);
}