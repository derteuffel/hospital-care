package com.hospital.Controller;


import com.hospital.entities.Compte;
import com.hospital.entities.Consultation;
import com.hospital.entities.DosMedical;
import com.hospital.entities.Hospital;
import com.hospital.helpers.ConsultationHelper;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.DosMedicalRepository;
import com.hospital.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private CompteRepository compteRepository;


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
    public String addConsultation(@RequestParam("code") String code, Model model){
        List<Hospital> hospitals = hospitalRepository.findAll();
        model.addAttribute("hospitalList",hospitals);
        model.addAttribute("code",code);
        model.addAttribute(new ConsultationHelper());
        return "dashboard/pages/admin/addConsultation";
    }

    /** Add a consultation */
    @PostMapping(value = "/create")
    public String saveConsultation(@ModelAttribute @Valid ConsultationHelper consultationHelper, Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute("hospitalList",hospitalRepository.findAll());
            model.addAttribute("code",consultationHelper.getCode());
            return "dashboard/pages/admin/addConsultation";
        }else{
            DosMedical dosMedical = dosMedicalRepository.findByCode(consultationHelper.getCode());
            Hospital hospital = hospitalRepository.findByName(consultationHelper.getHospitalName());
            consultationRepository.save(consultationHelper.getConsultationInstance(hospital,dosMedical));
        }
        return  "redirect:/admin/consultation/medical-record/"+consultationHelper.getCode();
    }

    /** cancel an exam */
    @PostMapping(value = "/cancel")
    public String cancelConsultation(HttpServletRequest request, Model model){

        Long id = Long.parseLong(request.getParameter("id"));
        String password = request.getParameter("password");
        String code = request.getParameter("code");
       // return "--"+password+"--"+code+"--";
        Compte compte = compteRepository.findByPassword(password);

        if(compte != null){
            if(compte.getStatus()){
                consultationRepository.deleteById(id);
            }
        }
        return "redirect:/admin/consultation/medical-record/"+code;
    }
}
