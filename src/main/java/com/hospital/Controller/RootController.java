package com.hospital.Controller;

import com.hospital.entities.*;
import com.hospital.repository.*;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/root")
public class RootController {

    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private CompteService compteService;
    @Autowired
    private CompteRepository compteRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PersonnelRepository personnelRepository;
    @Autowired
    private RdvRepository rdvRepository;
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    @Autowired
    private MedicamentRepository medicamentRepository;
    @Autowired
    private IncubatorRepository incubatorRepository;
    @Autowired
    private ExamenRepository examenRepository;
    @Autowired
    private FactureRepository factureRepository;
    @Autowired
    private DosMedicalRepository dosMedicalRepository;
    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private BloodBankRepository bloodBankRepository;
    @Autowired
    private ArmoireRepository armoireRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;

    @GetMapping("/login")
    public String login(Model model){
        return "dashboard/pages/admin/root/login";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        request.getSession().setAttribute("compte",compte);
        return "redirect:/root/hospital/lists";
    }

    @GetMapping(value = "/hospital/lists")
    public ModelAndView getHospitals(Model model) {

        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/root/hospital/lists");

        List<Hospital> hospitals = hospitalRepository.findAll();
        Hospital hospital = new Hospital();
        modelAndView.addObject("hospital",hospital);
        modelAndView.addObject("lists",hospitals);

        return modelAndView;
    }

    @GetMapping("/hospital/add")
    public ModelAndView showForm() {
        Hospital hospital = new Hospital();
        ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/root/hospital/form");
        modelAndView.addObject("hospital", hospital);

        return modelAndView;
    }

    @PostMapping("/hospital/add")
    public String addHospital(@Valid Hospital hospital, BindingResult bindingResult, Model model, RedirectAttributes redirAttrs){

        Pharmacy pharmacy = new Pharmacy();
        if (bindingResult.hasErrors()) {
            model.addAttribute("hospital",new Hospital());
            System.out.println(hospital.toString()) ;
            return "dashboard/pages/admin/root/hospital/form";
        }
        hospitalRepository.save(hospital);
        pharmacy.setName(hospital.getName());
        pharmacy.setHospital(hospital);
        pharmacyRepository.save(pharmacy);
        redirAttrs.addFlashAttribute("message", "Successfully added hospital " + hospital.getName());
        return "redirect:/root/hospital/lists";
    }

    @GetMapping(value = "/hospital/detail/{id}")
    public String getHospital(@PathVariable Long id, Model model,HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hos = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hos);
        model.addAttribute("compte",compte);
        model.addAttribute("doctors", personnelRepository.findAllByFunctionAndHospital_Id("DOCTOR",hos.getId()));
        model.addAttribute("simples", personnelRepository.findAllByFunctionAndHospital_Id("SIMPLE",hos.getId()));
        return "dashboard/pages/admin/root/hospital/show";
    }

    @GetMapping("/hospital/update/{id}")
    public String showEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs){

        try{
            Hospital hospital = hospitalRepository.getOne(id);
            model.addAttribute("hospital",hospital);
            return "dashboard/pages/admin/root/hospital/update";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "This hospital seems to not exist");
            return "redirect:/root/hospital/lists";
        }
    }

    @PostMapping("/hospital/update")
    public String updateHospital( @Valid Hospital hospital, BindingResult bindingResult, Errors errors, Model model, RedirectAttributes redirAttrs){
        if (bindingResult.hasErrors()) {
            model.addAttribute("hospital",hospital);
            return "dashboard/pages/admin/root/hospital/update";
        }
        hospitalRepository.save(hospital);
        redirAttrs.addFlashAttribute("message", "Successfully edited");
        return "redirect:/root/hospital/lists";
    }


    @GetMapping(value = "/hospital/doctor/lists/{id}")
    public String getHospitalDoctors(@PathVariable Long id, Model model,HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hos = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hos);
        model.addAttribute("compte",compte);
        model.addAttribute("lists", personnelRepository.findAllByFunctionAndHospital_Id("DOCTOR",hos.getId()));
        return "dashboard/pages/admin/root/hospital/doctor/lists";
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
        return "dashboard/pages/admin/root/hospital/doctor/detail";
    }

}
