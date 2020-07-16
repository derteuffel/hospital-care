package com.hospital.Controller;

import com.hospital.entities.*;
import com.hospital.enums.ERole;
import com.hospital.helpers.BloodBankHelper;
import com.hospital.helpers.CompteRegistrationDto;
import com.hospital.helpers.DosMedicalHelper;
import com.hospital.helpers.IncubatorHelper;
import com.hospital.repository.*;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/infirmier")
public class InfirmierController {
    @Autowired
    private BloodBankRepository bloodBankRepository;

    @Autowired
    private IncubatorRepository incubatorRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private DosMedicalRepository dos;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private ExamenRepository examenRepository;

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

    @GetMapping("/login")
    public String login(Model model){
        return "dashboard/pages/admin/doctor/login";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model, Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte", compte);
        Personnel pers = personnelRepository.getOne(id);
        DosMedical dosMedical = dos.findByCode(compte.getUsername());
        Personnel personnel = personnelRepository.findByEmail(compte.getEmail());
        Hospital hospital = personnel.getHospital();
        model.addAttribute("pers",pers);
        model.addAttribute("dosMedical", dosMedical);
        return "redirect:/infirmier/hospital/detail/"+hospital.getId();
    }

    @GetMapping("/hospital/detail/{id}")
    public String getHospital(@PathVariable Long id, Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hos = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hos);
        model.addAttribute("compte", compte);
        model.addAttribute("lists", personnelRepository.findAllByFunctionAndHospital_Id("INFIRMIER",hos.getId()));
        return "dashboard/pages/admin/infirmier/hospital/detail";
    }


    @GetMapping("/hospital/blood/lists/{id}")
    public String findAllByHospital(Model model, HttpServletRequest request, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hospital);
        model.addAttribute("compte",compte);
        model.addAttribute("lists", bloodBankRepository.findAllByHospital(hospital));
        return "dashboard/pages/admin/infirmier/hospital/blood/lists";
    }

    @GetMapping("/bloods/add/{id}")
    public String saveBlood(Model model, @PathVariable Long id, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("compte",compte);
        model.addAttribute("blood",new BloodBankHelper());
        return  "dashboard/pages/admin/infirmier/hospital/blood/add";
    }

    @PostMapping("/bloods/add/{id}")
    public String addBlood(RedirectAttributes redirectAttributes, @PathVariable Long id, @Valid BloodBankHelper bloodBankHelper, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        BloodBank bloodBank = bloodBankRepository.findByGroupeSanguinAndRhesus(bloodBankHelper.getGroupeSanguin(),bloodBankHelper.getRhesus());
        BloodBank blood = new BloodBank();
        if (bloodBank != null){
            bloodBank.setQuantity(bloodBank.getQuantity() + bloodBankHelper.getQuantity());
            bloodBankRepository.save(bloodBank);
        }else {
            blood.setHospital(hospital);
            blood.setDate(bloodBankHelper.getDate());
            blood.setRhesus(bloodBankHelper.getRhesus());
            blood.setGroupeSanguin(bloodBankHelper.getGroupeSanguin());
            blood.setStatus(true);
            blood.setQuantity(bloodBankHelper.getQuantity());
            bloodBankRepository.save(blood);
        }
        redirectAttributes.addFlashAttribute("success", "Your Blood item saved successfully");
        return "redirect:/infirmier/hospital/blood/lists/"+hospital.getId();
    }

    @GetMapping("/bloods/update/{id}")
    public String updateBlood(@PathVariable("id") Long id,Model model, HttpServletRequest request){
            Principal principal = request.getUserPrincipal();
            Compte compte = compteService.findByUsername(principal.getName());
            BloodBank blood = bloodBankRepository.getOne(id);
            model.addAttribute("blood",blood);
            model.addAttribute("compte",compte);
            return "dashboard/pages/admin/infirmier/hospital/blood/update";
    }

    @PostMapping("/bloods/update/{id}")
    public String updateBlood( BloodBank blood, @PathVariable Long id, RedirectAttributes redirectAttributes,
                               Long hospitalId, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Optional<BloodBank> blood1 = bloodBankRepository.findById(id);
        Hospital hospital = hospitalRepository.getOne(hospitalId);

        if (blood1.isPresent()) {
            BloodBank blood2 = blood1.get();
            blood2.setGroupeSanguin(blood.getGroupeSanguin());
            blood2.setRhesus(blood.getRhesus());
            blood2.setDate(blood.getDate());
            blood2.setQuantity(blood.getQuantity());
            bloodBankRepository.save(blood2);
            redirectAttributes.addFlashAttribute("success", "The blood has been updated successfully");
            return "redirect:/infirmier/blood/lists/"+hospital.getId();
        }
        else {
            redirectAttributes.addFlashAttribute("error","There are no blood with Id :" +id);
            return "redirect:/infirmier/blood/lists/"+hospital.getId();
        }
    }

    @GetMapping("/bloods/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        BloodBank bloodBank = bloodBankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bloodBankRepository id:" +id));
        System.out.println("bloodBankRepository id: " + bloodBank.getId());
        bloodBankRepository.delete(bloodBank);
        return "redirect:/infirmier/blood/lists/"+bloodBank.getHospital().getId();
    }

    @GetMapping("/blood/active/{id}")
    public String active(@PathVariable Long id, HttpSession session, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        BloodBank blood = bloodBankRepository.getOne(id);
        if (blood.getStatus()== true){
            blood.setStatus(false);
        }else {
            blood.setStatus(true);
        }

        bloodBankRepository.save(blood);
        return "redirect:/infirmier/bloods/lists/"+blood.getId() ;
    }


    @GetMapping("/hospital/incubator/lists/{id}")
    public String findAllIncubatorByHospital(Model model, HttpServletRequest request, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hospital);
        model.addAttribute("compte",compte);
        model.addAttribute("lists", incubatorRepository.findAllByHospital(hospital));
        return "dashboard/pages/admin/infirmier/hospital/incubator/lists";
    }

    @GetMapping("/incubator/add/{id}")
    public String saveIncubator(Model model, @PathVariable Long id, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("compte",compte);
        model.addAttribute("incubator",new IncubatorHelper());
        return  "dashboard/pages/admin/infirmier/hospital/incubator/add";
    }

    @PostMapping("/incubator/add/{id}")
    public String addIncubator(RedirectAttributes redirectAttributes, @PathVariable Long id, @Valid IncubatorHelper incubatorHelper, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
//        Incubator incubator = incubatorRepository.findByNumberLike();
        Incubator incubator = new Incubator();
        if (incubator != null){
            incubator.setQuantity(incubator.getQuantity() + incubatorHelper.getQuantity());
            incubatorRepository.save(incubator);
        }else {
            incubator.setHospital(hospital);
           incubator.setDateObtained(incubatorHelper.getDateObtained());
            incubator.setNumber(incubatorHelper.getNumber());
            incubator.setState(true);
            incubator.setStatus(incubatorHelper.getStatus());
            incubator.setType(incubatorHelper.getType());
            incubatorRepository.save(incubator);
        }
        redirectAttributes.addFlashAttribute("success", "Your Incubator item saved successfully");
        return "redirect:/infirmier/hospital/incubator/lists/"+hospital.getId();
    }

    @GetMapping("/incubator/update/{id}")
    public String updateIncubator(@PathVariable("id") Long id,Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Incubator incubator = incubatorRepository.getOne(id);
        model.addAttribute("incubator",incubator);
        model.addAttribute("compte",compte);
        return "dashboard/pages/admin/infirmier/hospital/incubator/update";
    }

    @PostMapping("/incubator/update/{id}")
    public String updateIncubator( Incubator incubator, @PathVariable Long id,
                                   RedirectAttributes redirectAttributes, Long hospitalId, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());

        Optional<Incubator> incubator1 = incubatorRepository.findById(id);
        Hospital hospital = hospitalRepository.getOne(hospitalId);

        if (incubator1.isPresent()) {
            Incubator incubator2 = incubator1.get();
            incubator2.setType(incubator.getType());
            incubator2.setStatus(incubator.getStatus());
            incubator2.setNumber(incubator.getNumber());
            incubator2.setQuantity(incubator.getQuantity());
            incubator2.setDateObtained(incubator.getDateObtained());
            incubatorRepository.save(incubator2);
            redirectAttributes.addFlashAttribute("success", "The incubator has been updated successfully");
            return "redirect:/infirmier/incubator/lists/"+hospital.getId();
        }
        else {
            redirectAttributes.addFlashAttribute("error","There are no incubator with Id :" +id);
            return "redirect:/infirmier/incubator/lists/"+hospital.getId();
        }
    }

    @GetMapping("/incubator/delete/{id}")
    public String deleteIncubator(@PathVariable Long id, Model model, HttpSession session) {
        Incubator incubator = incubatorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid incubatorRepository id:" +id));
        System.out.println("incubatorRepository id: " + incubator.getId());
        incubatorRepository.delete(incubator);
        return "redirect:/infirmier/incubator/lists/"+incubator.getHospital().getId();
    }

    @GetMapping("/incubator/active/{id}")
    public String activeIncubator(@PathVariable Long id, HttpSession session, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Incubator incubator = incubatorRepository.getOne(id);
        if (incubator.getState()== true){
            incubator.setState(false);
        }else {
            incubator.setState(true);
        }

        incubatorRepository.save(incubator);
        return "redirect:/infirmier/incubator/lists/"+incubator.getId() ;
    }


    @GetMapping("/hospital/examen/lists/{id}")
    public String getAllExamensOfHospital(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);

        List<Examen> examens = new ArrayList<>();
        Collection<Consultation> consultations = hospital.getConsultations();
        for (Consultation consultation : consultations){
            examens.addAll(consultation.getExamens());
        }
        model.addAttribute("lists",examens);
        model.addAttribute("hospital",hospital);
        model.addAttribute("compte",compte);
        return "dashboard/pages/admin/infirmier/hospital/exams";
    }

    @GetMapping("/hospital/examen/detail/{id}")
    public String getExamensOfHospital(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Examen examen = examenRepository.getOne(id);
        Hospital hospital = examen.getHospital();

        model.addAttribute("examen",examen);
        model.addAttribute("hospital",hospital);
        model.addAttribute("compte",compte);
        return "dashboard/pages/admin/infirmier/hospital/exam";
    }

    /** Retrieve all medical records */
    @GetMapping(value = "/medical-record/all")
    public String getAllMedicalRecords(Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());

        if(compte.checkRole(ERole.ROLE_INFIRMIER)) model.addAttribute("compte",compte);
        model.addAttribute("dosMedicalList",dos.findAll());
        return "dashboard/pages/admin/infirmier/dosMedical";
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
        return "dashboard/pages/admin/infirmier/addDosMedical";
    }

    /** Add a medical record */
    @PostMapping(value = "/medical-record/create")
    public String addMedicalRecord(@ModelAttribute @Valid DosMedicalHelper dosMedicalHelper,
                                   Errors errors, Model model, HttpServletRequest request){

        if(errors.hasErrors()) {
            return "dashboard/pages/admin/infirmier/addDosMedical";
        }
        Compte compte = compteRepository.findByEmail(dosMedicalHelper.getEmail());
        DosMedical dosMedical = dos.findByCode(dosMedicalHelper.getCode());

        if(compte != null){
            model.addAttribute("error","There is an existing account with the provided email");
            return "dashboard/pages/admin/infirmier/addDosMedical";
        }else if (dosMedical != null){
            model.addAttribute("error","There is an existing medical record with the provided code");
            return "dashboard/pages/admin/infirmier/addDosMedical";
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

        return "redirect:/infirmier/medical-record/search?search="+dosMedicalHelper.getCode();
    }

    @GetMapping("/medical-record/{id}")
    public String dosMedicalDetail(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        DosMedical dosMedical = dos.getOne(id);
        model.addAttribute("dosMedical",dosMedical);
        return "dashboard/pages/admin/infirmier/dosMedical-detail";
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
                return "redirect:/infirmier/medical-record/create";
            }
        }

        return "dashboard/pages/admin/infirmier/dosMedical";
    }

    @GetMapping("/armoire/detail/{id}")
    public String armoire(HttpServletRequest request, Model model, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dos.getOne(id);
        Compte compte1 = dosMedical.getCompte();
        List<Armoire> armoires = armoireRepository.findAllByCompte_Id(compte1.getId(), Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("compte", compte);
        model.addAttribute("compte1", compte1);
        model.addAttribute("lists",armoires);
        return "dashboard/pages/admin/infirmier/armoire/lists";
    }

    @GetMapping("/appointment/lists/{id}")
    public String appointments(HttpServletRequest request, Model model, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("compte",compte);
        List<Rdv> rdvs = rdvRepository.findAllByHospital_Id(hospital.getId(), Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",rdvs);
        return "dashboard/pages/admin/infirmier/appointment/lists";
    }


    @GetMapping("/appointment/actif/{id}")
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
            return "redirect:/infirmier/appointment/active/"+rdv.getId();
        }


        return "dashboard/pages/admin/infirmier/appointment/lists" ;
    }

    @GetMapping("/appointment/active/{id}")
    public String findAllStatusActive(Model model,HttpServletRequest request, @PathVariable Long id){
        Principal principal =  request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        List<Rdv> lists = rdvRepository.findAllByHospital_IdAndStatus(hospital.getId(),true,Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists", lists);
        model.addAttribute("hos", hospital);
        return "dashboard/pages/admin/infirmier/appointment/rdv-actif";
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
        return "dashboard/pages/admin/infirmier/appointment/rdv-inactif";
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
        return "dashboard/pages/admin/infirmier/appointment/detail";

    }

    @GetMapping("/appointement/assign/{id}/{doctorId}")
    public String appointmentSignToDoctor(@PathVariable Long id,@PathVariable Long doctorId, HttpServletRequest request, RedirectAttributes redirectAttributes){
        Principal principal =  request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Rdv rdv = rdvRepository.getOne(id);
        Hospital hospital = rdv.getHospital();
        Personnel personnel = personnelRepository.getOne(doctorId);
        Compte personnelAccount = compteRepository.findByPersonnel_Id(personnel.getId());
        rdv.getComptes().add(personnelAccount);
        rdv.setDoctor(personnelAccount.getName());
        rdvRepository.save(rdv);
        redirectAttributes.addFlashAttribute("success", "You successfuly assign this appointment to :"+personnel.getLastName()+" "+personnel.getFirstName());
        return "redirect:/infirmier/appointment/detail/"+rdv.getId();

    }

    @ResponseBody
    @GetMapping("/account")
    public ModelAndView getAllRdv(HttpServletRequest request){

        Principal principal =  request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());

        ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/infirmier/appointment/lists");
        List<Rdv> rdvs = rdvRepository.findAllByComptes_Id(compte.getId(),Sort.by(Sort.Direction.DESC, "id"));
        modelAndView.addObject("lists",rdvs);
        return modelAndView;
    }

    @GetMapping("/personnel/appointment/lists/{id}")
    public String appointmentsForDoctor(HttpServletRequest request, Model model, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Personnel personnel = personnelRepository.getOne(id);
        Compte compte1 = compteRepository.findByPersonnel_Id(personnel.getId());
        List<Rdv> rdvs = rdvRepository.findAllByComptes_Id(compte1.getId(), Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",rdvs);
        return "dashboard/pages/admin/infirmier/appointment/lists";
    }

    @GetMapping(value = "/medical-record/consultation/{code}")
    public String getAllConsultationsInMedicalRecord(@PathVariable String code, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dos.findByCode(code);
        List<Consultation> consultations = consultationRepository.findByDosMedical(dosMedical);
        model.addAttribute("lists",consultations);
        model.addAttribute("code",code);
        model.addAttribute("compte",compte);
        return "dashboard/pages/admin/infirmier/consultation/lists";
    }


    @GetMapping("/consultation/examen/lists/{id}")
    public String examens(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Consultation consultation = consultationRepository.getOne(id);
        DosMedical dosMedical = consultation.getDosMedical();
        Collection<Examen> examens = consultation.getExamens();
        model.addAttribute("consultation", consultation);
        model.addAttribute("lists",examens);
        model.addAttribute("dosMedical",dosMedical);
        return "dashboard/pages/admin/infirmier/consultation/examens/lists";

    }

    @GetMapping("/medical-record/examen/lists/{id}")
    public String examensByDoctor(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dos.getOne(id);
        model.addAttribute("compte",compte);
        Collection<Consultation> consultations = dosMedical.getConsultations();
        List<Examen> examens = new ArrayList<>();

        for (Consultation consultation : consultations){
            examens.addAll(consultation.getExamens());
        }

        model.addAttribute("lists",examens);
        return "dashboard/pages/admin/infirmier/medical-record/examens/lists";

    }

    @GetMapping("/consultation/prescription/lists/{id}")
    public String prescriptions(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Consultation consultation = consultationRepository.getOne(id);
        DosMedical dosMedical = consultation.getDosMedical();
        Collection<Prescription> prescriptions = consultation.getPrescriptions();


        model.addAttribute("lists",prescriptions);
        model.addAttribute("dosMedical",dosMedical);
        return "dashboard/pages/admin/infirmier/consultation/prescription/lists";

    }

    @GetMapping("/medical-record/prescription/lists/{id}")
    public String prescriptionByDoctor(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        DosMedical dosMedical = dos.getOne(id);
        model.addAttribute("compte",compte);
        Collection<Consultation> consultations = dosMedical.getConsultations();
        List<Prescription> prescriptions = new ArrayList<>();

        for (Consultation consultation : consultations){
            prescriptions.addAll(consultation.getPrescriptions());
        }

        model.addAttribute("lists",prescriptions);
        return "dashboard/pages/admin/infirmier/medical-record/prescription/lists";

    }

    @GetMapping("/personnel/lists/{id}")
    public String PersonnelDetail(@PathVariable Long id, HttpServletRequest request, Model model){
        Principal principal =  request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        List<Personnel> personnels = personnelRepository.findAllByFunctionAndHospital_Id("DOCTOR",hospital.getId());
        model.addAttribute("compte",compte);
        model.addAttribute("lists",personnels);
        model.addAttribute("hospital",hospital);
        return "dashboard/pages/admin/infirmier/personnel/lists";

    }

}
