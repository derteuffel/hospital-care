package com.hospital.repository;

import com.hospital.entities.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    /*@Query("SELECT t FROM hospital t WHERE t.name LIKE CONCAT('%',:searchTerm,'%') OR t.city LIKE CONCAT('%',:searchTerm,'%') OR t.type LIKE CONCAT('%',:searchTerm,'%')")
    List<Hospital> findBySearch(@Param("searchTerm") String searchTerm);*/

    List<Hospital> findByNameLike(String name);

    Hospital findByName(String name);

}
