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

        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/incubator-list");

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


    /** form for adding an exam */
    @GetMapping(value = "/create")
    public String add(@RequestParam("idHospital") int  idHospital, Model model){
        List<Hospital> hospitals = hospitalRepository.findAll();
        model.addAttribute("idHospital",idHospital);
        model.addAttribute("hospitalList",hospitals);
        model.addAttribute(new IncubatorHelper());
        return "dashboard/pages/admin/add-incubator";
    }

    /** Add an incubator */
    @PostMapping("/create")
    public String save(@ModelAttribute @Valid IncubatorHelper incubatorHelper, Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute("hospitalList",hospitalRepository.findAll());
            model.addAttribute("idHospital",incubatorHelper.getIdHospital());
            return "dashboard/pages/admin/add-incubator";
        }else{
            Hospital hospital = hospitalRepository.getOne(incubatorHelper.getIdHospital());
            //Hospital hospital = hospitalRepository.findByName(examenHelper.getHospitalName());
            incubatorRepository.save(incubatorHelper.getIncubatorInstance(hospital));
        }
        return  "redirect:/admin/incubator/hospital/"+incubatorHelper.getIdHospital();
    }

    /** form for updating an incubator */
    @GetMapping(value = "/update/{idIncubator}")
    public String updateIncubator(@PathVariable Long idIncubator, @RequestParam("idHospital") Long idHospital, Model model){
        List<Hospital> hospitals = hospitalRepository.findAll();
        model.addAttribute("idHospital",idHospital);
        model.addAttribute("hospitalList",hospitals);
        model.addAttribute("incubatorHelper", IncubatorHelper.getIncubatorHelperInstance(incubatorRepository.getOne(idIncubator)));

        return "dashboard/pages/admin/updateIncubator";
    }

    /** Update an incubator */
    @PostMapping(value = "/update/{idIncubator}")
    public String update(@PathVariable Long idIncubator, @ModelAttribute @Valid IncubatorHelper incubatorHelper, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("hospitalList", hospitalRepository.findAll());
            model.addAttribute("idHospital", incubatorHelper.getIdHospital());
            return "dashboard/pages/admin/updateIncubator";
        } else {
            Hospital hospital = hospitalRepository.getOne(incubatorHelper.getIdHospital());
            Incubator incubator = incubatorRepository.getOne(idIncubator);
            Incubator newExam = incubatorHelper.getIncubatorInstance(hospital);
            newExam.setId(incubator.getId());
            newExam.setHospital(hospital);
            incubatorRepository.save(newExam);
        }

        model.addAttribute("success","Operation successfully completed");
        System.out.println(model.getAttribute("success"));
        return  "redirect:/admin/incubator/hospital/"+incubatorHelper.getIdHospital();
    }

  /*  @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs){

        try{
            Incubator incubator = incubatorRepository.getOne(id);
            model.addAttribute("incubator",incubator);
            return "dashboard/pages/admin/UpdateIncubator";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "This incubator seems to not exist");
            return "redirect:/admin/incubator/all";
        }
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Long id, @Valid Incubator incubator, BindingResult bindingResult, Errors errors, Model model, RedirectAttributes redirAttrs){
        if (bindingResult.hasErrors()) {
            return "dashboard/pages/admin/UpdateIncubator";
        }
        incubatorRepository.save(incubator);
        redirAttrs.addFlashAttribute("message", "Successfully edited");
        return "redirect:/admin/incubator/all";
    }*/

    @PostMapping("/search")
    public String searchIncubator(Incubator incubator,Model model){

        List<Incubator> results =  incubatorRepository.findByNumberLike(incubator.getNumber());
        model.addAttribute("results",results);
        return "dashboard/pages/admin/search-incubator";
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
    public String getAllIncubatorOfHospital(@PathVariable Long id, Model model){
        Hospital hospital =  hospitalRepository.getOne(id);
        List<Incubator> incubators = incubatorRepository.findByHospital(hospital);
        model.addAttribute("incubators",incubators);
        model.addAttribute("idHospital",id);
        return "dashboard/pages/admin/incubator";
    }

}
