package com.hospital.Controller;


import com.hospital.entities.DosMedical;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.DosMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@ResponseBody
@RequestMapping("/hospital-care/dossier-medical")
public class DosMedicalController {

    @Autowired
    private DosMedicalRepository dos;

    @Autowired
    private CompteRepository compteRepository;

    /** Retrieve all medical records */
    @GetMapping(value = "/")
    public List<DosMedical> getAllMedicalRecords(){
        List<DosMedical> DosMedical = dos.findAll();
        return DosMedical;
    }

    /** Add a medical record */
    @PostMapping(value = "/")
    public DosMedical addMedicalRecord(@RequestBody DosMedical dosMedical){
         dos.save(dosMedical);
        return dosMedical;
    }

    /** Delete a medical record */
    @DeleteMapping(value = "/{id}")
    public int deleteMedicalRecord(@PathVariable Long id){
        try {
            dos.deleteById(id);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    /** get a medical record */
    @GetMapping(value = "/{id}")
    public Optional<DosMedical> getMedicalRecord(@PathVariable Long id){
        return dos.findById(id);
    }



}
