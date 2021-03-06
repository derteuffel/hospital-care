package com.hospital.Controller;

import com.hospital.entities.Compte;
import com.hospital.entities.Consultation;
import com.hospital.entities.Prescription;
import com.hospital.entities.Role;
import com.hospital.enums.ERole;
import com.hospital.helpers.PrescriptionHelper;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/prescription")
public class PrescriptionController {

    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    @Autowired
    private CompteRepository compteRepository;



    /** Get all prescriptions of a consultation */
    @GetMapping(value = "/consultation/{id}")
    public String getAllPrescriptionsOfAConsultation(@PathVariable Long id, Model model){
        Consultation consultation =  consultationRepository.getOne(id);
        List<Prescription> prescriptions = prescriptionRepository.findByConsultation(consultation);
        model.addAttribute("prescriptionList",prescriptions);
        model.addAttribute("consultation", consultation);
        model.addAttribute("idConsultation",id);
        return "dashboard/pages/admin/prescription/prescription";
    }

    /** form for adding an prescription */
    @GetMapping(value = "/create")
    public String addPrescription(@RequestParam("idConsultation") int  idConsultation, Model model){
        model.addAttribute("idConsultation",idConsultation);
        model.addAttribute(new PrescriptionHelper());
        return "dashboard/pages/admin/prescription/addPrescription";
    }

    /** Add an prescription */
    @PostMapping("/create")
    public String savePrescription(@ModelAttribute @Valid PrescriptionHelper prescriptionHelper, Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute("idConsultation",prescriptionHelper.getIdConsultation());
            return "dashboard/pages/admin/prescription/addPrescription";
        }else{
            Consultation consultation = consultationRepository.getOne(prescriptionHelper.getIdConsultation());
            prescriptionRepository.save(prescriptionHelper.getPrescriptionInstance(consultation));
        }
        return  "redirect:/admin/prescription/consultation/"+prescriptionHelper.getIdConsultation();
    }

    /** cancel a exam */
    @PostMapping(value = "/cancel")
    public String cancelPrescription(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes){

        Long id = Long.parseLong(request.getParameter("id"));
        String username = request.getParameter("username");
        String idConsultation = request.getParameter("idConsultation");
        Compte compte = compteRepository.findByUsername(username);
        boolean authorized = false;

        if(compte == null){
            model.addAttribute("error","There is no account with this username");
            System.out.println(model.getAttribute("error"));
            return "redirect:/admin/prescription/consultation/"+idConsultation;
        }else{
            for (Role role : compte.getRoles()){
                if(role.getName().equals(ERole.ROLE_ROOT.toString())){
                    authorized = true;
                }
            }

            if(!authorized){
                model.addAttribute("error","you don't have rights to perform this operation");
                System.out.println(model.getAttribute("error"));
                return "redirect:/admin/prescription/consultation/"+idConsultation;
            }
        }

        prescriptionRepository.deleteById(id);
        model.addAttribute("success","Operation successfully completed");
        System.out.println(model.getAttribute("success"));
        return "redirect:/admin/prescription/consultation/"+idConsultation;
    }

    /** form for updating a prescription */
    @GetMapping(value = "/update/{idPrescription}")
    public String updatePrescription(@PathVariable Long idPrescription, @RequestParam("idConsultation") Long idConsultation, Model model){
        model.addAttribute("idConsultation",idConsultation);
        model.addAttribute("prescriptionHelper", PrescriptionHelper.getPrescriptionHelperInstance(prescriptionRepository.getOne(idPrescription)));
        return "dashboard/pages/admin/prescription/updatePrescription";
    }

    /** Update a prescription */
    @PostMapping(value = "/update/{idPrescription}")
    public String updatePrescription(@PathVariable Long idPrescription, @ModelAttribute @Valid PrescriptionHelper prescriptionHelper, Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute("idConsultation",prescriptionHelper.getIdConsultation());
            return "dashboard/pages/admin/prescription/updatePrescription";
        }else{
            Consultation consultation = consultationRepository.getOne(prescriptionHelper.getIdConsultation());
            Prescription exPrescription = prescriptionRepository.getOne(idPrescription);
            Prescription newPrescription = prescriptionHelper.getPrescriptionInstance(consultation);
            newPrescription.setId(exPrescription.getId());
            prescriptionRepository.save(newPrescription);
        }
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
        return  "redirect:/admin/prescription/consultation/"+prescriptionHelper.getIdConsultation();
    }

}