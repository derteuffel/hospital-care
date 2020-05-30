package com.hospital.services;


import com.hospital.entities.Compte;
import com.hospital.helpers.CompteRegistrationDto;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface CompteService extends UserDetailsService {

    Compte findByUsername(String username);
    Compte save(CompteRegistrationDto compteRegistrationDto, String s);
    public List<Compte> findAllCompte();
}
