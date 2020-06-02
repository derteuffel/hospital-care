package com.hospital.Controller;


import com.hospital.entities.DosMedical;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.DosMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/medical-record")
public class DosMedicalController {

    @Autowired
    private DosMedicalRepository dos;

    @Autowired
    private CompteRepository compteRepository;

    /** Retrieve all medical records */
    @GetMapping(value = "/all")
    public String getAllMedicalRecords(Model model){
        model.addAttribute("dosMedicalList",dos.findAll());
        return "dashboard/pages/admin/dosMedical";
    }

    /** form for adding a medical-record */
    @GetMapping(value = "/create")
    public String addMedicalRecords(){
        return "dashboard/pages/admin/addDosMedical";
    }

    /** Add a medical record */
    @PostMapping(value = "/create")
    public String addMedicalRecord(@Valid @RequestBody DosMedical dosMedical, Errors errors, Model model){
        if(errors.hasErrors()) {
            System.out.println(errors.hasErrors());
            return "error";
        }
        //dos.save(dosMedical);
        model.addAttribute("dosMedicalList", dos.findAll());
        return "dashboard/pages/admin/dosMedical";
    }

    /** Delete a medical record */
    @DeleteMapping(value = "/{id}")
    @ResponseBody public int deleteMedicalRecord(@PathVariable Long id){
        try {
            dos.deleteById(id);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    /** get a medical record */
    @GetMapping(value = "/{id}")
    @ResponseBody
    public Optional<DosMedical> getMedicalRecord(@PathVariable Long id){
        return dos.findById(id);
    }

}
