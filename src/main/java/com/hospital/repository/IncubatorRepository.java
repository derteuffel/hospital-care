package com.hospital.repository;

import com.hospital.entities.Hospital;
import com.hospital.entities.Incubator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncubatorRepository extends JpaRepository<Incubator,Long> {
    Page<Incubator> findAll(Pageable pageable);
    List<Incubator> findByNumberLike(String incubatorNo);
    List<Incubator> findByHospital(Hospital hospital);
    List<Incubator> findAllByHospital(Hospital hospital);
}
