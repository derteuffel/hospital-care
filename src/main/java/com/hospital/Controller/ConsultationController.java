package com.hospital.Controller;


import com.hospital.entities.Consultation;
import com.hospital.entities.DosMedical;
import com.hospital.entities.Hospital;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.DosMedicalRepository;
import com.hospital.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/consultation")
public class ConsultationController {

    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private DosMedicalRepository dosMedicalRepository;
    @Autowired
    private HospitalRepository hospitalRepository;


    /** Get all consultations in a medical record */
    @GetMapping(value = "/medical-record/{code}")
    public String getAllConsultationsInMedicalRecord(@PathVariable String code, Model model){
        DosMedical dosMedical = dosMedicalRepository.findByCode(code);
        List<Consultation> consultations = consultationRepository.findByDosMedical(dosMedical);
        model.addAttribute("consultationList",consultations);
        model.addAttribute("code",code);
        return "dashboard/pages/admin/consultation";
    }

    /** Get all consultations in an hospital */
    @GetMapping(value = "/hospital/{id}")
    @ResponseBody
    public List<Consultation> getAllConsultationsInHospital(@PathVariable Long id){
        Optional<Hospital> hospital =  hospitalRepository.findById(id);
        List<Consultation> consultations = consultationRepository.findByHospital(hospital);
        return consultations;
    }

    /** form for adding a consultation */
    @GetMapping(value = "/create")
    public String createConsultation(){
        return "dashboard/pages/admin/addConsultation";
    }

    /** Add a consultation */
    @PostMapping(value = "/create")
    public String addConsultation(@Valid Consultation consultation, Errors errors, Model model){
        if(errors.hasErrors()) {
            System.out.println(errors.hasErrors());
            return "error";
        }
        //dos.save(dosMedical);
        //model.addAttribute("dosMedicalList", dos.findAll());
        return "dashboard/pages/admin/dosMedical";
    }

    /** Research a consultation */
   /* @GetMapping(value = "research/")
    public Optional<Consultation> getMedicalRecord(@PathVariable Long id){
        return consultationRepository.findById(id);
    }*/
}
