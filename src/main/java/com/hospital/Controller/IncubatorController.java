package com.hospital.Controller;


import com.hospital.entities.Hospital;
import com.hospital.entities.Incubator;
import com.hospital.helpers.IncubatorHelper;
import com.hospital.repository.HospitalRepository;
import com.hospital.repository.IncubatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javax.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/hospital-care/incubator")
public class IncubatorController {


    @Autowired
    private IncubatorRepository incubatorRepository;

    @Autowired
    private HospitalRepository hospitalRepository;



    @PostMapping("/save/{id}")
    public String saveIncubator(@Valid IncubatorHelper incubatorHelper, @PathVariable Long id,
                                RedirectAttributes redirectAttributes){

        Hospital hospital = hospitalRepository.getOne(id);
        Incubator incubator = new Incubator();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd hh:mm");
        incubator.setDateObtained(dateFormat.format(date));
        incubator.setIncubatorNo(incubatorHelper.getIncubatorNo());
        incubator.setQuantity(incubatorHelper.getQuantity());
        incubator.setStatus(incubatorHelper.getStatus());
        //incubator.setHospital(hospital);
        incubatorRepository.save(incubator);

        return ""; /**** ici on va voir la vue qu'il faut mettre *****/

    }


    @GetMapping("/detail/{id}")
    public String getIncubator(@PathVariable Long id, Model model){

       // Incubator incubator = incubartorRepository.getOne(id);

       // model.addAttribute("incubator", incubator);

        return ""; /**** ici on va voir la vue qu'il faut mettre *****/
    }
}
