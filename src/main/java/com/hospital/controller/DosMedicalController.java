package com.hospital.Controller;

import com.hospital.entities.Compte;
import com.hospital.entities.DosMedical;
import com.hospital.entities.Examen;
import com.hospital.entities.Role;
import com.hospital.enums.ERole;
import com.hospital.helpers.CompteRegistrationDto;
import com.hospital.helpers.DosMedicalHelper;
import com.hospital.repository.*;
import com.hospital.services.CompteService;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("/admin/medical-record")
public class DosMedicalController {

    @Autowired
    private DosMedicalRepository dos;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CompteService compteService;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private DosMedicalRepository dosMedicalRepository;

    /** Retrieve all medical records */
    @GetMapping(value = "/all")
    public String getAllMedicalRecords(Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());

        if(compte.checkRole(ERole.ROLE_ROOT)) model.addAttribute("compte",compte);
        model.addAttribute("dosMedicalList",dos.findAll());
        return "dashboard/pages/admin/patient/dosMedical";
    }

    /** form for adding a medical-record */
    @GetMapping(value = "/create")
    public String addMedicalRecords(HttpServletRequest request, Model model){
        String code = request.getParameter("code");
        DosMedicalHelper dosMedicalHelper = new DosMedicalHelper();
        if(code != null) dosMedicalHelper.setCode(code);
        model.addAttribute("dosMedicalHelper",dosMedicalHelper);
        return "dashboard/pages/admin/patient/addDosMedical";
    }

    /** Add a medical record */
    @PostMapping(value = "/create")
    public String addMedicalRecord(@ModelAttribute @Valid DosMedicalHelper dosMedicalHelper, Errors errors, Model model, HttpServletRequest request){
        if(errors.hasErrors()) {
            return "dashboard/pages/admin/patient/addDosMedical";
        }
        Compte compte = compteRepository.findByEmail(dosMedicalHelper.getEmail());
        DosMedical dosMedical = dos.findByCode(dosMedicalHelper.getCode());

        if(compte != null){
            model.addAttribute("error","There is an existing account with the provided email");
            return "dashboard/pages/admin/patient/addDosMedical";
        }else if (dosMedical != null){
            model.addAttribute("error","There is an existing medical record with the provided code");
            return "dashboard/pages/admin/patient/addDosMedical";
        }else {
            CompteRegistrationDto compteDto = new CompteRegistrationDto();
            compteDto.setEmail(dosMedicalHelper.getEmail());
            compteDto.setPassword("1234567890");
            compteDto.setUsername(dosMedicalHelper.getCode());
            compteService.savePatient(compteDto,"/img/default.jpeg", dosMedicalHelper.getDosMedicalInstance());
        }

        model.addAttribute("success","Operation successfully completed");
        Principal principal = request.getUserPrincipal(); Compte loggedAccount = compteService.findByUsername(principal.getName());

        if(loggedAccount.checkRole(ERole.ROLE_ROOT)) return "redirect:/admin/medical-record/all";
        else return "redirect:/admin/medical-record/search?search="+dosMedicalHelper.getCode();
    }

    /** cancel a medical record */
    @GetMapping(value = "/cancel")
    public String cancelDosMedical(HttpServletRequest request, Model model){

        Long id = Long.parseLong(request.getParameter("id"));
        String username = request.getParameter("username");
        Compte compte = compteRepository.findByUsername(username);
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
        }

        DosMedical dosMedical = dos.getOne(id);
        dosMedical.getConsultations().forEach(consultation -> {
            prescriptionRepository.deleteAll(consultation.getPrescriptions());
            examenRepository.deleteAll(consultation.getExamens());
        });
        consultationRepository.deleteAll(dosMedical.getConsultations());
        dos.deleteById(id);
        model.addAttribute("success","Operation successfully completed");
        return "redirect:/admin/medical-record/all";
    }

    /** form for updating a medical-record */
    @GetMapping(value = "/update/{code}")
    public String updateMedicalRecords(@PathVariable String code, Model model){
        DosMedical dosMedical = dos.findByCode(code);

        if(dosMedical != null){
            model.addAttribute("dosMedicalHelper",DosMedicalHelper.getDosMedicalHelperInstance(dosMedical));
        }
        return "dashboard/pages/admin/patient/updateDosMedical";
    }

    /** Update a medical record */
    @PostMapping(value = "/update/{code}")
    public String updateMedicalRecord(@PathVariable String code, @ModelAttribute @Valid DosMedicalHelper dosMedicalHelper, Errors errors, Model model){
        if(errors.hasErrors()) {
            return "dashboard/pages/admin/patient/updateDosMedical";
        }
        DosMedical exDosMedical = dos.findByCode(code);
        DosMedical newDosMedical = dosMedicalHelper.getDosMedicalInstance();
        exDosMedical.getCompte().setEmail(dosMedicalHelper.getEmail());
        newDosMedical.setId(exDosMedical.getId());
        newDosMedical.setCompte(exDosMedical.getCompte());
        dos.save(newDosMedical);
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
        return "redirect:/admin/medical-record/all";
    }

    /** check if a medical record exists */
    @GetMapping(value = "exists/{code}")
    @ResponseBody
    public int existsMedicalRecord(@PathVariable String code){
        DosMedical dosMedical = dos.findByCode(code);
        if(dosMedical != null) return 1;
        else return 0;
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
                return "redirect:/admin/medical-record/create";
            }
        }

        return "dashboard/pages/admin/patient/dosMedical";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model) {
        DosMedical dosMedical = dosMedicalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid consultation id:" +id));
        dosMedicalRepository.delete(dosMedical);
        return "redirect:/admin/medical-record/all";

    }

}
