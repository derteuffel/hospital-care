package com.hospital.controller;

import com.hospital.entities.*;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.RdvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/rdv")
public class RdvController {

    @Autowired
    CompteRepository compteRepository;

    @Autowired
    RdvRepository rdvRepository;

    @GetMapping("/create")
    public ModelAndView showform(){

        ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/appointment/add-rdv");
        List<Compte> comptes= compteRepository.findAll();
        List<Compte> patients = comptes.stream()
                .filter(d -> d.getRoles().stream().findFirst().get().getId() == 2)
                .collect(Collectors.toList());
        List<Compte> medecins = comptes.stream()
                .filter(d -> d.getRoles().stream().findFirst().get().getId() == 1)
                .collect(Collectors.toList());

        Rdv rdv = new Rdv();

        modelAndView.addObject("rdv", rdv);
        modelAndView.addObject("patients", patients);
        modelAndView.addObject("medecins", medecins);
        return modelAndView;
    }

    @PostMapping("/create")
    public String storeRdv(@ModelAttribute @Valid Rdv rdv, Errors errors, RedirectAttributes redirAttrs){
        if(errors.hasErrors()){
            return "dashboard/pages/admin/appointment/add-rdv";
        }
        rdvRepository.save(rdv);
        redirAttrs.addFlashAttribute("message", "Rdv added Successfully");
        return "redirect:/admin/rdv/all";
    }

    @ResponseBody
    @GetMapping("/all")
    public ModelAndView getAllRdv(){

        ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/appointment/rdv-list");
        List<Irdvjointure> rdvs = rdvRepository.findAllWithJoin();
        modelAndView.addObject("rdvs",rdvs);
        return modelAndView;
    }
    @PostMapping("/delete/{id}")
    public String deleteHospital(@PathVariable("id") Long id,RedirectAttributes redirAttrs){
        try {
            rdvRepository.deleteById(id);
            redirAttrs.addFlashAttribute("message", "Successfully deleted");
            return "redirect:/admin/rdv/all";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "Error deleting this hospital");
            return "redirect:/admin/rdv/all";
        }
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEditForm(@PathVariable("id") Long id,RedirectAttributes redirAttrs){

        ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/edit-rdv");
        List<Compte> comptes= compteRepository.findAll();
        List<Compte> patients = comptes.stream()
                .filter(d -> d.getRoles().stream().findFirst().get().getId() == 2)
                .collect(Collectors.toList());
        List<Compte> medecins = comptes.stream()
                .filter(d -> d.getRoles().stream().findFirst().get().getId() == 1)
                .collect(Collectors.toList());
        Rdv rdv = rdvRepository.findById(id).get();
        modelAndView.addObject("rdv",rdv);
        modelAndView.addObject("patients", patients);
        modelAndView.addObject("medecins", medecins);
        return modelAndView;
    }

    @PostMapping("/edit/{id}")
    public String updateHospital(@PathVariable("id") Long id, @Valid Rdv rdv, Errors errors, RedirectAttributes redirAttrs){
        if (errors.hasErrors()) {
            return "dashboard/pages/appointment/admin/edit-rdv";
        }
        rdvRepository.save(rdv);
        redirAttrs.addFlashAttribute("message", "Successfully edited");
        return "redirect:/admin/rdv/all";
    }
}
