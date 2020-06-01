package com.hospital.Controller;

import com.hospital.repository.DosMedicalRepository;
import com.hospital.repository.OrdonnanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hospital-care/ordonnance")
public class OrdonnanceController {

    @Autowired
    private OrdonnanceRepository ordonnanceRepository;

    @Autowired
    private DosMedicalRepository dosMedicalRepository;
}
