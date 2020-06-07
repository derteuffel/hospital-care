package com.hospital.controller;

import com.hospital.entities.Hospital;
import com.hospital.entities.HospitalSearch;
import com.hospital.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/hospital")
public class HospitalController {
    @Autowired
    private HospitalRepository hospitalRepository;

    @PostMapping("/create")
    public String addHostpital(Hospital hospital){

        Hospital hospitalAdded = hospitalRepository.save(hospital);

        return hospitalAdded.toString();
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public String getHospital(@PathVariable Long id) {

        Optional<Hospital> hospital = hospitalRepository.findById(id);

        return hospital.toString();
    }

    @GetMapping(value = "/all")

    public ModelAndView getHospitals(Model model) {

        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/hospital-list");

        List<Hospital> hospitals = hospitalRepository.findAll();
        Hospital hospital = new Hospital();
        modelAndView.addObject("hospital",hospital);
        modelAndView.addObject("hospitals",hospitals);

        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView showForm() {
        Hospital hospital = new Hospital();
        ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/add-hospital");
        modelAndView.addObject("hospital", hospital);

        return modelAndView;
    }

    @PostMapping("/add")
    public String addHospital(@Valid Hospital hospital, BindingResult bindingResult, Model model, RedirectAttributes redirAttrs){

        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            model.addAttribute("hospital",hospital);
            System.out.println(hospital.toString()) ;
            return "dashboard/pages/admin/add-hospital";
        }
        hospitalRepository.save(hospital);
        modelAndView.setViewName("/dashboard/pages/admin/hospital-list");
        redirAttrs.addFlashAttribute("message", "Successfully added hospital " + hospital.getName());
        return "redirect:/admin/hospital/all";
    }

    @PostMapping("/delete/{id}")
    public String deleteHospital(@PathVariable("id") Long id,RedirectAttributes redirAttrs){
        try {
            hospitalRepository.deleteById(id);
            redirAttrs.addFlashAttribute("message", "Successfully deleted");
            return "redirect:/admin/hospital/all";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "Error deleting this hospital");
           return "redirect:/admin/hospital/all";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs){

        try{
            Hospital hospital = hospitalRepository.getOne(id);
            model.addAttribute("hospital",hospital);
            return "dashboard/pages/admin/edit-hospital";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "This hospital seems to not exist");
            return "redirect:/admin/hospital/all";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateHospital(@PathVariable("id") Long id, @Valid Hospital hospital, BindingResult bindingResult, Errors errors, Model model, RedirectAttributes redirAttrs){
        if (bindingResult.hasErrors()) {
            return "dashboard/pages/admin/edit-hospital";
        }
        hospitalRepository.save(hospital);
        redirAttrs.addFlashAttribute("message", "Successfully edited");
        return "redirect:/admin/hospital/all";
    }
    
}
