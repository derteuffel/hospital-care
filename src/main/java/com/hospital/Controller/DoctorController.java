package com.hospital.Controller;

import com.hospital.entities.*;
import com.hospital.enums.ERole;
import com.hospital.helpers.*;
import com.hospital.repository.*;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.concurrent.TimeUnit;

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
    private PrescriptionRepository prescriptionRepository;

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

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private BloodBankRepository bloodBankRepository;

    @Autowired
    private ExamenRepository examenRepository;


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
        Personnel personnel = personnelRepository.findByEmail(compte.getEmail());
        Hospital hospital = personnel.getHospital();
        model.addAttribute("dosMedical", dosMedical);
        return "redirect:/doctor/hospital/detail/"+hospital.getId();
    }


    /** form for adding a medical-record */
    @GetMapping(value = "/medical-record/create")
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
    @PostMapping(value = "/medical-record/create")
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
            compteDto.setPassword(dosMedicalHelper.getCode());
            compteDto.setUsername(dosMedicalHelper.getCode());
            compteService.savePatient(compteDto,"/img/default.jpeg", dosMedicalHelper.getDosMedicalInstance());
        }

        model.addAttribute("success","Operation successfully completed");
        Principal principal = request.getUserPrincipal();
        Compte loggedAccount = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",loggedAccount);

        return "redirect:/doctor/medical-record/search?search="+dosMedicalHelper.getCode();
    }

    /** form for updating a medical-record */
   /* @GetMapping(value = "/update/{code}")
    public String updateMedicalRecords(@PathVariable String code, Model model, HttpServletRequest request){
        DosMedical dosMedical = dos.findByCode(code);
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        model.addAttribute("dosMedicalHelper",DosMedicalHelper.getDosMedicalHelperInstance(dosMedical));
        return "dashboard/pages/admin/doctor/updateDosMedical";
    }

    *//** Update a medical record *//*
    @PostMapping(value = "/update/{code}")
    public String updateMedicalRecord(@PathVariable String code, @ModelAttribute @Valid DosMedicalHelper dosMedicalHelper, Errors errors, Model model)
    {
        if(errors.hasErrors()) {
            return "dashboard/pages/admin/doctor/updateDosMedical";
        }
        DosMedical exDosMedical = dos.findByCode(code);
        DosMedical newDosMedical = dosMedicalHelper.getDosMedicalInstance();
        exDosMedical.getCompte().setEmail(dosMedicalHelper.getEmail());
        newDosMedical.setId(exDosMedical.getId());
        newDosMedical.setCompte(exDosMedical.getCompte());
        dos.save(newDosMedical);

        model.addAttribute("success","Operation successfully completed");
        System.out.println(model.getAttribute("success"));
        return "redirect:/doctor/all";
    }*/

    @GetMapping("/medical-record/{id}")
    public String dosMedicalDetail(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        DosMedical dosMedical = dos.getOne(id);
        model.addAttribute("dosMedical",dosMedical);
        return "dashboard/pages/admin/doctor/dosMedical-detail";
    }

    /** search a medical record */
    @GetMapping(value = "/medical-record/search")
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

   /* @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, String password, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dosMedicalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid consultation id:" +id));
        dosMedicalRepository.delete(dosMedical);
        return "redirect:/doctor/medical-record/all";

    }*/

    @GetMapping(value = "/consultaion/create")
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
            return "dashboard/pages/admin/doctor/consultation/add";


        } else {
            redirectAttributes.addFlashAttribute("error", "Your credentials are not authorized to access there");
            return "redirect:/doctor/medical-record/" + dosMedical.getId();
        }

    }

    /** Add a consultation */
    @PostMapping(value = "/consultation/create")
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
            return "dashboard/pages/admin/doctor/consultation/add";
        }else {

            Personnel doctor = personnelRepository.getOne(Long.parseLong(consultationHelper.getDoctorName()));
            consultationRepository.save(consultationHelper.getConsultationInstance(hospital, dosMedical, doctor));

            model.addAttribute("success", "consultation successfully added");
            System.out.println("done");
            return "redirect:/doctor/medical-record/" + dosMedical.getId();
        }
    }

    /** form for updating a consultation */
    @GetMapping(value = "/consultation/update/{idConsultation}")
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

        return "dashboard/pages/admin/doctor/consultation/update";
    }

    /** Update a consultation */
    @PostMapping(value = "/consultation/update/{idConsultation}")
    public String updateConsultation(@PathVariable Long idConsultation, @ModelAttribute @Valid ConsultationHelper consultationHelper, Errors errors, Model model){
        if(errors.hasErrors()){
            List<Compte> doctors = compteRepository.findByRolesName(ERole.ROLE_ROOT.toString());
            model.addAttribute("doctors",doctors);
            model.addAttribute("hospitalList",hospitalRepository.findAll());
            model.addAttribute("code",consultationHelper.getCode());
            return "dashboard/pages/admin/doctor/consultation/update";
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
        return  "redirect:/doctor/medical-record/"+dosMedical.getId();
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
    @GetMapping(value = "/medical-record/consultation/{code}")
    public String getAllConsultationsInMedicalRecord(@PathVariable String code, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dosMedicalRepository.findByCode(code);
        List<Consultation> consultations = consultationRepository.findByDosMedical(dosMedical);
        model.addAttribute("lists",consultations);
        model.addAttribute("code",code);
        model.addAttribute("compte",compte);
        return "dashboard/pages/admin/doctor/consultation/lists";
    }


    /** Get all consultations in an hospital */
    @GetMapping(value = "/consultation/lists/{id}")
    public String getAllConsultationsForDoctor(@PathVariable Long id,Model model){
        Personnel personnel = personnelRepository.getOne(id);
        Compte compte = compteRepository.findByPersonnel_Id(personnel.getId());
        List<Consultation> consultations = consultationRepository.findAllByPersonnel_Id(personnel.getId(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists", consultations);
        model.addAttribute("code",compte.getUsername());
        model.addAttribute("personnel",personnel);

        return "dashboard/pages/admin/doctor/consultations";
    }
    @GetMapping("/consultation/delete/{id}")
    public String deleteConsultationByDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes,String password, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        if (passwordEncoder.matches(password,compte.getPassword())){
            Consultation consultation = consultationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid consultation id:" +id));
            consultationRepository.delete(consultation);
            redirectAttributes.addFlashAttribute("success","Your consultation has been deleted successfuly");
        }else {
            redirectAttributes.addFlashAttribute("error", "You don't have permission for this operation");
        }

        return "redirect:/doctor/consultation/lists/"+compte.getPersonnel().getId();

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
        return "dashboard/pages/admin/doctor/examens/lists";

    }
    @GetMapping("/hospital/examen/lists/{id}")
    public String examensInHospital(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Hospital hospital = hospitalRepository.getOne(id);
        List<Examen> examens = examenRepository.findByHospital(hospital);

        model.addAttribute("lists",examens);
        return "dashboard/pages/admin/doctor/examens/lists";

    }

    @GetMapping("/examen/lists")
    public String examensByDoctor(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Personnel personnel = compte.getPersonnel();
        model.addAttribute("compte",compte);
        List<Consultation> consultations = consultationRepository.findAllByPersonnel_Id(personnel.getId(),Sort.by(Sort.Direction.DESC,"id"));
        Hospital hospital = hospitalRepository.getOne(id);
        List<Examen> examens = new ArrayList<>();

        for (Consultation consultation : consultations){
            examens.addAll(consultation.getExamens());
        }

        model.addAttribute("lists",examens);
        model.addAttribute("hospital",hospital);
        return "dashboard/pages/admin/doctor/examens/lists";

    }

    @GetMapping(value = "/examen/create")
    public String addExam(@RequestParam("idConsultation") int  idConsultation, Model model, HttpServletRequest request,RedirectAttributes redirectAttributes){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Consultation consultation = consultationRepository.getOne(Long.parseLong(""+idConsultation));
        if (compte.getPersonnel() != null) {
            List<Hospital> hospitals = hospitalRepository.findAll();
            model.addAttribute("idConsultation", idConsultation);
            model.addAttribute("lists", hospitals);
            model.addAttribute("hospital",compte.getPersonnel().getHospital());
            model.addAttribute(new ExamenHelper());
            return "dashboard/pages/admin/doctor/examens/addExam";
        }else {
            redirectAttributes.addFlashAttribute("error","You don't have access for this action");
            return "redirect:/doctor/consultation/detail/" + consultation.getId();
        }
    }

    /** Add an exam */
    @PostMapping("/examen/create")
    public String saveExam(@ModelAttribute @Valid ExamenHelper examenHelper,Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute("lists",hospitalRepository.findAll());
            model.addAttribute("idConsultation",examenHelper.getIdConsultation());
            return "dashboard/pages/admin/doctor/examens/addExam";
        }else{
            Consultation consultation = consultationRepository.getOne(examenHelper.getIdConsultation());
            Hospital hospital = hospitalRepository.findByName(examenHelper.getHospitalName());
            examenRepository.save(examenHelper.getExamInstance(hospital,consultation));
        }
        return  "redirect:/doctor/consultation/detail/"+examenHelper.getIdConsultation();
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
        return "dashboard/pages/admin/doctor/prescriptions/lists";

    }

    /** form for adding an prescription */
    @GetMapping(value = "/prescription/create")
    public String addOrdonances(@RequestParam("idConsultation") int  idConsultation, Model model,
                                HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        model.addAttribute("idConsultation",idConsultation);
        model.addAttribute(new PrescriptionHelper());
        return "dashboard/pages/admin/doctor/prescription/addPrescription";
    }

    /** Get all prescriptions of a consultation */
    @GetMapping(value = "/consultation/prescription/lists/{id}")
    public String getAllPrescriptionsOfAConsultation(@PathVariable Long id, Model model, HttpServletRequest request){
        Consultation consultation =  consultationRepository.getOne(id);
        List<Prescription> prescriptions = prescriptionRepository.findByConsultation(consultation);
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        model.addAttribute("lists",prescriptions);
        model.addAttribute("consultation", consultation);
        model.addAttribute("idConsultation",id);
        return "dashboard/pages/admin/doctor/prescription/lists";
    }

    /** Add an prescription */
    @PostMapping("/prescription/create")
    public String savePrescription(@ModelAttribute @Valid PrescriptionHelper prescriptionHelper,
                                   Errors errors, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        if(errors.hasErrors()){
            model.addAttribute("idConsultation",prescriptionHelper.getIdConsultation());
            return "dashboard/pages/admin/doctor/prescription/addPrescription";
        }else{
            Consultation consultation = consultationRepository.getOne(prescriptionHelper.getIdConsultation());
            prescriptionRepository.save(prescriptionHelper.getPrescriptionInstance(consultation));
        }
        return  "redirect:/doctor/consultation/prescription/lists/"+prescriptionHelper.getIdConsultation();
    }

    @GetMapping(value = "/update/{idPrescription}")
    public String updatePrescription(@PathVariable Long idPrescription, @RequestParam("idConsultation") Long idConsultation, Model model){
        model.addAttribute("idConsultation",idConsultation);
        model.addAttribute("prescriptionHelper", PrescriptionHelper.getPrescriptionHelperInstance(prescriptionRepository.getOne(idPrescription)));
        return "dashboard/pages/admin/doctor/prescription/updatePrescription";
    }

    /** Update a prescription */
    @PostMapping(value = "/update/{idPrescription}")
    public String updatePrescription(@PathVariable Long idPrescription, @ModelAttribute @Valid PrescriptionHelper prescriptionHelper, Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute("idConsultation",prescriptionHelper.getIdConsultation());
            return "dashboard/pages/admin/doctor/prescription/updatePrescription";
        }else{
            Consultation consultation = consultationRepository.getOne(prescriptionHelper.getIdConsultation());
            Prescription exPrescription = prescriptionRepository.getOne(idPrescription);
            Prescription newPrescription = prescriptionHelper.getPrescriptionInstance(consultation);
            newPrescription.setId(exPrescription.getId());
            prescriptionRepository.save(newPrescription);
        }

        model.addAttribute("success","Operation successfully completed");
        System.out.println(model.getAttribute("success"));
        return  "redirect:/doctor/consultation/prescription/lists/"+prescriptionHelper.getIdConsultation();
    }

    @GetMapping("/prescription/delete/{id}")
    public String deletePrescriptionById(@PathVariable Long id, Model model) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid prescription id:" +id));
        Consultation consultation = prescription.getConsultation();
        prescriptionRepository.delete(prescription);
        return "redirect:/doctor/consultation/prescription/lists/"+consultation.getId();

    }

    @GetMapping("/rdv/lists")
    public String appointments(HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        List<Rdv> rdvs = rdvRepository.findAllByComptes_Id(compte.getId(), Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",rdvs);
        return "dashboard/pages/admin/doctor/rdv/lists";
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


   /* @GetMapping(value = "/hospital/lists")
    public ModelAndView getHospitals(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/doctor/hospital/lists");

        List<Hospital> hospitals = hospitalRepository.findAll();
        modelAndView.addObject("lists",hospitals);
        modelAndView.addObject("compte", compte);

        return modelAndView;
    }*/




    @GetMapping("/armoire/detail/{id}")
    public String armoire(HttpServletRequest request, Model model, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dos.getOne(id);
        Compte compte1 = dosMedical.getCompte();
        List<Armoire> armoires = armoireRepository.findAllByCompte_Id(compte1.getId(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("compte", compte);
        model.addAttribute("compte1", compte1);
        model.addAttribute("lists",armoires);
        return "dashboard/pages/admin/doctor/armoire/lists";
    }

    @GetMapping("/hospital/blood-bank/lists/{id}")
    public String bloodBank(HttpServletRequest request, Model model, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);

        List<BloodBank> bloodBanks = bloodBankRepository.findAllByHospital(hospital);
        model.addAttribute("compte", compte);
        model.addAttribute("hosptial", hospital);
        model.addAttribute("lists",bloodBanks);
        return "dashboard/pages/admin/doctor/blood-bank/lists";
    }
    @GetMapping("/hospital/incubator/lists/{id}")
    public String incubator(HttpServletRequest request, Model model, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);

        List<Incubator> incubators = incubatorRepository.findAllByHospital(hospital);
        model.addAttribute("compte", compte);
        model.addAttribute("hosptial", hospital);
        model.addAttribute("lists",incubators);
        return "dashboard/pages/admin/doctor/incubator/lists";
    }


}
