package com.hospital.repository;

import com.hospital.entities.Others;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OthersRepository extends JpaRepository<Others, Long> {
}
