package com.hospital.controller;


import com.hospital.entities.*;
import com.hospital.helpers.IncubatorHelper;
import com.hospital.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/incubator")
public class IncubatorController {

    @Autowired
    private IncubatorRepository incubatorRepository;

    @Autowired
    private HospitalRepository hospitalRepository;



    @GetMapping(value = "/all")

    public ModelAndView getIncubators(Model model, @RequestParam(name = "page") Optional<Integer> page) {

        int currentPage = page.orElse(1);

        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/incubator/incubator-list");

        PageRequest pageable = PageRequest.of(currentPage - 1, 10);

        Page<Incubator> incubators = incubatorRepository.findAll(pageable);

        modelAndView.addObject("currentEntries",incubators.getContent().size());

        int totalPages = incubators.getTotalPages();

        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
            modelAndView.addObject("entries",pageNumbers.size());
        }

        Incubator incubator = new Incubator();
        modelAndView.addObject("incubator",incubator);
        //modelAndView.addObject("activeArticleList", true);
        modelAndView.addObject("incubators", incubators.getContent());
        modelAndView.addObject("currentPage",currentPage);

        return modelAndView;
    }

    @GetMapping("/lists/incubators/{id}")
    public String findAllByHospital(Model model, @PathVariable Long id){
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hospital);
        model.addAttribute("incubators", incubatorRepository.findAll());
        return "dashboard/pages/admin/incubator/incubator-list";
    }


    @GetMapping("/add/{id}")
    public String saveIncubator(Model model, @PathVariable Long id){
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("incubator",new IncubatorHelper());
        return  "dashboard/pages/admin/incubator/add-incubator";
    }

    @PostMapping("/add/{id}")
    public String addIncubator(Model model, @PathVariable Long id, @Valid IncubatorHelper incubatorHelper) {
        Hospital hospital = hospitalRepository.getOne(id);
        Incubator incubator = new Incubator();
        incubator.setHospital(hospital);
        incubator.setDateObtained(incubatorHelper.getDateObtained());
        incubator.setStatus(incubatorHelper.getStatus());
        incubator.setNumber(incubatorHelper.getNumber());
        incubatorRepository.save(incubator);
        return "dashboard/pages/admin/incubator/incubator-list";
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs){

        try{
            Incubator incubator = incubatorRepository.getOne(id);
            model.addAttribute("incubator",incubator);
            return "dashboard/pages/admin/incubator/updateIncubator";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "This hospital seems to not exist");
            return "admin/pages/incubator/incubator-list";
        }
    }

    @PostMapping("/update/{id}")
    public String updateIncubator(Incubator incubator, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Optional<Incubator> incubator1 = incubatorRepository.findById(id);


        if (incubator1.isPresent()) {
            Incubator incubator2 = incubator1.get();
            incubator2.setNumber(incubator.getNumber());
            incubator2.setStatus(incubator.getStatus());
            incubator2.setDateObtained(incubator.getDateObtained());

            incubatorRepository.save(incubator2);
            redirectAttributes.addFlashAttribute("success", "The incubator has been updated successfully");
            return "admin/pages/incubator/updateIncubator";
        }
        else {
            redirectAttributes.addFlashAttribute("error","There are no incubator with Id :" +id);
            return "admin/pages/incubator/incubator-list";
        }
    }





    @PostMapping("/search")
    public String searchIncubator(Incubator incubator,Model model){

        List<Incubator> results =  incubatorRepository.findByNumberLike(incubator.getNumber());
        model.addAttribute("results",results);
        return "dashboard/pages/admin/incubator/search-incubator";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        Incubator incubator = incubatorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid incubator id:" +id));
        System.out.println("incubator id: " + incubator.getId());
        Hospital hospital = hospitalRepository.getOne((Long)session.getAttribute("id"));
        incubatorRepository.delete(incubator);
        model.addAttribute("incubators", incubatorRepository.findAll());
        return "redirect:/admin/hospital/"+hospital.getId() ;
    }

    /** Get all incubators of a hospital */
    @GetMapping(value = "/hospital/{id}")
    public String getAllIncubatorOfHospital(@PathVariable Long id, Model model)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             {
        Hospital hospital =  hospitalRepository.getOne(id);
        List<Incubator> incubators = incubatorRepository.findByHospital(hospital);
        model.addAttribute("incubators",incubators);
        model.addAttribute("idHospital",id);
        return "dashboard/pages/admin/incubator/incubator-list";
    }

                                }
