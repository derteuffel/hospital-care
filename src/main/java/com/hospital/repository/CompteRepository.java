package com.hospital.repository;

import com.hospital.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface CompteRepository  extends JpaRepository<Compte, Long> {


    Compte findByUsername(String username);



}
