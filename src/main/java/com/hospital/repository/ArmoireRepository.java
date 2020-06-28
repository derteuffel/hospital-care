package com.hospital.repository;

import com.hospital.entities.Armoire;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArmoireRepository extends JpaRepository<Armoire,Long> {

    List<Armoire> findAllByCompte_Id(Long id, Sort sort);
}
