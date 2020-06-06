package com.hospital.services;

import com.hospital.entities.Compte;
import com.hospital.entities.DosMedical;
import com.hospital.helpers.CompteRegistrationDto;

import org.springframework.security.core.userdetails.UserDetailsService;


import java.util.List;

public interface CompteService extends UserDetailsService {

    Compte findByUsername(String username);
    Compte save(CompteRegistrationDto compteRegistrationDto, String s);
    Compte savePatient(CompteRegistrationDto compteRegistrationDto, String s, DosMedical dosMedical);
<<<<<<< HEAD

    public List<Compte> findAllCompte();
=======
    List<Compte> findAllCompte();
>>>>>>> owner-developer


}
