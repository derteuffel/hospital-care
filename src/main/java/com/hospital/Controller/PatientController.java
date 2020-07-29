package com.hospital.Controller;


import com.hospital.entities.*;
import com.hospital.repository.*;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Value("${file.upload-dir}")
    private  String fileStorage;


    @Autowired
    private DosMedicalRepository dos;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private ArmoireRepository armoireRepository;

    @Autowired
    private CompteService compteService;

    @Autowired
    private RdvRepository rdvRepository;
    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;


    @GetMapping("/login")
    public String login(Model model){
        return "dashboard/pages/admin/patient/login";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte", compte);
        DosMedical dosMedical = dos.findByCode(compte.getUsername());
        model.addAttribute("dosMedical", dosMedical);
        return "dashboard/pages/admin/patient/home";
    }

    @GetMapping("/medical-record/{code}")
    public String dosMedicalDetail(@PathVariable String code, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        DosMedical dosMedical = dos.findByCode(code);
        model.addAttribute("dosMedical",dosMedical);
        return "dashboard/pages/admin/patient/medical-record/dosMedical";
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
        return "dashboard/pages/admin/patient/consultations/lists";

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
        return "dashboard/pages/admin/patient/consultations/detail";

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
        return "dashboard/pages/admin/patient/examens/lists";

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
        return "dashboard/pages/admin/patient/prescriptions/lists";

    }

    @GetMapping("/appointment/lists")
    public String appointments(HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        List<Rdv> rdvs = rdvRepository.findAllByComptes_Id(compte.getId(), Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",rdvs);
        return "dashboard/pages/admin/appointment/lists";
    }

    @GetMapping("/hospital/detail/{id}")
    public String getHospital(@PathVariable Long id, Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hos = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hos);
        model.addAttribute("compte", compte);
        model.addAttribute("lists", personnelRepository.findAllByFunctionAndHospital_Id("DOCTOR",hos.getId()));
        return "dashboard/pages/admin/patient/hospital/detail";
    }


    @GetMapping(value = "/hospital/lists")
    public ModelAndView getHospitals(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/patient/hospital/lists");

        List<Hospital> hospitals = hospitalRepository.findAll();
        modelAndView.addObject("lists",hospitals);
        modelAndView.addObject("compte", compte);

        return modelAndView;
    }


    @GetMapping("/appointment/add/{id}")
    public ModelAndView showform(HttpServletRequest request, @PathVariable Long id){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/patient/appointment/add-rdv");
        Rdv rdv = new Rdv();
        Hospital hospital = hospitalRepository.getOne(id);
        modelAndView.addObject("hospital",hospital);
        modelAndView.addObject("rdv", rdv);
        modelAndView.addObject("compte",compte);
//        modelAndView.addObject("appointments", rdvRepository.findAll());
        return modelAndView;
    }

    @PostMapping("/appointment/add/{id}")
    public String storeRdv(@ModelAttribute @Valid Rdv rdv, Errors errors, RedirectAttributes redirAttrs, HttpServletRequest request,Model model, @PathVariable Long id){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        if(errors.hasErrors()){
            return "dashboard/pages/admin/patient/appointment/add-rdv";
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
        return "redirect:/patient/appointment/lists";
    }

    @GetMapping("/armoire/lists")
    public String armoire(HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        List<Armoire> armoires = armoireRepository.findAllByCompte_Id(compte.getId(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("compte", compte);
        model.addAttribute("lists",armoires);
        return "dashboard/pages/admin/patient/armoire/lists";
    }

    @GetMapping("/armoire/add")
    public String newArmoire(Model model){
        model.addAttribute("armoire",new Armoire());
        return "dashboard/pages/admin/patient/armoire/new";
    }

    @PostMapping("/armoire/add")
    public String saveArmoire(@Valid Armoire armoire, @RequestParam("files") MultipartFile[] files, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        armoire.setDate(sdf.format(new Date()));
        armoire.setCompte(compte);
        ArrayList<String> pieces = new ArrayList<>();
        if (files.length != 0){
            for (MultipartFile  file : files){
                if (!(file.isEmpty())){
                    try{
                        // Get the file and save it somewhere
                        byte[] bytes = file.getBytes();
                        Path path = Paths.get(fileStorage + file.getOriginalFilename());
                        Files.write(path, bytes);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                pieces.add("/downloadFile/"+file.getOriginalFilename());
            }
            armoire.setFichiers(pieces);
        }else {
           model.addAttribute("error", "Your do not have file in your consultation, please add file image for consultation pictures");
           model.addAttribute("armoire", new Armoire());
           return "dashboard/pages/admin/patient/armoire/new";
        }

        armoireRepository.save(armoire);
        return "redirect:/patient/armoire/lists";
    }

}
