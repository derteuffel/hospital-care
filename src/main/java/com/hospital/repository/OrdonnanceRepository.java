package com.hospital.repository;

import com.hospital.entities.Ordonnace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdonnanceRepository extends JpaRepository<Ordonnace, Long> {
}
