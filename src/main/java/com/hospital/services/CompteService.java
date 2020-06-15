package com.hospital.services;

import com.hospital.entities.Compte;
import com.hospital.entities.DosMedical;
import com.hospital.entities.Personnel;
import com.hospital.helpers.CompteRegistrationDto;

import com.hospital.helpers.PersonnelHelper;
import org.springframework.security.core.userdetails.UserDetailsService;


import java.util.List;

public interface CompteService extends UserDetailsService {

    Compte findByUsername(String username);
    Compte save(CompteRegistrationDto compteRegistrationDto, String s);
    Compte savePatient(CompteRegistrationDto compteRegistrationDto, String s, DosMedical dosMedical);
    Compte saveDoctor(PersonnelHelper personnelHelper, String s, DosMedical dosMedical, Personnel personnel);
    Compte saveSimple(PersonnelHelper personnelHelper, String s, DosMedical dosMedical, Personnel personnel);

    public List<Compte> findAllCompte();

}
