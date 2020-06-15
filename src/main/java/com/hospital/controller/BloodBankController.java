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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/blood/blood");

        List<BloodBank> bloods = bloodBankRepository.findAll();
        BloodBank blood = new BloodBank();
        modelAndView.addObject("blood",blood);
        modelAndView.addObject("bloods",bloods);

        return modelAndView;
    }

    @GetMapping("/lists/bloods/{id}")
    public String findAllByHospital(Model model, @PathVariable Long id){
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hospital);
        model.addAttribute("bloods", bloodBankRepository.findAll());
        return "dashboard/pages/admin/blood/blood";
    }


    @GetMapping("/add/{id}")
    public String saveBlood(Model model, @PathVariable Long id){
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("blood",new BloodBankHelper());
        return  "dashboard/pages/admin/blood/add-blood";
    }

    @PostMapping("/add/{id}")
    public String addBlood(Model model, @PathVariable Long id, @Valid BloodBankHelper bloodBankHelper) {
        Hospital hospital = hospitalRepository.getOne(id);
        BloodBank blood = new BloodBank();
        blood.setHospital(hospital);
        blood.setDate(bloodBankHelper.getDate());
        blood.setRhesus(bloodBankHelper.getRhesus());
        blood.setGroupeSanguin(bloodBankHelper.getGroupeSanguin());
        blood.setStatus(true);
        bloodBankRepository.save(blood);
        return "redirect:/admin/blood/lists/bloods/"+hospital.getId();
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs){

        try{
            BloodBank blood = bloodBankRepository.getOne(id);
            model.addAttribute("blood",blood);
            return "dashboard/pages/admin/blood/update";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "This hospital seems to not exist");

            return "admin/pages/blood/blood";
        }
    }

    @PostMapping("/update/{id}")
    public String updateBlood( BloodBank blood, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Optional<BloodBank> blood1 = bloodBankRepository.findById(id);


        if (blood1.isPresent()) {
            BloodBank blood2 = blood1.get();
            blood2.setGroupeSanguin(blood.getGroupeSanguin());
            blood2.setRhesus(blood.getRhesus());
            blood2.setDate(blood.getDate());
            bloodBankRepository.save(blood2);
            redirectAttributes.addFlashAttribute("success", "The blood has been updated successfully");
            return "admin/pages/blood/update";
        }
        else {
            redirectAttributes.addFlashAttribute("error","There are no blood with Id :" +id);
            return "redirect:/admin/blood/lists/bloods/"+blood.getId();
        }
    }



    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        BloodBank bloodBank = bloodBankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bloodBankRepository id:" +id));
        System.out.println("bloodBankRepository id: " + bloodBank.getId());
        bloodBankRepository.delete(bloodBank);
        return "redirect:/admin/blood/lists/bloods/"+bloodBank.getId();
    }

    /** Get all bloods of a hospital */
    @GetMapping(value = "/get/hospital/{id}")
    public String getAllBloodOfHospital(@PathVariable Long id, Model model){
        Hospital hospital =  hospitalRepository.getOne(id);
        List<BloodBank> bloods = bloodBankRepository.findByHospital(hospital);
        model.addAttribute("bloods",bloods);
        model.addAttribute("idHospital",id);
        return "dashboard/pages/admin/blood/blood";
    }

    @GetMapping("/active/{id}")
    public String active(@PathVariable Long id, HttpSession session){
        BloodBank blood = bloodBankRepository.getOne(id);
        if (blood.getStatus()== true){
            blood.setStatus(false);
        }else {
            blood.setStatus(true);
        }

        bloodBankRepository.save(blood);
        return "redirect:/admin/blood/lists/bloods/"+blood.getId() ;
    }

}
