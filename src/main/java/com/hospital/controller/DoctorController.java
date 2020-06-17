package com.hospital.controller;

import com.hospital.entities.Compte;
import com.hospital.entities.Hospital;
import com.hospital.entities.Personnel;
import com.hospital.enums.ERole;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.HospitalRepository;
import com.hospital.repository.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/doctors")
public class DoctorController {

    @Autowired
    private CompteRepository compteRepository;
    @Autowired
    private PersonnelRepository personnelRepository;
    @Autowired
    private HospitalRepository hospitalRepository;

    /** Get all doctors */
    @GetMapping("/all")
    public String getAllDoctors(Model model){
       // System.out.println(doctors);
        //model.addAttribute("doctors",doctors);
        return "dashboard/pages/admin/doctors";
    }

    /** Get all doctors in an hospital */
    @GetMapping("/{hospitalName}/all")
    public List<Personnel> getAllDoctorsInHospital(@PathVariable String hospitalName, Model model){
        List<Compte> comptes = compteRepository.findByRolesName(ERole.ROLE_DOCTOR.toString());
        Hospital myHospital = hospitalRepository.findByName(hospitalName);
        List<Personnel> doctors = new ArrayList<Personnel>();

        List<Personnel> personnels = personnelRepository.findAllByHospital_Id(myHospital.getId());
        for (Compte compte1 : comptes){ for (Personnel personnel : personnels){ if (compte1.getPersonnel() == personnel) doctors.add(personnel); } }
        return doctors;
    }
}
