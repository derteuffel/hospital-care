package com.hospital.Controller;

import com.hospital.entities.*;
import com.hospital.enums.ERole;
import com.hospital.helpers.ConsultationHelper;
import com.hospital.repository.*;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import java.io.PrintStream;
import java.security.Principal;
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

    @Autowired
    private CompteService compteService;



    /** owner-developer branch code **/


    /** Get all consultations in a medical record */
    @GetMapping(value = "/medical-record/{code}")
    public String getAllConsultationsInMedicalRecord(@PathVariable String code, Model model){
        DosMedical dosMedical = dosMedicalRepository.findByCode(code);
        List<Consultation> consultations = consultationRepository.findByDosMedical(dosMedical);
        model.addAttribute("consultationList",consultations);
        model.addAttribute("code",code);
        return "dashboard/pages/admin/consultation/consultation";
    }

    /** Get all consultations in an hospital */
    @GetMapping(value = "/hospital/{id}")
    public String getAllConsultationsInHospital(@PathVariable Long id,Model model){
        Optional<Hospital> hospital =  hospitalRepository.findById(id);
        List<Consultation> consultations = consultationRepository.findByHospital(hospital);
        model.addAttribute("lists", consultations);
        model.addAttribute("hospital",hospital.get());

        return "dashboard/pages/admin/hospital/consultations";
    }

    /** Get all consultations in an hospital */
    @GetMapping(value = "/doctor/{id}")
    public String getAllConsultationsForDoctor(@PathVariable Long id,Model model){
        Personnel personnel = personnelRepository.getOne(id);
        Compte compte = compteRepository.findByPersonnel_Id(personnel.getId());
        List<Consultation> consultations = consultationRepository.findAllByPersonnel_Id(personnel.getId(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists", consultations);
        model.addAttribute("code",compte.getUsername());
        model.addAttribute("personnel",personnel);

        return "dashboard/pages/admin/personnel/consultations";
    }



    /** form for adding a consultation */
    @GetMapping(value = "/create")
    public String addConsultation(@RequestParam("code") String code, Model model,
                                  HttpServletRequest request, RedirectAttributes redirectAttributes){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dosMedicalRepository.findByCode(code);
        if (compte.getPersonnel() != null) {

                Hospital hospital = compte.getPersonnel().getHospital();
                List<Personnel> personnels = personnelRepository.findAllByHospital_Id(hospital.getId());
                List<Personnel> lists = new ArrayList<>();
                Long days = TimeUnit.DAYS.convert(new Date().getTime() - dosMedical.getBirthDate().getTime(), TimeUnit.MILLISECONDS);
                List<Compte> comptes = compteRepository.findByRolesName(ERole.ROLE_DOCTOR.toString());
                for (Compte compte1 : comptes) {
                    for (Personnel personnel : personnels) {
                        if (compte1.getPersonnel() == personnel) {
                            lists.add(personnel);
                        }
                    }
                }
                model.addAttribute("age", Math.round(days / 365));
                model.addAttribute("doctors", lists);
                model.addAttribute("patient", dosMedical);
                model.addAttribute("code", code);
                model.addAttribute("hospital",hospital);
                model.addAttribute("consultationHelper", new ConsultationHelper());
                return "dashboard/pages/admin/consultation/addConsultation";


        } else {
            redirectAttributes.addFlashAttribute("error", "Your credentials are not authorized to access there");
            return "redirect:/admin/consultation/medical-record/" + dosMedical.getCode();
        }

    }

    /** Add a consultation */
    @PostMapping(value = "/create")
    public String saveConsultation(@ModelAttribute @Valid ConsultationHelper consultationHelper,
                                   Errors errors, Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dosMedicalRepository.findByCode(consultationHelper.getCode());
        Hospital hospital = compte.getPersonnel().getHospital();
        if(errors.hasErrors()){
            List<Personnel> personnels = personnelRepository.findAllByHospital_Id(hospital.getId());
            List<Personnel> lists = new ArrayList<>();
            Long days = TimeUnit.DAYS.convert(new Date().getTime() - dosMedical.getBirthDate().getTime(),TimeUnit.MILLISECONDS);
            List<Compte> comptes = compteRepository.findByRolesName(ERole.ROLE_DOCTOR.toString());
            for (Compte compte1 : comptes){
                for (Personnel personnel : personnels){
                    if (compte1.getPersonnel() == personnel){
                        lists.add(personnel);
                    }
                }
            }
            model.addAttribute("age",Math.round(days/365));
            model.addAttribute("doctors",lists);
            model.addAttribute("patient",dosMedical);
            model.addAttribute("code",consultationHelper.getCode());
            model.addAttribute("consultationHelper", new ConsultationHelper());
            return "dashboard/pages/admin/consultation/addConsultation";
        }else {

            Personnel doctor = personnelRepository.getOne(Long.parseLong(consultationHelper.getDoctorName()));
            consultationRepository.save(consultationHelper.getConsultationInstance(hospital, dosMedical, doctor));

            model.addAttribute("success", "consultation successfully added");
            System.out.println("done");
            return "redirect:/admin/consultation/medical-record/" + consultationHelper.getCode();
        }
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

        return "dashboard/pages/admin/consultation/updateConsultation";
    }

    /** Update a consultation */
    @PostMapping(value = "/update/{idConsultation}")
    public String updateConsultation(@PathVariable Long idConsultation, @ModelAttribute @Valid ConsultationHelper consultationHelper, Errors errors, Model model){
        if(errors.hasErrors()){
            List<Compte> doctors = compteRepository.findByRolesName(ERole.ROLE_ROOT.toString());
            model.addAttribute("doctors",doctors);
            model.addAttribute("hospitalList",hospitalRepository.findAll());
            model.addAttribute("code",consultationHelper.getCode());
            return "dashboard/pages/admin/consultation/updateConsultation";
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

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid consultation id:" +id));
                consultationRepository.delete(consultation);
        model.addAttribute("hospitals", hospitalRepository.findAll());
        return "redirect:/admin/medical-record/all";

    }

}
