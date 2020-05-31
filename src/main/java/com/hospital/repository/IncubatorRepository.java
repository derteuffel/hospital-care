package com.hospital.repository;

import com.hospital.entities.Incubator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncubatorRepository extends JpaRepository<Incubator,Long> {
}
