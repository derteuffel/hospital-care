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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.validation.Errors;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

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



    @GetMapping("/lists")
    public String findAll(Model model){
        model.addAttribute("consultations", consultationRepository.findAll());
        return "consultation/consultations";
    }


    @GetMapping("/form")
    public String form(Model model, Long id){
        List<DosMedical> dosMedicals = dosMedicalRepository.findAll();
        model.addAttribute("consultation", new Consultation());
        model.addAttribute("dos", dosMedicals);
        return  "consultation/form";
    }

    @PostMapping("/save")
    public String saveConsultation(@Valid Consultation consultation, Long id, HttpSession httpSession){
        DosMedical dos = dosMedicalRepository.findById(id).get();
        consultation.setDosMedical(dos);
        httpSession.setAttribute("id", id);
        consultationRepository.save(consultation);
        return "redirect:/hospital-care/dossier-medical/"+httpSession.getAttribute("id");
    }

    @GetMapping("/update/{id}")
    public String updateConsultation(Model model, @PathVariable Long id){
        Consultation consultation = consultationRepository.getOne(id);
        model.addAttribute("consultation", consultation);
        return "consultation/edit";
    }

    @PostMapping("/update/{id}")
    public String update(@Valid Consultation consultation, @PathVariable("id") Long id,
                         BindingResult bindingResult, HttpSession session, Model model){

        DosMedical dos = dosMedicalRepository.getOne((Long)session.getAttribute("id"));
        consultationRepository.save(consultation);
        model.addAttribute("consultations", consultationRepository.findAll());

        return "redirect: /hospital-care/dossier-medical/"+consultation.getId();

    }


    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id){

        Consultation consultation = consultationRepository.findById(id).get();
        model.addAttribute("consultation", consultation);
        return  "consultation/detail";
    }

    @GetMapping("/delete/{id}")
    public String deleteConsultation(Model model, @PathVariable("id") Long id, HttpSession session){
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Invalid consultation id :" +id));
        System.out.println("consultation id"+consultation.getId());
        DosMedical dos = dosMedicalRepository.getOne((Long)session.getAttribute("id"));
        consultationRepository.delete(consultation);
        model.addAttribute("consultations", consultationRepository.findAll());
        return "redirect: /hospital-care/dossier-medical/"+dos.getId();
    }


    /** owner-developer branch code **/


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


    /** Research a consultation */
   /* @GetMapping(value = "research/")
    public Optional<Consultation> getMedicalRecord(@PathVariable Long id){
        return consultationRepository.findById(id);

    }


    }*/


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
