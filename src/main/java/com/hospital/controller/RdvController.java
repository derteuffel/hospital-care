package com.hospital.controller;

import com.hospital.entities.*;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.DosMedicalRepository;
import com.hospital.repository.PersonnelRepository;
import com.hospital.repository.RdvRepository;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
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

    @Autowired
    private CompteService compteService;

    @Autowired
    private DosMedicalRepository dosMedical;

    @Autowired
    private PersonnelRepository per;


    @GetMapping("/add")
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
//        modelAndView.addObject("appointments", rdvRepository.findAll());
        modelAndView.addObject("patients", patients);
        modelAndView.addObject("medecins", medecins);
        return modelAndView;
    }

    @PostMapping("/add")
    public String storeRdv(@ModelAttribute @Valid Rdv rdv, Errors errors, RedirectAttributes redirAttrs){
        if(errors.hasErrors()){
            return "dashboard/pages/admin/appointment/add-rdv";
        }
        rdv.setStatus(false);
        rdvRepository.save(rdv);
        redirAttrs.addFlashAttribute("message", "Rdv added Successfully");
        return "redirect:/admin/rdv/all";
    }

/*
      @GetMapping("/add")
      public String form(Model model){
          List<Personnel> personnels = per.findAllByFunction("DOCTOR");
          List<DosMedical>dos = dosMedical.findAll();
          model.addAttribute("dos", dos);
          model.addAttribute("staffs", personnels);
          model.addAttribute("rdv", new Rdv());
          return "dashboard/pages/admin/appointment/add-rdv";
      }

        @PostMapping("/add")
        public String save(@Valid Rdv rdv, String code, Long id){

            Personnel personnel = per.getOne(id);
            DosMedical dos = dosMedical.findByCode(code);
            rdv.setStatus(false);
            rdv.setDosMedical(dos);
            rdv.setPersonnel(personnel);
            rdvRepository.save(rdv);
            return "redirect:/admin/rdv/all";
        }
*/


        @ResponseBody
        @GetMapping("/account")
        public ModelAndView getAllRdv(HttpServletRequest request){

            Principal principal =  request.getUserPrincipal();
            Compte compte = compteService.findByUsername(principal.getName());

            ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/rdv-list");
            List<Rdv> rdvs = rdvRepository.findAllByComptes_Id(compte.getId(),Sort.by(Sort.Direction.DESC, "id"));
            modelAndView.addObject("appointments",rdvs);
            return modelAndView;
        }

    @ResponseBody
    @GetMapping("/all")
    public ModelAndView getAllRdvs(HttpServletRequest request){

        Principal principal =  request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());

        ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/appointment/rdv-list");
        List<Rdv> rdvs = rdvRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        modelAndView.addObject("appointments",rdvs);
        return modelAndView;
    }
        @PostMapping("/delete/{id}")
        public String delete(@PathVariable("id") Long id,RedirectAttributes redirAttrs){
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

            ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/appointment/edit-rdv");
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
                return "dashboard/pages/admin/appointment/edit-rdv";
            }
            rdvRepository.save(rdv);
            redirAttrs.addFlashAttribute("message", "Successfully edited");
            return "redirect:/admin/rdv/all";
        }

        @GetMapping("/active/{id}")
        public String active(@PathVariable Long id, HttpSession session){
            Rdv rdv = rdvRepository.getOne(id);
            if (rdv.getStatus() != null) {
                if (rdv.getStatus() == true) {
                    rdv.setStatus(false);
                } else {
                    rdv.setStatus(true);
                }
                rdvRepository.save(rdv);
            }else {
                rdv.setStatus(false);
                rdvRepository.save(rdv);
                return "redirect:/admin/rdv/active/"+rdv.getId();
            }


            return "redirect:/admin/rdv/all" ;
        }

        @GetMapping("/active")
        public String findAllStatusActive(Model model,HttpServletRequest request){
            Principal principal =  request.getUserPrincipal();
            Compte compte = compteService.findByUsername(principal.getName());
            List<Rdv> lists = rdvRepository.findAllByStatusAndComptes_Id(true,compte.getId(),Sort.by(Sort.Direction.DESC,"id"));
            model.addAttribute("appointments", lists);
            return "dashboard/pages/admin/appointment/rdv-actif";
        }

        @GetMapping("/inactive")
        public String findAllStatusInacctive(Model model,HttpServletRequest request){

            Principal principal =  request.getUserPrincipal();
            Compte compte = compteService.findByUsername(principal.getName());
            List<Rdv> lists = rdvRepository.findAllByStatusAndComptes_Id(false,compte.getId(),Sort.by(Sort.Direction.DESC,"id"));
            model.addAttribute("appointments", lists);
            return "dashboard/pages/admin/appointment/rdv-inactif";
        }

}
