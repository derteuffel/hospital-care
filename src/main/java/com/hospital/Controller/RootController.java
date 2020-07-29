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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
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
    private DosMedicalRepository dos;
    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private BloodBankRepository bloodBankRepository;
    @Autowired
    private ArmoireRepository armoireRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${file.upload-dir}")
    private  String fileStorage;

    @GetMapping("/login")
    public String login(Model model){
        return "dashboard/pages/admin/root/login";
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
        return "redirect:/root/hospital/lists";
    }

    @GetMapping(value = "/hospital/lists")
    public ModelAndView getHospitals(Model model) {

        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/root/hospital/lists");

        List<Hospital> hospitals = hospitalRepository.findAll();
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

    @PostMapping("/add")
    public String addHospital(@Valid Hospital hospital, BindingResult bindingResult, Model model, RedirectAttributes redirAttrs){

        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            model.addAttribute("hospital",new Hospital());
            return "dashboard/pages/admin/root/hospital/form";
        }
        hospitalRepository.save(hospital);
        redirAttrs.addFlashAttribute("message", "Successfully added hospital " + hospital.getName());
        return "redirect:/root/hospital/lists";
    }

    @GetMapping("/hospital/detail/{id}")
    public String getHospital(@PathVariable Long id, Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hos = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hos);
        model.addAttribute("compte",compte);
        model.addAttribute("doctors", personnelRepository.findAllByFunctionAndHospital_Id("DOCTOR",hos.getId()));
        model.addAttribute("simples", personnelRepository.findAllByFunctionAndHospital_Id("SIMPLE",hos.getId()));
        return "dashboard/pages/admin/root/hospital/show";
    }

    @GetMapping("/hospital/pharmacie/{id}")
    public String getPharmacy(@PathVariable Long id, Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hos = hospitalRepository.getOne(id);
        Pharmacy pharmacy = pharmacyRepository.findByHospital_Id(hos.getId());
        model.addAttribute("hospital", hos);
        model.addAttribute("pharmacy",pharmacy);
        model.addAttribute("compte",compte);

        return "dashboard/pages/admin/root/hospital/pharmacy/detail";
    }

    @GetMapping("/hospital/pharmacie/medicament/lists/{id}")
    public String findAllByPharmacy(Model model, @PathVariable Long id){
        Pharmacy pharmacy = pharmacyRepository.getOne(id);

        List<Medicament> medicaments = medicamentRepository.findAllByPharmacy_Id(pharmacy.getId(), Sort.by(Sort.Direction.DESC,"id"));
        for (Medicament medicament : medicaments){
            if (medicament.getStockQuantity() == 0){
                medicament.setStatus(false);
                medicamentRepository.save(medicament);
            }
        }
        model.addAttribute("pharmacy", pharmacy);
        model.addAttribute("lists", medicaments);
        return "dashboard/pages/admin/root/hospital/pharmacy/medicament/lists";
    }

    @GetMapping("/hospital/pharmacie/medicament/detail/{id}")
    public String medicamentDetail(Model model, @PathVariable Long id){
        Medicament  medicament = medicamentRepository.getOne(id);

        Pharmacy pharmacy = medicament.getPharmacy();
        model.addAttribute("pharmacy", pharmacy);
        model.addAttribute("medicament", medicament);
        return "dashboard/pages/admin/root/hospital/pharmacy/medicament/detail";
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

    @GetMapping("/hospital/delete/{id}")
    public String deleteById(@PathVariable Long id, RedirectAttributes redirectAttributes, String username) {

        Compte compte = compteRepository.findByUsername(username);
        Optional<Role> role = roleRepository.findByName("ROLE_ROOT");
        if (compte.getRoles().contains(role)) {
            Hospital hospital = hospitalRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid hospital id:" + id));
            hospitalRepository.delete(hospital);
            redirectAttributes.addFlashAttribute("message","Delete operation success");
        }else {
            redirectAttributes.addFlashAttribute("message","You don't have access to do this operation");
        }
        return "redirect:/root/hospital/lists";

    }


    @GetMapping("/armoire/detail/{id}")
    public String armoireDetail(HttpServletRequest request, Model model, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dos.getOne(id);
        Compte compte1 = dosMedical.getCompte();
        List<Armoire> armoires = armoireRepository.findAllByCompte_Id(compte1.getId(), Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("compte", compte);
        model.addAttribute("compte1", compte1);
        model.addAttribute("lists",armoires);
        return "dashboard/pages/admin/root/armoire/lists";
    }

    @GetMapping("/hospital/blood/lists/{id}")
    public String bloodBank(HttpServletRequest request, Model model, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);

        List<BloodBank> bloodBanks = bloodBankRepository.findAllByHospital(hospital);
        model.addAttribute("compte", compte);
        model.addAttribute("hosptial", hospital);
        model.addAttribute("lists",bloodBanks);
        return "dashboard/pages/admin/root/blood-bank/lists";
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
        return "dashboard/pages/admin/root/incubator/lists";
    }

    @GetMapping("/consultation/observation/{id}")
    public String addObservationToConsultation(@PathVariable Long id, String observation){
        Consultation consultation = consultationRepository.getOne(id);
        consultation.setObservations(observation);
        consultationRepository.save(consultation);
        return "redirect:/root/consultation/detail/"+consultation.getId();
    }

    @GetMapping("/prescription/delete/{id}")
    public String deletePrescriptionById(@PathVariable Long id, Model model) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid prescription id:" +id));
        Consultation consultation = prescription.getConsultation();
        prescriptionRepository.delete(prescription);
        return "redirect:/root/consultation/prescription/lists/"+consultation.getId();

    }

    @GetMapping("/appointment/lists")
    public String appointments(HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        List<Rdv> rdvs = rdvRepository.findAll();
        model.addAttribute("lists",rdvs);
        return "dashboard/pages/admin/root/appointment/lists";
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
            return "redirect:/root/appointment/lists";
        }


        return "redirect:/root/appointment/lists" ;
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

    @GetMapping(value = "/hospital/other/lists/{id}")
    public String getHospitalOthers(@PathVariable Long id, Model model,HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hos = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hos);
        model.addAttribute("compte",compte);
        model.addAttribute("lists", personnelRepository.findAllByFunctionAndHospital_Id("SIMPLE",hos.getId()));
        return "dashboard/pages/admin/root/hospital/other/lists";
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
        return "dashboard/pages/admin/root/hospital/other/detail";
    }

    @GetMapping("/hospital/doctor/add/{id}")
    public String addDoctor(@PathVariable Long id,Model model){
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("form",new PersonnelHelper());
        return "dashboard/pages/admin/root/hospital/doctor/form";
    }

    @PostMapping("/hospital/doctor/add/{id}")
    public String saveDoctor( @Valid PersonnelHelper form, Model model,
                              BindingResult bindingResult,
                              @RequestParam("file") MultipartFile file, @PathVariable Long id, RedirectAttributes redirectAttributes) throws ParseException {

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
            model.addAttribute("hospital",hospital);
            model.addAttribute("form",new PersonnelHelper());
            return  "dashboard/pages/admin/root/hospital/doctor/form";
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
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy",Locale.ENGLISH);
            Date now = sdf.parse(sdf.format(new Date()));
            Date secondDate = sdf.parse(sdf.format(form.getBirthDate()));
            long difference = Math.abs(now.getTime()-secondDate.getTime());
            long days = TimeUnit.DAYS.convert(difference,TimeUnit.MILLISECONDS);
            personnel.setHospital(hospital);
            personnel.setAddress(form.getAddress());
            personnel.setEmail(form.getEmail());
            personnel.setLastName(form.getLastName());
            personnel.setFirstName(form.getFirstName());
            personnel.setAge(Math.round(days/365));
            personnel.setCity(form.getCity());
            personnel.setFunction("DOCTOR");
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
            compteService.saveDoctor(form,personnel.getAvatar(),dosMedical,personnel);
            System.out.println(personnel.getLastName());
        }

        redirectAttributes.addFlashAttribute("success","You saved this doctor successfuly");
        return "redirect:/root/hospital/doctor/lists/"+hospital.getId();

    }

    @GetMapping("/hospital/other/add/{id}")
    public String addOther(@PathVariable Long id,Model model){
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("form",new PersonnelHelper());
        return "dashboard/pages/admin/root/hospital/other/form";
    }

    @PostMapping("/hospital/other/add/{id}")
    public String saveOther( @Valid PersonnelHelper form, Model model,
                              BindingResult bindingResult,
                              @RequestParam("file") MultipartFile file, @PathVariable Long id, RedirectAttributes redirectAttributes) throws ParseException {

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
            model.addAttribute("hospital",hospital);
            model.addAttribute("form",new PersonnelHelper());
            return  "dashboard/pages/admin/root/hospital/other/form";
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
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy",Locale.ENGLISH);
            Date now = sdf.parse(sdf.format(new Date()));
            Date secondDate = sdf.parse(sdf.format(form.getBirthDate()));
            long difference = Math.abs(now.getTime()-secondDate.getTime());
            long days = TimeUnit.DAYS.convert(difference,TimeUnit.MILLISECONDS);
            personnel.setHospital(hospital);
            personnel.setAddress(form.getAddress());
            personnel.setEmail(form.getEmail());
            personnel.setLastName(form.getLastName());
            personnel.setFirstName(form.getFirstName());
            personnel.setAge(Math.round(days/365));
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
        return "redirect:/root/hospital/other/lists/"+hospital.getId();

    }

    @GetMapping("/hospital/doctor/update/{id}")
    public String doctorEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs){

        Personnel personnel = personnelRepository.getOne(id);
        Hospital hospital =  personnel.getHospital();
        try{
            model.addAttribute("personnel",personnel);
            model.addAttribute("hospital",hospital);
            return "dashboard/pages/admin/root/hospital/doctor/update";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "This doctor seems to not exist in that hospital");
            return "redirect:/root/hospital/doctor/lists/"+hospital.getId();
        }
    }

    @GetMapping("/hospital/other/update/{id}")
    public String otherEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs){

        Personnel personnel = personnelRepository.getOne(id);
        Hospital hospital =  personnel.getHospital();
        try{
            model.addAttribute("personnel",personnel);
            return "dashboard/pages/admin/root/hospital/other/update";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "This user seems to not exist in that hospital");
            return "redirect:/root/hospital/other/lists/"+hospital.getId();
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
            personnelRepository.save(personnel);
            dosMedical.setName(personnel.getLastName()+" "+personnel.getFirstName());
            dosMedical.setBirthDate(personnel.getBirthDate());
            dos.save(dosMedical);
            redirectAttributes.addFlashAttribute("success", "The personnel has been updated successfully");
            return "redirect:/root/hospital/doctor/detail/"+personnel.getId();

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
        personnelRepository.save(personnel);
        dosMedical.setName(personnel.getLastName()+" "+personnel.getFirstName());
        dosMedical.setBirthDate(personnel.getBirthDate());
        dos.save(dosMedical);
        redirectAttributes.addFlashAttribute("success", "The personnel has been updated successfully");
        return "redirect:/root/hospital/other/detail/"+personnel.getId();

    }




    @GetMapping("/medical-record/lists")
    public String dosMedicalDetail( Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        model.addAttribute("lists",dos.findAll());
        return "dashboard/pages/admin/root/dosMedical";
    }

    /** search a medical record */
    @GetMapping(value = "/medical-record/detail/{id}")
    public String getMedicalRecord(@PathVariable Long id,Model model, RedirectAttributes redirectAttributes){
            DosMedical dosMedical = dos.getOne(id);
            if(dosMedical != null) {

                model.addAttribute("dosMedical", dosMedical);
            }else {
                redirectAttributes.addFlashAttribute("error","There are no account and medical records with provided code. Please add new medical record");
                return "redirect:/root/medical-record/lists";
            }

        return "dashboard/pages/admin/root/dosMedical-detail";
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
        System.out.println(consultation.getId());
        return "dashboard/pages/admin/root/consultation/detail";

    }

    /** Get all consultations in a medical record */
    @GetMapping(value = "/medical-record/consultation/lists/{code}")
    public String getAllConsultationsInMedicalRecord(@PathVariable String code, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dos.findByCode(code);
        List<Consultation> consultations = consultationRepository.findByDosMedical(dosMedical);
        model.addAttribute("lists",consultations);
        model.addAttribute("code",code);
        model.addAttribute("compte",compte);
        return "dashboard/pages/admin/root/consultation/lists";
    }


    /** Get all consultations in an hospital */
    @GetMapping(value = "/doctor/consultation/lists/{id}")
    public String getAllConsultationsForDoctor(Model model, @PathVariable Long id){
        Personnel personnel = personnelRepository.getOne(id);
        Compte compte = compteRepository.findByPersonnel_Id(personnel.getId());
        List<Consultation> consultations = consultationRepository.findAllByPersonnel_Id(personnel.getId(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists", consultations);
        model.addAttribute("code",compte.getUsername());
        model.addAttribute("personnel",personnel);

        return "dashboard/pages/admin/root/hospital/consultation/lists";
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

        return "dashboard/pages/admin/root/hospital/consultation/lists";
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

        return "redirect:/root/consultation/lists/"+compte.getPersonnel().getId();

    }

    @GetMapping("/consultation/examen/lists/{id}")
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
        return "dashboard/pages/admin/root/examens/lists";

    }
    @GetMapping("/hospital/examen/lists/{id}")
    public String examensInHospital(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Hospital hospital = hospitalRepository.getOne(id);
        List<Examen> examens = examenRepository.findByHospital(hospital);

        model.addAttribute("lists",examens);
        return "dashboard/pages/admin/root/examens/lists";

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
        return "dashboard/pages/admin/root/examens/lists";

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
        return "dashboard/pages/admin/root/examens/detail";

    }

    @GetMapping("/consultation/ordonances/lists/{id}")
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
        return "dashboard/pages/admin/root/prescription/lists";

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
        return "dashboard/pages/admin/root/prescription/lists";
    }





    @GetMapping("/medical-record/armoire/lists/{id}")
    public String armoire(HttpServletRequest request, Model model, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dos.getOne(id);
        Compte compte1 = compteRepository.findByUsername(dosMedical.getCode());
        List<Armoire> armoires = armoireRepository.findAllByCompte_Id(compte1.getId(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("compte", compte);
        model.addAttribute("lists",armoires);
        return "dashboard/pages/admin/root/armoire/lists";
    }





    @GetMapping("/appointment/inactive/{id}")
    public String findAllStatusInactive(Model model,HttpServletRequest request, @PathVariable Long id){
        Principal principal =  request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        List<Rdv> lists = rdvRepository.findAllByHospital_IdAndStatus(hospital.getId(),false,Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists", lists);
        model.addAttribute("compte",compte);
        model.addAttribute("hos", hospital);
        return "dashboard/pages/admin/root/appointment/rdv-inactif";
    }

    @GetMapping("/appointement/detail/{id}")
    public String appointmentDetail(@PathVariable Long id, HttpServletRequest request, Model model){
        Principal principal =  request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Rdv rdv = rdvRepository.getOne(id);
        Hospital hospital = rdv.getHospital();
        List<Personnel> personnels = personnelRepository.findAllByFunctionAndHospital_Id("DOCTOR",hospital.getId());
        model.addAttribute("compte",compte);
        model.addAttribute("rdv",rdv);
        model.addAttribute("lists",personnels);
        model.addAttribute("hospital",hospital);
        return "dashboard/pages/admin/root/appointment/detail";

    }

    @ResponseBody
    @GetMapping("/appointment/account")
    public ModelAndView getAllRdv(HttpServletRequest request){

        Principal principal =  request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());

        ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/root/appointment/lists");
        List<Rdv> rdvs = rdvRepository.findAllByComptes_Id(compte.getId(),Sort.by(Sort.Direction.DESC, "id"));
        modelAndView.addObject("lists",rdvs);
        return modelAndView;
    }

    @GetMapping("/doctor/appointment/lists/{id}")
    public String appointmentsForDoctor(HttpServletRequest request, Model model, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Compte compte1 = compteRepository.getOne(id);
        List<Rdv> rdvs = rdvRepository.findAllByComptes_Id(compte1.getId(), Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",rdvs);
        return "dashboard/pages/admin/root/appointment/lists";
    }

    @GetMapping("/compte/lists")
    public String getAllAccounts(Model model,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        List<Compte> comptes = compteRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists", comptes);
        model.addAttribute("compte",compte);
        return "dashboard/pages/admin/root/compte/lists";
    }

}
