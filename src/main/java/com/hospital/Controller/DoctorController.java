package com.hospital.Controller;

import com.hospital.entities.*;
import com.hospital.enums.ERole;
import com.hospital.helpers.*;
import com.hospital.repository.*;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.ParseException;
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

    @Value("${file.upload-dir}")
    private  String fileStorage;


    @GetMapping("/login")
    public String login(Model model){
        return "dashboard/pages/admin/doctor/login";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte", compte);
        request.getSession().setAttribute("compte",compte);
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

    /** Retrieve all medical records */
    @GetMapping(value = "/medical-record/lists")
    public String getAllMedicalRecords(Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        model.addAttribute("dosMedicalList",dos.findAll());
        return "dashboard/pages/admin/doctor/dosMedical";
    }

    /** Add a medical record */
    @PostMapping(value = "/medical-record/create")
    public String addMedicalRecord(@ModelAttribute @Valid DosMedicalHelper dosMedicalHelper,
                                   Errors errors, Model model, HttpServletRequest request){

        if(errors.hasErrors()) {
            return "dashboard/pages/admin/doctor/addDosMedical";
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
    public String getMedicalRecord(@RequestParam("search") String search, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        if(search != ""){
            DosMedical dosMedical = dos.findByCode(search);
            if(dosMedical != null) {

                model.addAttribute("compte",compte);
                model.addAttribute("dosMedicalFound", dosMedical);
            }else {
                redirectAttributes.addFlashAttribute("error","There are no account and medical records with provided code. Please add new medical record");
                return "redirect:/doctor/medical-record/create";
            }
        }

        return "dashboard/pages/admin/doctor/dosMedical";
    }



    @GetMapping(value = "/consultation/create")
    public String addConsultation(@RequestParam("code") String code, Model model,
                                  HttpServletRequest request, RedirectAttributes redirectAttributes){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dosMedicalRepository.findByCode(code);
        if (compte.getPersonnel() != null) {

            Hospital hospital = compte.getPersonnel().getHospital();
            Long days = TimeUnit.DAYS.convert(new Date().getTime() - dosMedical.getBirthDate().getTime(), TimeUnit.MILLISECONDS);

            model.addAttribute("age", Math.round(days / 365));
            model.addAttribute("patient", dosMedical);
            model.addAttribute("code", dosMedical.getCode());
            model.addAttribute("compte",compte);
            model.addAttribute("hospital",hospital);
            model.addAttribute("consultationHelper", new ConsultationHelper());
            return "dashboard/pages/admin/doctor/consultation/form";


        } else {
            redirectAttributes.addFlashAttribute("error", "Your credentials are not authorized to access there");
            return "redirect:/doctor/medical-record/consultation/lists/" + dosMedical.getCode();
        }

    }

    /** Add a consultation */
    @PostMapping("/consultation/save")
    public String saveConsultation( @Valid ConsultationHelper consultationHelper,
                                   Errors errors, Model model, HttpServletRequest request,
                                   RedirectAttributes redirectAttributes, String code){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dosMedicalRepository.findByCode(code);
        Hospital hospital = compte.getPersonnel().getHospital();
        consultationHelper.setCode(code);
        Personnel doctor = compte.getPersonnel();
        consultationHelper.setDoctorName(doctor.getLastName()+" "+doctor.getFirstName());
        consultationHelper.setDoctorPhone(doctor.getPhone());
        consultationHelper.setHospitalName(hospital.getName());

        if(errors.hasErrors()){
            System.out.println(errors.getAllErrors());
            redirectAttributes.addFlashAttribute("error","There are an error in your form");
            return "redirect:/doctor/consultation/create?code="+code;
        }else {
            consultationRepository.save(consultationHelper.getConsultationInstance(hospital, dosMedical, doctor));

            redirectAttributes.addFlashAttribute("success", "consultation successfully added");
            System.out.println("done");
            return "redirect:/doctor/medical-record/consultation/lists/" + dosMedical.getCode();
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
        return "dashboard/pages/admin/doctor/consultation/detail";

    }

    @GetMapping("/hospital/consultation/detail/{id}")
    public String consultationDetails(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Consultation consultation = consultationRepository.getOne(id);
        DosMedical dosMedical = consultation.getDosMedical();
        model.addAttribute("dosMedical",dosMedical);
        model.addAttribute("consultation",consultation);
        return "dashboard/pages/admin/doctor/hospital/consultation/detail";

    }

    /** Get all consultations in a medical record */
    @GetMapping(value = "/medical-record/consultation/lists/{code}")
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
    @GetMapping(value = "/consultation/lists")
    public String getAllConsultationsForDoctor(Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Personnel personnel = compte.getPersonnel();
        List<Consultation> consultations = consultationRepository.findAllByPersonnel_Id(personnel.getId(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists", consultations);
        model.addAttribute("code",compte.getUsername());
        model.addAttribute("compte",compte);
        model.addAttribute("personnel",personnel);

        return "dashboard/pages/admin/doctor/hospital/consultation/lists";
    }

    /** Get all consultations in an hospital */
    @GetMapping(value = "/consultation/lists/{id}")
    public String getAllConsultationsByDoctor(Model model, HttpServletRequest request, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Personnel personnel = personnelRepository.getOne(id);
        List<Consultation> consultations = consultationRepository.findAllByPersonnel_Id(personnel.getId(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists", consultations);
        model.addAttribute("code",compte.getUsername());
        model.addAttribute("personnel",personnel);
        model.addAttribute("compte",compte);

        return "dashboard/pages/admin/doctor/hospital/consultation/lists";
    }

    /** Get all consultations in an hospital */
    @GetMapping(value = "/hospital/consultation/lists/{id}")
    public String getAllConsultationsForHospital(Model model, HttpServletRequest request, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Personnel personnel = compte.getPersonnel();
        Hospital hospital = hospitalRepository.getOne(id);
        Collection<Consultation> consultations = hospital.getConsultations();
        model.addAttribute("lists", consultations);
        model.addAttribute("code",compte.getUsername());
        model.addAttribute("personnel",personnel);
        model.addAttribute("compte",compte);

        return "dashboard/pages/admin/doctor/hospital/consultation/lists";
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

    @GetMapping("/hospital/consultation/examen/lists/{id}")
    public String examensByConsultation(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Consultation consultation = consultationRepository.getOne(id);
        Collection<Examen> examens = consultation.getExamens();

        model.addAttribute("lists",examens);
        model.addAttribute("consultation",consultation);
        return "dashboard/pages/admin/doctor/hospital/examens/lists";

    }

    @GetMapping("/consultation/examen/lists/{id}")
    public String examens(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Consultation consultation = consultationRepository.getOne(id);
        Collection<Examen> examens = consultation.getExamens();

        model.addAttribute("lists",examens);
        model.addAttribute("consultation",consultation);
        return "dashboard/pages/admin/doctor/examens/lists";

    }

    @GetMapping("/examen/detail/{id}")
    public String examensDetail(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Personnel personnel = compte.getPersonnel();
        model.addAttribute("compte",compte);
        Examen examen = examenRepository.getOne(id);
        Hospital hospital = examen.getHospital();
        model.addAttribute("hospital",hospital);
        model.addAttribute("examen", examen);
        return "dashboard/pages/admin/doctor/examens/detail";

    }

    @GetMapping("/hospital/examen/detail/{id}")
    public String examensDetail1(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Personnel personnel = compte.getPersonnel();
        model.addAttribute("compte",compte);
        Examen examen = examenRepository.getOne(id);
        Hospital hospital = examen.getHospital();
        model.addAttribute("hospital",hospital);
        model.addAttribute("examen", examen);
        return "dashboard/pages/admin/doctor/hospital/examens/detail";

    }

    @GetMapping("/hospital/examen/lists/{id}")
    public String examensInHospital(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Hospital hospital = hospitalRepository.getOne(id);
        List<Examen> examens = examenRepository.findByHospital(hospital);

        model.addAttribute("lists",examens);
        return "dashboard/pages/admin/doctor/hospital/examens/lists";

    }

    @GetMapping("/examen/lists/{id}")
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
        return "dashboard/pages/admin/doctor/hospital/examens/lists";

    }

    @GetMapping(value = "/consultation/examen/create")
    public String addExam(@RequestParam("idConsultation") int  idConsultation, Model model, HttpServletRequest request,RedirectAttributes redirectAttributes){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Consultation consultation = consultationRepository.getOne(Long.parseLong(""+idConsultation));
        if (compte.getPersonnel() != null) {
            model.addAttribute("idConsultation", idConsultation);
            model.addAttribute("compte",compte);
            model.addAttribute("hospital",compte.getPersonnel().getHospital());
            model.addAttribute(new ExamenHelper());
            return "dashboard/pages/admin/doctor/examens/form";
        }else {
            redirectAttributes.addFlashAttribute("error","You don't have access for this action");
            return "redirect:/doctor/consultation/examen/lists/" + consultation.getId();
        }
    }

    /** Add an exam */
    @PostMapping("/consultation/examen/create")
    public String saveExam(@ModelAttribute @Valid ExamenHelper examenHelper,Errors errors, Model model,
                           int idConsultation, HttpServletRequest request, RedirectAttributes redirectAttributes){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        if(errors.hasErrors()){
            model.addAttribute("compte",compte);
            model.addAttribute("idConsultation",idConsultation);
            model.addAttribute("hospital",compte.getPersonnel().getHospital());
            model.addAttribute(new ExamenHelper());
            return "dashboard/pages/admin/doctor/examens/form";
        }else{
            Consultation consultation = consultationRepository.getOne(Long.parseLong(idConsultation+""));
            Hospital hospital = compte.getPersonnel().getHospital();
            System.out.println(hospital.getName());
            examenHelper.setHospitalName(hospital.getName());
            examenHelper.setStatus(false);
            examenRepository.save(examenHelper.getExamInstance(hospital,consultation));
        }
        return  "redirect:/doctor/consultation/examen/lists/"+idConsultation;
    }

    @GetMapping("/hospital/consultation/ordonances/lists/{id}")
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
        return "dashboard/pages/admin/doctor/hospital/prescription/lists";

    }

    /** form for adding an prescription */
    @GetMapping(value = "/consultation/prescription/create")
    public String addOrdonances(@RequestParam("idConsultation") int  idConsultation, Model model,
                                HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        model.addAttribute("idConsultation",idConsultation);
        model.addAttribute(new PrescriptionHelper());
        return "dashboard/pages/admin/doctor/prescription/form";
    }

    /** Get all prescriptions of a consultation */
    @GetMapping(value = "/hospital/consultation/prescription/lists/{id}")
    public String getAllPrescriptionOfAConsultation(@PathVariable Long id, Model model, HttpServletRequest request){
        Consultation consultation =  consultationRepository.getOne(id);
        List<Prescription> prescriptions = prescriptionRepository.findByConsultation(consultation);
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        model.addAttribute("lists",prescriptions);
        model.addAttribute("consultation", consultation);
        model.addAttribute("idConsultation",id);
        return "dashboard/pages/admin/doctor/hospital/prescription/lists";
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
    @PostMapping("/consultation/prescription/save")
    public String savePrescription( @Valid PrescriptionHelper prescriptionHelper,
                                   Errors errors, Model model, HttpServletRequest request, int idConsultation){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        System.out.println("je suis "+idConsultation);
        Consultation consultation = consultationRepository.getOne(Long.parseLong(idConsultation+""));
        System.out.println(consultation.getObservations());
        model.addAttribute("compte",compte);
        if(errors.hasErrors()){
            System.out.println(errors.getAllErrors());
            model.addAttribute("idConsultation",consultation.getId());
            model.addAttribute(new PrescriptionHelper());
            return "dashboard/pages/admin/doctor/prescription/form";
        }else{
            prescriptionHelper.setDate(new Date());
            prescriptionRepository.save(prescriptionHelper.getPrescriptionInstance(consultation));
        }
        return  "redirect:/doctor/consultation/prescription/lists/"+prescriptionHelper.getIdConsultation();
    }

    @GetMapping(value = "/consultation/prescription/update/{id}")
    public String updatePrescription(@PathVariable Long id, @RequestParam("idConsultation") Long idConsultation, Model model,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("idConsultation",idConsultation);
        model.addAttribute("compte",compte);
        model.addAttribute("prescription", prescriptionRepository.getOne(id));
        return "dashboard/pages/admin/doctor/prescription/update";
    }

    /** Update a prescription */
    @PostMapping(value = "/consultation/prescription/update/{id}")
    public String updatePrescription(@PathVariable Long id, @Valid Prescription prescription, Model model, Long idConsultation){

            Consultation consultation = consultationRepository.getOne(idConsultation);
            prescription.setConsultation(consultation);
            prescription.setDate(new Date());
            prescriptionRepository.save(prescription);

        model.addAttribute("success","Operation successfully completed");
        System.out.println(model.getAttribute("success"));
        return  "redirect:/doctor/consultation/prescription/lists/"+consultation.getId();
    }

    @GetMapping("/consultation/prescription/delete/{id}")
    public String deletePrescriptionById(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid prescription id:" +id));
        Consultation consultation = prescription.getConsultation();
        prescriptionRepository.delete(prescription);
        redirectAttributes.addFlashAttribute("success","Your delete action has been successfuly");
        return "redirect:/doctor/consultation/prescription/lists/"+consultation.getId();

    }

    @GetMapping("/appointment/lists")
    public String appointments(HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        List<Rdv> rdvs = rdvRepository.findAllByComptes_Id(compte.getId(), Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",rdvs);
        return "dashboard/pages/admin/doctor/appointment/lists";
    }

    @GetMapping("/appointment/lists/{id}")
    public String appointmentsByDoctor(HttpServletRequest request, Model model, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Compte compte1 = compteRepository.getOne(id);
        List<Rdv> rdvs = rdvRepository.findAllByComptes_Id(compte1.getId(), Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",rdvs);
        return "dashboard/pages/admin/doctor/appointment/lists";
    }

    @GetMapping("/hospital/appointment/lists/{id}")
    public String appointmentsByHospital(HttpServletRequest request, Model model, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Hospital hospital = hospitalRepository.getOne(id);
        List<Rdv> rdvs = rdvRepository.findAllByHospital_Id(hospital.getId(), Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",rdvs);
        model.addAttribute("hospital",hospital);
        return "dashboard/pages/admin/doctor/appointment/lists";
    }

    @GetMapping("/appointment/active/{id}")
    public String active(@PathVariable Long id, HttpSession session){
        Rdv rdv = rdvRepository.getOne(id);
        if (rdv.getStatus() != null) {
            if (rdv.getStatus() == true) {
                rdv.setStatus(false);
            } else {
                rdv.setStatus(true);
            }
            rdvRepository.save(rdv);
        }else {
            rdv.setStatus(false);
            rdvRepository.save(rdv);
            return "redirect:/doctor/appointment/lists";
        }


        return "redirect:/doctor/appointment/lists" ;
    }


    @GetMapping("/hospital/detail/{id}")
    public String getHospital(@PathVariable Long id, Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hos = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hos);
        model.addAttribute("compte", compte);
        model.addAttribute("doctors", personnelRepository.findAllByFunctionAndHospital_Id("DOCTOR",hos.getId()));
        model.addAttribute("simples", personnelRepository.findAllByFunctionAndHospital_Id("SIMPLE",hos.getId()));
        return "dashboard/pages/admin/doctor/hospital/detail";
    }





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

    @GetMapping("/consultation/examen/observation/{id}")
    public String addObservationToExamen(@PathVariable Long id, String observation){
        Examen examen = examenRepository.getOne(id);
        examen.setDescription(observation);
        examenRepository.save(examen);
        return "redirect:/doctor/examen/detail/"+examen.getId();
    }

    @GetMapping(value = "/hospital/doctor/lists/{id}")
    public String getHospitalDoctors(@PathVariable Long id, Model model,HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hos = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hos);
        model.addAttribute("compte",compte);
        model.addAttribute("lists", personnelRepository.findAllByFunctionAndHospital_Id("DOCTOR",hos.getId()));
        return "dashboard/pages/admin/doctor/hospital/doctor/lists";
    }

    @GetMapping(value = "/hospital/other/lists/{id}")
    public String getHospitalOthers(@PathVariable Long id, Model model,HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hos = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hos);
        model.addAttribute("compte",compte);
        model.addAttribute("lists", personnelRepository.findAllByFunctionAndHospital_Id("SIMPLE",hos.getId()));
        return "dashboard/pages/admin/doctor/hospital/other/all";
    }

    @GetMapping(value = "/hospital/doctor/detail/{id}")
    public String getHospitalDoctor(@PathVariable Long id, Model model,HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Personnel personnel = personnelRepository.getOne(id);
        Compte personnelAccount = compteRepository.findByPersonnel_Id(personnel.getId());
        List<Rdv> appointments = rdvRepository.findAllByComptes_Id(personnelAccount.getId(), Sort.by(Sort.Direction.DESC,"id"));
        Hospital hos = personnel.getHospital();
        model.addAttribute("hospital", hos);
        model.addAttribute("compte",compte);
        model.addAttribute("personnel", personnel);
        model.addAttribute("appointments",appointments);
        model.addAttribute("personnelAccount",personnelAccount);
        return "dashboard/pages/admin/doctor/hospital/doctor/detail";
    }

    @GetMapping(value = "/hospital/other/detail/{id}")
    public String getHospitalOther(@PathVariable Long id, Model model,HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Personnel personnel = personnelRepository.getOne(id);
        Compte personnelAccount = compteRepository.findByPersonnel_Id(personnel.getId());
        List<Rdv> appointments = rdvRepository.findAllByComptes_Id(personnelAccount.getId(), Sort.by(Sort.Direction.DESC,"id"));
        Hospital hos = personnel.getHospital();
        model.addAttribute("hospital", hos);
        model.addAttribute("compte",compte);
        model.addAttribute("personnel", personnel);
        model.addAttribute("appointments",appointments);
        model.addAttribute("personnelAccount",personnelAccount);
        return "dashboard/pages/admin/doctor/hospital/other/detail";
    }

    @GetMapping("/hospital/doctor/add/{id}")
    public String addDoctor(@PathVariable Long id,Model model,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("compte",compte);
        model.addAttribute("form",new PersonnelHelper());
        return "dashboard/pages/admin/doctor/hospital/doctor/form";
    }

    @PostMapping("/hospital/doctor/add/{id}")
    public String saveDoctor(@Valid PersonnelHelper form, Model model,
                             BindingResult bindingResult,HttpServletRequest request,
                             @RequestParam("file") MultipartFile file, @PathVariable Long id, RedirectAttributes redirectAttributes) throws ParseException {
        Principal principal = request.getUserPrincipal();
        Compte compte1 = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        Personnel persExists = personnelRepository.findByEmailOrPhone(form.getEmail(), form.getPhone());
        Compte compte = compteRepository.findByEmail(form.getEmail());
        Personnel personnel = new Personnel();
        DosMedical dosMedical = new DosMedical();

        if (persExists != null){
            bindingResult
                    .rejectValue("email", "error.personnel",
                            "there is already a personnel registered with that email or name or telephone provided ");
        }else if (compte != null){
            bindingResult
                    .rejectValue("email","error.compte","There are existing account with the provided email");
        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("compte",compte1);
            model.addAttribute("hospital",hospital);
            model.addAttribute("form",new PersonnelHelper());
            return  "dashboard/pages/admin/doctor/hospital/doctor/form";
        }else {
            if (!(file.isEmpty())){
                try {
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(fileStorage + file.getOriginalFilename());
                    Files.write(path, bytes);
                }catch (IOException e){
                    e.printStackTrace();
                }
                personnel.setAvatar("/downloadFile/"+file.getOriginalFilename());
            }else {
                personnel.setAvatar("/img/default.jpeg");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy",Locale.ENGLISH);
            String now = sdf.format(new Date());
            String secondDate = sdf.format(form.getBirthDate());
            Integer now1 = Integer.parseInt(now);
            Integer secondDate1 = Integer.parseInt(secondDate);
            personnel.setHospital(hospital);
            personnel.setAddress(form.getAddress());
            personnel.setEmail(form.getEmail());
            personnel.setLastName(form.getLastName());
            personnel.setFirstName(form.getFirstName());
            personnel.setAge(now1 - secondDate1);
            personnel.setCity(form.getCity());
            personnel.setFunction("DOCTOR");
            personnel.setQualifier(form.getQualifier());
            personnel.setPhone(form.getPhone());
            personnel.setGender(form.getGender());
            personnel.setNeighborhood(form.getNeighborhood());
            personnel.setBirthDate(form.getBirthDate());
            //personnel.setLocalisation(form.getLocalisation());
            personnelRepository.save(personnel);
            dosMedical.setSex(form.getGender());
            dosMedical.setName(personnel.getLastName()+" "+personnel.getFirstName());
            dosMedical.setBloodType(form.getBloodType());
            dosMedical.setHeight(Integer.parseInt(form.getHeight()));
            dosMedical.setWeight(Integer.parseInt(form.getWeight()));
            dosMedical.setBirthDate(form.getBirthDate());
            dosMedical.setRhesus(form.getRhesus());
            dosMedical.setHereditaryDiseases(form.getHereditaryDiseases());
            dosMedical.setDescription(form.getDescription());
            dosMedical.setCode(form.getCode());
            dos.save(dosMedical);
            compteService.saveDoctor(form,personnel.getAvatar(),dosMedical,personnel);
            System.out.println(personnel.getLastName());
        }

        redirectAttributes.addFlashAttribute("success","You saved this doctor successfuly");
        return "redirect:/doctor/hospital/doctor/lists/"+hospital.getId();

    }

    @GetMapping("/hospital/other/add/{id}")
    public String addOther(@PathVariable Long id,Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("compte",compte);
        model.addAttribute("form",new PersonnelHelper());
        return "dashboard/pages/admin/doctor/hospital/other/form";
    }

    @PostMapping("/hospital/other/add/{id}")
    public String saveOther( @Valid PersonnelHelper form, Model model,
                             BindingResult bindingResult,HttpServletRequest request,
                             @RequestParam("file") MultipartFile file, @PathVariable Long id, RedirectAttributes redirectAttributes) throws ParseException {
        Principal principal = request.getUserPrincipal();
        Compte compte1 = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        Personnel persExists = personnelRepository.findByEmailOrPhone(form.getEmail(), form.getPhone());
        Compte compte = compteRepository.findByEmail(form.getEmail());
        Personnel personnel = new Personnel();
        DosMedical dosMedical = new DosMedical();

        if (persExists != null){
            bindingResult
                    .rejectValue("email", "error.personnel",
                            "there is already a personnel registered with that email or name or telephone provided ");
        }else if (compte != null){
            bindingResult
                    .rejectValue("email","error.compte","There are existing account with the provided email");
        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("compte",compte1);
            model.addAttribute("hospital",hospital);
            model.addAttribute("form",new PersonnelHelper());
            return  "dashboard/pages/admin/doctor/hospital/other/form";
        }else {
            if (!(file.isEmpty())){
                try {
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(fileStorage + file.getOriginalFilename());
                    Files.write(path, bytes);
                }catch (IOException e){
                    e.printStackTrace();
                }
                personnel.setAvatar("/downloadFile/"+file.getOriginalFilename());
            }else {
                personnel.setAvatar("/img/default.jpeg");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy",Locale.ENGLISH);
            String now = sdf.format(new Date());
            String secondDate = sdf.format(form.getBirthDate());
            Integer now1 = Integer.parseInt(now);
            Integer secondDate1 = Integer.parseInt(secondDate);
            personnel.setHospital(hospital);
            personnel.setAddress(form.getAddress());
            personnel.setEmail(form.getEmail());
            personnel.setLastName(form.getLastName());
            personnel.setFirstName(form.getFirstName());
            personnel.setAge(now1 - secondDate1);
            personnel.setCity(form.getCity());
            personnel.setFunction("SIMPLE");
            personnel.setQualifier(form.getQualifier());
            personnel.setPhone(form.getPhone());
            personnel.setGender(form.getGender());
            personnel.setBirthDate(form.getBirthDate());
            personnel.setNeighborhood(form.getNeighborhood());
            //personnel.setLocalisation(form.getLocalisation());
            personnelRepository.save(personnel);
            dosMedical.setSex(form.getGender());
            dosMedical.setName(personnel.getLastName()+" "+personnel.getFirstName());
            dosMedical.setBloodType(form.getBloodType());
            dosMedical.setHeight(Integer.parseInt(form.getHeight()));
            dosMedical.setWeight(Integer.parseInt(form.getWeight()));
            dosMedical.setBirthDate(form.getBirthDate());
            dosMedical.setRhesus(form.getRhesus());
            dosMedical.setHereditaryDiseases(form.getHereditaryDiseases());
            dosMedical.setDescription(form.getDescription());
            dosMedical.setCode(form.getCode());
            dos.save(dosMedical);
            compteService.saveSimple(form,personnel.getAvatar(),dosMedical,personnel);
            System.out.println(personnel.getLastName());
        }

        redirectAttributes.addFlashAttribute("success","You saved this user successfuly");
        return "redirect:/doctor/hospital/other/lists/"+hospital.getId();

    }

    @GetMapping("/hospital/doctor/update/{id}")
    public String doctorEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Personnel personnel = personnelRepository.getOne(id);
        Hospital hospital =  personnel.getHospital();
        try{
            model.addAttribute("compte",compte);
            model.addAttribute("personnel",personnel);
            model.addAttribute("hospital",hospital);
            return "dashboard/pages/admin/doctor/hospital/doctor/update";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "This doctor seems to not exist in that hospital");
            return "redirect:/doctor/hospital/doctor/lists/"+hospital.getId();
        }
    }

    @GetMapping("/hospital/other/update/{id}")
    public String otherEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs,HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Personnel personnel = personnelRepository.getOne(id);
        Hospital hospital =  personnel.getHospital();
        try{
            model.addAttribute("personnel",personnel);
            model.addAttribute("hospital",hospital);
            model.addAttribute("compte",compte);
            return "dashboard/pages/admin/doctor/hospital/other/update";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "This user seems to not exist in that hospital");
            return "redirect:/doctor/hospital/other/lists/"+hospital.getId();
        }
    }

    @PostMapping("/hospital/doctor/update/{id}")
    public String updateUser(Personnel personnel, RedirectAttributes redirectAttributes,
                             @RequestParam("file") MultipartFile file, @PathVariable Long id){
        Compte compte = compteRepository.findByEmail(personnel.getEmail());
        DosMedical dosMedical = dos.findByCode(compte.getUsername());
        Hospital hospital = hospitalRepository.getOne(id);

        if (!(file.isEmpty())){
            try{
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
            personnel.setAvatar("/downloadFile/"+file.getOriginalFilename());
        }else {
            personnel.setAvatar(personnel.getAvatar());
        }
        personnel.setHospital(hospital);
        personnel.setFunction("DOCTOR");
        personnelRepository.save(personnel);
        dosMedical.setName(personnel.getLastName()+" "+personnel.getFirstName());
        dosMedical.setBirthDate(personnel.getBirthDate());
        dos.save(dosMedical);
        redirectAttributes.addFlashAttribute("success", "The personnel has been updated successfully");
        return "redirect:/doctor/hospital/doctor/detail/"+personnel.getId();

    }


    @PostMapping("/hospital/other/update/{id}")
    public String updateOther(Personnel personnel, RedirectAttributes redirectAttributes,
                              @RequestParam("file") MultipartFile file, @PathVariable Long id){
        Compte compte = compteRepository.findByEmail(personnel.getEmail());
        DosMedical dosMedical = dos.findByCode(compte.getUsername());
        Hospital hospital = hospitalRepository.getOne(id);

        if (!(file.isEmpty())){
            try{
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
            personnel.setAvatar("/downloadFile/"+file.getOriginalFilename());
        }else {
            personnel.setAvatar(personnel.getAvatar());
        }
        personnel.setHospital(hospital);
        personnel.setFunction("SIMPLE");
        personnelRepository.save(personnel);
        dosMedical.setName(personnel.getLastName()+" "+personnel.getFirstName());
        dosMedical.setBirthDate(personnel.getBirthDate());
        dos.save(dosMedical);
        redirectAttributes.addFlashAttribute("success", "The personnel has been updated successfully");
        return "redirect:/doctor/hospital/other/detail/"+personnel.getId();

    }


}
