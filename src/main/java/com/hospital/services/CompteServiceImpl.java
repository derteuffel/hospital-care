package com.hospital.services;


import com.hospital.entities.Compte;
import com.hospital.entities.Personnel;
import com.hospital.entities.Role;
import com.hospital.helpers.CompteRegistrationDto;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.PersonnelRepository;
import com.hospital.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompteServiceImpl implements CompteService {

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PersonnelRepository personnelRepository;



    @Override
    public Compte findByUsername(String username) {
        return compteRepository.findByUsername(username);
    }

    @Override
    public Compte save(CompteRegistrationDto compteRegistrationDto, String s) {
        Compte compte = new Compte();
        Personnel personnel = new Personnel();
        personnel.setLastName(compteRegistrationDto.getUsername());
        personnel.setEmail(compteRegistrationDto.getEmail());
        personnel.setAvatar(s);
        compte.setUsername(compteRegistrationDto.getUsername());
        compte.setPassword(passwordEncoder.encode(compteRegistrationDto.getPassword()));
        compte.setEmail(compteRegistrationDto.getEmail());
        compte.setAvatar(s);
        personnelRepository.save(personnel);
        Role role = new Role();
        if (compteRepository.findAll().size()<=2){
            role.setName("ROLE_ROOT");
        }else {
            role.setName("ROLE_USER");
        }
        Optional<Role> existRole =  roleRepository.findByName(role.getName());
        if (existRole.isPresent()){
            compte.setRoles(Arrays.asList(existRole.get()));
        }else {
            System.out.println(role.getName());
            roleRepository.save(role);
            compte.setRoles(Arrays.asList(role));
        }

        compteRepository.save(compte);
        return compte;
    }

    @Override
    public List<Compte> findAllCompte() {
        return compteRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println(username);
        Compte compte = compteRepository.findByUsername(username);
        if (compte == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(compte.getUsername(),
                compte.getPassword(),
                mapRolesToAuthorities(compte.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
