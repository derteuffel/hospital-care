package com.hospital.Controller;


import com.hospital.repository.CompteRepository;
import com.hospital.repository.DosMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hospital-care/dossier-medical")
public class DosMedicalController {


    @Autowired
    private DosMedicalRepository dos;

    @Autowired
    private CompteRepository compteRepository;
}
