package com.hospital.controller;


import com.hospital.entities.BloodBank;
import com.hospital.entities.Hospital;
import com.hospital.helpers.BloodBankHelper;
import com.hospital.repository.BloodBankRepository;
import com.hospital.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/blood")
public class BloodBankController {

    @Autowired
    private BloodBankRepository bloodBankRepository;

    @Autowired
    private HospitalRepository hospitalRepository;


    @GetMapping(value = "/all")

    public ModelAndView getBloods(Model model) {

        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/blood");

        List<BloodBank> bloods = bloodBankRepository.findAll();
        BloodBank blood = new BloodBank();
        modelAndView.addObject("blood",blood);
        modelAndView.addObject("bloods",bloods);

        return modelAndView;
    }


    /** form for adding an blood */
    @GetMapping(value = "/create")
    public String add(@RequestParam("idHospital") int  idHospital, Model model){
        List<Hospital> hospitals = hospitalRepository.findAll();
        model.addAttribute("idHospital",idHospital);
        model.addAttribute("hospitalList",hospitals);
        model.addAttribute(new BloodBankHelper());
        return "dashboard/pages/admin/add-blood";
    }

    /** Add an blood */
    @PostMapping("/create")
    public String save(@ModelAttribute @Valid BloodBankHelper bloodBankHelper, Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute("hospitalList",hospitalRepository.findAll());
            model.addAttribute("idHospital",bloodBankHelper.getIdHospital());
            return "dashboard/pages/admin/add-blood";
        }else{
            Hospital hospital = hospitalRepository.getOne(bloodBankHelper.getIdHospital());
            //Hospital hospital = hospitalRepository.findByName(examenHelper.getHospitalName());
            bloodBankRepository.save(bloodBankHelper.getBloodBankInstance(hospital));
        }
        return  "redirect:/admin/blood/hospital/"+bloodBankHelper.getIdHospital();
    }


    @PostMapping("/search")
    public String searchBloodBank(BloodBank bloodBank,Model model){

        List<BloodBank> results =  bloodBankRepository.findByGroupeSanguinLike(bloodBank.getGroupeSanguin());
        model.addAttribute("results",results);
        return "dashboard/pages/admin/search-blood";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        BloodBank bloodBank = bloodBankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bloodBankRepository id:" +id));
        System.out.println("bloodBankRepository id: " + bloodBank.getId());
        Hospital hospital = hospitalRepository.getOne((Long)session.getAttribute("id"));
        bloodBankRepository.delete(bloodBank);
        model.addAttribute("bloodBankRepositorys", bloodBankRepository.findAll());
        return "redirect:/admin/hospital/"+hospital.getId() ;
    }

    /** Get all bloods of a hospital */
    @GetMapping(value = "/get/hospital/{id}")
    public String getAllBloodOfHospital(@PathVariable Long id, Model model){
        Hospital hospital =  hospitalRepository.getOne(id);
        List<BloodBank> bloods = bloodBankRepository.findByHospital(hospital);
        model.addAttribute("bloods",bloods);
        model.addAttribute("idHospital",id);
        return "dashboard/pages/admin/blood";
    }

}
