package com.hospital.controller;

import com.hospital.entities.*;
import com.hospital.enums.ERole;
import com.hospital.helpers.ConsultationHelper;
import com.hospital.helpers.ExamenHelper;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.ExamenRepository;
import com.hospital.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/admin/exam")
public class ExamenController {


    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private CompteRepository compteRepository;



    /** Get all exams made in an hospital */
   /* @GetMapping(value = "/hospital/{id}")
    @ResponseBody
    public List<Examen> getAllExamsInHospital(@PathVariable Long id){
        Hospital hospital =  hospitalRepository.getOne(id);
        List<Examen> exams = examenRepository.findByHospital(hospital);
        return exams;
    }*/

    /** Get all exams of a consultation */
    @GetMapping(value = "/consultation/{id}")
    public String getAllExamsOfAConsultation(@PathVariable Long id, Model model){
        Consultation consultation =  consultationRepository.getOne(id);
        List<Examen> exams = examenRepository.findByConsultation(consultation);
        model.addAttribute("examList",exams);
        model.addAttribute("idConsultation",id);
        return "dashboard/pages/admin/examen/exam";
    }


    @GetMapping("/hospital/{id}")
    public String getAllExamensOfHospital(@PathVariable Long id, Model model){
        Hospital hospital = hospitalRepository.getOne(id);

        List<Examen> examens = new ArrayList<>();
        Collection<Consultation> consultations = hospital.getConsultations();
        for (Consultation consultation : consultations){
            examens.addAll(consultation.getExamens());
        }
        model.addAttribute("lists",examens);
        return "dashboard/pages/admin/hospital/exams";
    }

    /** form for adding an exam */
    @GetMapping(value = "/create")
    public String addExam(@RequestParam("idConsultation") int  idConsultation, Model model){
        List<Hospital> hospitals = hospitalRepository.findAll();
        model.addAttribute("idConsultation",idConsultation);
        model.addAttribute("hospitalList",hospitals);
        model.addAttribute(new ExamenHelper());
        return "dashboard/pages/admin/examen/addExam";
    }

    /** Add an exam */
    @PostMapping("/create")
    public String saveExam(@ModelAttribute @Valid ExamenHelper examenHelper,Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute("hospitalList",hospitalRepository.findAll());
            model.addAttribute("idConsultation",examenHelper.getIdConsultation());
            return "dashboard/pages/admin/examen/addExam";
        }else{
            Consultation consultation = consultationRepository.getOne(examenHelper.getIdConsultation());
            Hospital hospital = hospitalRepository.findByName(examenHelper.getHospitalName());
            examenRepository.save(examenHelper.getExamInstance(hospital,consultation));
        }
        return  "redirect:/admin/exam/consultation/"+examenHelper.getIdConsultation();
    }

    /** cancel an exam */
    @PostMapping(value = "/cancel")
    public String cancelExam(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes){

        Long id = Long.parseLong(request.getParameter("id"));
        String username = request.getParameter("username");
        String idConsultation = request.getParameter("idConsultation");
        Compte compte = compteRepository.findByUsername(username);
        boolean authorized = false;

        if(compte == null){
            model.addAttribute("error","There is no account with this username");
            System.out.println(model.getAttribute("error"));
            return "redirect:/admin/exam/consultation/"+idConsultation;
        }else{
            for (Role role : compte.getRoles()){
                if(role.getName().equals(ERole.ROLE_ROOT.toString())){
                    authorized = true;
                }
            }

            if(!authorized){
                model.addAttribute("error","you don't have rights to perform this operation");
                System.out.println(model.getAttribute("error"));
                return "redirect:/admin/exam/consultation/"+idConsultation;
            }
        }

        examenRepository.deleteById(id);
        model.addAttribute("success","Operation successfully completed");
        System.out.println(model.getAttribute("success"));
        return "redirect:/admin/exam/consultation/"+idConsultation;
    }

    /** form for updating an exam */
    @GetMapping(value = "/update/{idExam}")
    public String updateExam(@PathVariable Long idExam, @RequestParam("idConsultation") Long idConsultation, Model model){
        List<Hospital> hospitals = hospitalRepository.findAll();
        model.addAttribute("idConsultation",idConsultation);
        model.addAttribute("hospitalList",hospitals);
        model.addAttribute("examenHelper", ExamenHelper.getExamenHelperInstance(examenRepository.getOne(idExam)));

        return "dashboard/pages/admin/examen/updateExam";
    }

    /** Update an exam */
    @PostMapping(value = "/update/{idExam}")
    public String updateExam(@PathVariable Long idExam, @ModelAttribute @Valid ExamenHelper examenHelper, Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute("hospitalList",hospitalRepository.findAll());
            model.addAttribute("idConsultation",examenHelper.getIdConsultation());
            return "dashboard/pages/admin/examen/updateExam";
        }else{
            Consultation consultation = consultationRepository.getOne(examenHelper.getIdConsultation());
            Hospital hospital = hospitalRepository.findByName(examenHelper.getHospitalName());
            Examen exExam = examenRepository.getOne(idExam);
            Examen newExam = examenHelper.getExamInstance(hospital,consultation);
            newExam.setId(exExam.getId());
            newExam.setHospital(hospital);
            examenRepository.save(newExam);
        }
       /* Compte compte = compteRepository.findByUsername(username);
        boolean authorized = false;

        if(compte == null){
            model.addAttribute("error","There is no account with this username");
            return "redirect:/admin/medical-record/all";
        }else{
            for (Role role : compte.getRoles()){
                if(role.getName().equals(ERole.ROLE_ROOT.toString())){
                    authorized = true;
                }
            }

            if(!authorized){
                model.addAttribute("error","you don't have rights to perform this operation");
                return "redirect:/admin/medical-record/all";
            }
        }*/

        model.addAttribute("success","Operation successfully completed");
        System.out.println(model.getAttribute("success"));
        return  "redirect:/admin/exam/consultation/"+examenHelper.getIdConsultation();
    }


}
