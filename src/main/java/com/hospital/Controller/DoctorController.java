package com.hospital.Controller;

import com.hospital.entities.*;
import com.hospital.enums.ERole;
import com.hospital.helpers.CompteRegistrationDto;
import com.hospital.helpers.DosMedicalHelper;
import com.hospital.repository.*;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private IncubatorRepository incubatorRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private DosMedicalRepository dos;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private CompteService compteService;
    @Autowired
    private CompteRepository compteRepository;
    @Autowired
    private ArmoireRepository armoireRepository;

    @Autowired
    private RdvRepository rdvRepository;
    @Autowired
    private DosMedicalRepository dosMedicalRepository;

    @GetMapping("/login")
    public String login(Model model){
        return "dashboard/pages/admin/doctor/login";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte", compte);
        DosMedical dosMedical = dos.findByCode(compte.getUsername());
        model.addAttribute("dosMedical", dosMedical);
        return "dashboard/pages/admin/doctor/home";
    }

    /** Retrieve all medical records */
    @GetMapping(value = "/medical-record/all")
    public String getAllMedicalRecords(Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());

        if(compte.checkRole(ERole.ROLE_DOCTOR)) model.addAttribute("compte",compte);
        model.addAttribute("dosMedicalList",dos.findAll());
        return "dashboard/pages/admin/doctor/dosMedical";
    }

    /** form for adding a medical-record */
    @GetMapping(value = "/create")
    public String addMedicalRecords(HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        String code = request.getParameter("code");
        DosMedicalHelper dosMedicalHelper = new DosMedicalHelper();
        if(code != null) dosMedicalHelper.setCode(code);
        model.addAttribute("compte",compte);
        model.addAttribute("dosMedicalHelper",dosMedicalHelper);
        return "dashboard/pages/admin/doctor/addDosMedical";
    }

    /** Add a medical record */
    @PostMapping(value = "/create")
    public String addMedicalRecord(@ModelAttribute @Valid DosMedicalHelper dosMedicalHelper,
                                   Errors errors, Model model, HttpServletRequest request){

        if(errors.hasErrors()) {
            return "dashboard/pages/admin/patient/addDosMedical";
        }
        Compte compte = compteRepository.findByEmail(dosMedicalHelper.getEmail());
        DosMedical dosMedical = dos.findByCode(dosMedicalHelper.getCode());

        if(compte != null){
            model.addAttribute("error","There is an existing account with the provided email");
            return "dashboard/pages/admin/doctor/addDosMedical";
        }else if (dosMedical != null){
            model.addAttribute("error","There is an existing medical record with the provided code");
            return "dashboard/pages/admin/doctor/addDosMedical";
        }else {
            CompteRegistrationDto compteDto = new CompteRegistrationDto();
            compteDto.setEmail(dosMedicalHelper.getEmail());
            compteDto.setPassword("1234567890");
            compteDto.setUsername(dosMedicalHelper.getCode());
            compteService.savePatient(compteDto,"/img/default.jpeg", dosMedicalHelper.getDosMedicalInstance());
        }

        model.addAttribute("success","Operation successfully completed");
        Principal principal = request.getUserPrincipal();
        Compte loggedAccount = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",loggedAccount);

        if(loggedAccount.checkRole(ERole.ROLE_ADMIN)) return "redirect:/doctor/medical-record/all";
        else return "redirect:/doctor/medical-record/search?search="+dosMedicalHelper.getCode();
    }


    @GetMapping("/medical-record/{id}")
    public String dosMedicalDetail(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        DosMedical dosMedical = dos.getOne(id);
        model.addAttribute("dosMedical",dosMedical);
        return "dashboard/pages/admin/doctor/dosMedical";
    }

    /** search a medical record */
    @GetMapping(value = "/search")
    public String getMedicalRecord(@RequestParam("search") String search, Model model, RedirectAttributes redirectAttributes){
        if(search != ""){
            DosMedical dosMedical = dos.findByCode(search);
            if(dosMedical != null) {

                model.addAttribute("dosMedicalFound", dosMedical);
            }else {
                redirectAttributes.addFlashAttribute("error","There are no account and medical records with provided code. Please add new medical record");
                return "redirect:/doctor/medical-record/create";
            }
        }

        return "dashboard/pages/admin/doctor/dosMedical";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model) {
        DosMedical dosMedical = dosMedicalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid consultation id:" +id));
        dosMedicalRepository.delete(dosMedical);
        return "redirect:/doctor/medical-record/all";

    }

    @GetMapping("/consultation/lists/{id}")
    public String consultations(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        DosMedical dosMedical = dos.getOne(id);
        List<Consultation> consultations = consultationRepository.findByDosMedical(dosMedical);
        model.addAttribute("dosMedical",dosMedical);
        model.addAttribute("lists",consultations);
        return "dashboard/pages/admin/doctor/consultations/lists";

    }

    @GetMapping("/consultation/detail/{id}")
    public String consultation(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Consultation consultation = consultationRepository.getOne(id);
        DosMedical dosMedical = consultation.getDosMedical();
        model.addAttribute("dosMedical",dosMedical);
        model.addAttribute("consultation",consultation);
        return "dashboard/pages/admin/doctor/consultations/detail";

    }

    /** Get all consultations in a medical record */
    @GetMapping(value = "/medical-record/{code}")
    public String getAllConsultationsInMedicalRecord(@PathVariable String code, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dosMedicalRepository.findByCode(code);
        List<Consultation> consultations = consultationRepository.findByDosMedical(dosMedical);
        model.addAttribute("consultationList",consultations);
        model.addAttribute("code",code);
        model.addAttribute("compte",compte);
        return "dashboard/pages/admin/consultation/consultation";
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

        return "dashboard/pages/admin/doctor/consultations";
    }
    @GetMapping("/delete/{id}")
    public String deleteConsultationByDoctor(@PathVariable Long id, Model model) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid consultation id:" +id));
        consultationRepository.delete(consultation);
        model.addAttribute("hospitals", hospitalRepository.findAll());
        return "redirect:/doctor/medical-record/all";

    }

    @GetMapping("/examen/lists/{id}")
    public String examens(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        DosMedical dosMedical = dos.getOne(id);
        List<Examen> examens = new ArrayList<>();
        List<Consultation> consultations = consultationRepository.findByDosMedical(dosMedical);
        for (Consultation consultation : consultations){
            examens.addAll(consultation.getExamens());
        }
        model.addAttribute("lists",examens);
        model.addAttribute("dosMedical",dosMedical);
        return "dashboard/pages/admin/docotor/examens/lists";

    }

    @GetMapping("/ordonances/lists/{id}")
    public String ordonances(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        DosMedical dosMedical = dos.getOne(id);
        List<Prescription> prescriptions = new ArrayList<>();
        List<Consultation> consultations = consultationRepository.findByDosMedical(dosMedical);
        for (Consultation consultation : consultations){
            prescriptions.addAll(consultation.getPrescriptions());
        }
        model.addAttribute("lists",prescriptions);
        model.addAttribute("dosMedical",dosMedical);
        return "dashboard/pages/admin/docotor/prescriptions/lists";

    }

    @GetMapping("/rdv/lists")
    public String appointments(HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        List<Rdv> rdvs = rdvRepository.findAllByComptes_Id(compte.getId(), Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",rdvs);
        return "dashboard/pages/admin/rdv/lists";
    }

    @GetMapping("/hospital/detail/{id}")
    public String getHospital(@PathVariable Long id, Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hos = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hos);
        model.addAttribute("compte", compte);
        model.addAttribute("lists", personnelRepository.findAllByFunctionAndHospital_Id("DOCTOR",hos.getId()));
        return "dashboard/pages/admin/doctor/hospital/detail";
    }


    @GetMapping(value = "/hospital/lists")
    public ModelAndView getHospitals(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/doctor/hospital/lists");

        List<Hospital> hospitals = hospitalRepository.findAll();
        modelAndView.addObject("lists",hospitals);
        modelAndView.addObject("compte", compte);

        return modelAndView;
    }


    @GetMapping("/rdv/add/{id}")
    public ModelAndView showform(HttpServletRequest request, @PathVariable Long id){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/doctor/appointment/add-rdv");
        Rdv rdv = new Rdv();
        Hospital hospital = hospitalRepository.getOne(id);
        modelAndView.addObject("hospital",hospital);
        modelAndView.addObject("rdv", rdv);
        modelAndView.addObject("compte",compte);
//        modelAndView.addObject("appointments", rdvRepository.findAll());
        return modelAndView;
    }

    @PostMapping("/rdv/add/{id}")
    public String storeRdv(@ModelAttribute @Valid Rdv rdv, Errors errors, RedirectAttributes redirAttrs, HttpServletRequest request, Model model, @PathVariable Long id){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        if(errors.hasErrors()){
            return "dashboard/pages/admin/doctor/appointment/add-rdv";
        }

        Hospital hospital = hospitalRepository.getOne(id);

        SimpleDateFormat heure = new SimpleDateFormat("hh:mm");

        rdv.setComptes(Arrays.asList(compte));
        rdv.setStatus(false);
        rdv.setDate(rdv.getDate());
        rdv.setHeure(heure.format(rdv.getHeure()));
        rdv.setPatient(compte.getName());
        rdv.setHospital(hospital);
        rdvRepository.save(rdv);
        redirAttrs.addFlashAttribute("message", "Rdv added Successfully");
        return "redirect:/doctor/rdv/lists";
    }

    @GetMapping("/armoire/lists")
    public String armoire(HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        List<Armoire> armoires = armoireRepository.findAllByCompte_Id(compte.getId(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("compte", compte);
        model.addAttribute("lists",armoires);
        return "dashboard/pages/admin/doctor/armoire/lists";
    }


}
