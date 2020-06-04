package com.hospital.Controller;


import com.hospital.entities.Compte;
import com.hospital.entities.DosMedical;
import com.hospital.helpers.CompteRegistrationDto;
import com.hospital.helpers.DosMedicalHelper;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.DosMedicalRepository;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin/medical-record")
public class DosMedicalController {

    @Autowired
    private DosMedicalRepository dos;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private CompteService compteService;
    /** Retrieve all medical records */
    @GetMapping(value = "/all")
    public String getAllMedicalRecords(Model model){
        model.addAttribute("dosMedicalList",dos.findAll());
        return "dashboard/pages/admin/dosMedical";
    }

    /** form for adding a medical-record */
    @GetMapping(value = "/create")
    public String addMedicalRecords(Model model){
        model.addAttribute(new DosMedicalHelper());
        return "dashboard/pages/admin/addDosMedical";
    }

    /** Add a medical record */
    @PostMapping(value = "/create")
    public String addMedicalRecord(@ModelAttribute @Valid DosMedicalHelper dosMedicalHelper, Errors errors, Model model){
        if(errors.hasErrors()) {
            return "dashboard/pages/admin/addDosMedical";
        }
        Compte compte = compteRepository.findByEmail(dosMedicalHelper.getEmail());
        DosMedical dosMedical = dos.findByCode(dosMedicalHelper.getCode());

        if(compte != null){
           model.addAttribute("error","There are existing account with provided email");
           model.addAttribute(new DosMedicalHelper());
           return "dashboard/pages/admin/addDosMedical";
        }else if (dosMedical != null){
            model.addAttribute("error","There are existing medical records with provided code, try to connect to their account please");
            model.addAttribute(new DosMedicalHelper());
            return "dashboard/pages/admin/addDosMedical";
        }else {
            DosMedical dosMedical1 = new DosMedical();
            dosMedical1.setAge(Integer.parseInt(dosMedicalHelper.getAge()));
            dosMedical1.setCode(dosMedicalHelper.getCode());
            dosMedical1.setDescription(dosMedicalHelper.getDescription());
            dosMedical1.setHereditaryDiseases(dosMedicalHelper.getHereditaryDiseases());
            dosMedical1.setRhesus(dosMedicalHelper.getRhesus());
            dosMedical1.setWeight(Integer.parseInt(dosMedicalHelper.getWeight()));
            dos.save(dosMedical1);
            CompteRegistrationDto compteDto = new CompteRegistrationDto();
            compteDto.setEmail(dosMedicalHelper.getEmail());
            compteDto.setPassword("1234567890");
            compteDto.setUsername(dosMedicalHelper.getCode());
            compteService.savePatient(compteDto,"/img/default.jpeg",dosMedical1);


        }
        return "redirect:/admin/medical-record/all";
    }

    /** cancel a medical record */
    @PostMapping(value = "/cancel")
    public String cancelDosMedical(HttpServletRequest request, Model model){

        Long id = Long.parseLong(request.getParameter("id"));
        String password = request.getParameter("password");
        Compte compte = compteRepository.findByPassword(password);

        if(compte != null){
            if(compte.getStatus()){
                dos.deleteById(id);
            }
        }
        return "redirect:/admin/medical-record/all";
    }

    /** get a medical record */
    @GetMapping(value = "/{id}")
    @ResponseBody
    public DosMedical getMedicalRecord(@PathVariable Long id){
        return dos.getOne(id);
    }

}
