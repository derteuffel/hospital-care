package com.hospital.Controller;


import com.hospital.entities.Consultation;
import com.hospital.entities.DosMedical;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.DosMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@ResponseBody
@RequestMapping("/hospital-care/consultation")
public class ConsultationController {

    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private DosMedicalRepository dosMedicalRepository;
    @Autowired
    private DosMedicalRepository hospitalRepository;


    /** Get all consultations in a medical record */
    @GetMapping(value = "/medical-record/{code}")
    public List<Consultation> getAllConsultationsInMedicalRecord(@PathVariable String code){
        DosMedical dosMedical = dosMedicalRepository.findByCode(code);
        List<Consultation> consultations = consultationRepository.findByDosMedical(dosMedical);
        return consultations;
    }

    /** Get all consultations in an hospital */
    @GetMapping(value = "/hospital/{id}")
    public Consultation getAllConsultationsInHospital(@RequestBody Consultation consultation){
        consultationRepository.save(consultation);
        return consultation;
    }


    /** Research a consultation */
    @GetMapping(value = "research/")
    public Optional<Consultation> getMedicalRecord(@PathVariable Long id){
        return consultationRepository.findById(id);
    }
}
