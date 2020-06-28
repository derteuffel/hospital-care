package com.hospital.Controller;


import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.ExamenRepository;
import com.hospital.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PatientController {


    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private ExamenRepository examenRepository;
}
