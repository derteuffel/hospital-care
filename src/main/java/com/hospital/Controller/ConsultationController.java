package com.hospital.Controller;


import com.hospital.entities.Consultation;
import com.hospital.entities.DosMedical;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.DosMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/hospital-care/consultation")
public class ConsultationController {


    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private DosMedicalRepository dosMedicalRepository;


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
    @GetMapping(value = "/medical-record/{id}")
    public List<Consultation> getAllConsultationsInMedicalRecord(){
        List<Consultation> consultations = consultationRepository.findAll();
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
