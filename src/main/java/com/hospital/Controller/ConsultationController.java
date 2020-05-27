package com.hospital.Controller;


import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.DosMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hospital-care/consultation")
public class ConsultationController {


    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private DosMedicalRepository dosMedicalRepository;
}
