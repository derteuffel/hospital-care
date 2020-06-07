package com.hospital.Controller;


import com.hospital.entities.*;
import com.hospital.enums.ERole;
import com.hospital.helpers.ConsultationHelper;
import com.hospital.helpers.DosMedicalHelper;
import com.hospital.repository.*;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private PersonnelRepository personnelRepository;
    @Autowired
    private ExamenRepository examenRepository;
    @Autowired
    private PrescriptionRepository prescriptionRepository;



  /*  @GetMapping("/lists")
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
    }*/


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
        DosMedical dosMedical = dosMedicalRepository.findByCode(code);
        List<Compte> doctors = compteRepository.findByRolesName(ERole.ROLE_ROOT.toString());
        Long days = TimeUnit.DAYS.convert(new Date().getTime() - dosMedical.getBirthDate().getTime(),TimeUnit.MILLISECONDS);

        model.addAttribute("age",Math.round(days/365));
        model.addAttribute("doctors",doctors);
        model.addAttribute("hospitalList",hospitals);
        model.addAttribute("patient",dosMedical);
        model.addAttribute("code",code);

        model.addAttribute("consultationHelper", new ConsultationHelper());
        return "dashboard/pages/admin/addConsultation";
    }

    /** Add a consultation */
    @PostMapping(value = "/create")
    public String saveConsultation(@ModelAttribute @Valid ConsultationHelper consultationHelper, Errors errors, Model model){
        if(errors.hasErrors()){
            List<Compte> doctors = compteRepository.findByRolesName(ERole.ROLE_ROOT.toString());

            model.addAttribute("doctors",doctors);
            model.addAttribute("hospitalList",hospitalRepository.findAll());
            model.addAttribute("code",consultationHelper.getCode());
            return "dashboard/pages/admin/addConsultation";
        }

        DosMedical dosMedical = dosMedicalRepository.findByCode(consultationHelper.getCode());
        Hospital hospital = hospitalRepository.findByName(consultationHelper.getHospitalName());
        Personnel doctor = personnelRepository.findByLastName(consultationHelper.getDoctorName());
        consultationRepository.save(consultationHelper.getConsultationInstance(hospital,dosMedical,doctor));

        model.addAttribute("success","consultation successfully added");
        System.out.println("done");
        return  "redirect:/admin/consultation/medical-record/"+consultationHelper.getCode();
    }

    /** form for updating a consultation */
    @GetMapping(value = "/update/{idConsultation}")
    public String updateConsultation(@PathVariable Long idConsultation, @RequestParam("code") String code, Model model){
        List<Hospital> hospitals = hospitalRepository.findAll();
        DosMedical dosMedical = dosMedicalRepository.findByCode(code);
        List<Compte> doctors = compteRepository.findByRolesName(ERole.ROLE_ROOT.toString());
        Long days = TimeUnit.DAYS.convert(new Date().getTime() - dosMedical.getBirthDate().getTime(),TimeUnit.MILLISECONDS);

        model.addAttribute("age",Math.round(days/365));
        model.addAttribute("doctors",doctors);
        model.addAttribute("hospitalList",hospitals);
        model.addAttribute("patient",dosMedical);
        model.addAttribute("code",code);
        model.addAttribute("consultationHelper", ConsultationHelper.getConsultationHelperInstance(consultationRepository.getOne(idConsultation)));

        return "dashboard/pages/admin/updateConsultation";
    }

    /** Update a consultation */
    @PostMapping(value = "/update/{idConsultation}")
    public String updateConsultation(@PathVariable Long idConsultation, @ModelAttribute @Valid ConsultationHelper consultationHelper, Errors errors, Model model){
        if(errors.hasErrors()){
            List<Compte> doctors = compteRepository.findByRolesName(ERole.ROLE_ROOT.toString());
            model.addAttribute("doctors",doctors);
            model.addAttribute("hospitalList",hospitalRepository.findAll());
            model.addAttribute("code",consultationHelper.getCode());
            return "dashboard/pages/admin/updateConsultation";
        }

        DosMedical dosMedical = dosMedicalRepository.findByCode(consultationHelper.getCode());
        Hospital hospital = hospitalRepository.findByName(consultationHelper.getHospitalName());
        Personnel doctor = personnelRepository.findByLastName(consultationHelper.getDoctorName());

        Consultation exConsultation = consultationRepository.getOne(idConsultation);
        Consultation newConsultation = consultationHelper.getConsultationInstance(hospital,dosMedical,doctor);
        newConsultation.setId(exConsultation.getId());
        newConsultation.setHospital(hospital);
        newConsultation.setPersonnel(doctor);
        consultationRepository.save(newConsultation);
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
        return  "redirect:/admin/consultation/medical-record/"+consultationHelper.getCode();
    }


    /** Research a consultation */
   /* @GetMapping(value = "research/")
    public Optional<Consultation> getMedicalRecord(@PathVariable Long id){
        return consultationRepository.findById(id);

    }


    }*/


    /** cancel a consultation */
    @PostMapping(value = "/cancel")
    public String cancelConsultation(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes){

        Long id = Long.parseLong(request.getParameter("id"));
        String username = request.getParameter("username");
        String code = request.getParameter("code");
        Compte compte = compteRepository.findByUsername(username);
        boolean authorized = false;

        if(compte == null){
            model.addAttribute("error","There is no account with this username");
            System.out.println(model.getAttribute("error"));
            return "redirect:/admin/consultation/medical-record/"+code;
        }else{
            for (Role role : compte.getRoles()){
                if(role.getName().equals(ERole.ROLE_ROOT.toString())){
                    authorized = true;
                }
            }

            if(!authorized){
                model.addAttribute("error","you don't have rights to perform this operation");
                System.out.println(model.getAttribute("error"));
                return "redirect:/admin/consultation/medical-record/"+code;
            }
        }

        Consultation consultation = consultationRepository.getOne(id);
        examenRepository.deleteAll(consultation.getExamens());
        prescriptionRepository.deleteAll(consultation.getPrescriptions());
        consultationRepository.deleteById(id);

        model.addAttribute("success","Operation successfully completed");
        System.out.println(model.getAttribute("success"));
        return "redirect:/admin/consultation/medical-record/"+code;
    }
}
