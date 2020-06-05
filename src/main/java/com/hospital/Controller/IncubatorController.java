package com.hospital.Controller;


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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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


    @GetMapping("/add")
    public String form(Model model, Long idHospital){
        List<Hospital> hospitals=hospitalRepository.findAll();
        model.addAttribute("incubator", new Incubator());
        model.addAttribute("idHospital",idHospital);
        model.addAttribute("hospitals", hospitals);
        return "dashboard/pages/admin/add-incubator";
    }

    @PostMapping("/create")
    public String saveIncubator(@Valid IncubatorHelper incubatorHelper, Long idHospital,
                                HttpSession session){

        Hospital hospital = hospitalRepository.getOne(idHospital);
        Incubator incubator = new Incubator();
        incubator.setStatus(incubatorHelper.getStatus());
        incubator.setQuantity(incubatorHelper.getQuantity());
        incubator.setNumber(incubatorHelper.getNumber());
        incubator.setDateObtained(incubatorHelper.getDateObtained());
        incubator.setHospital(hospital);
        session.setAttribute("idHospital",idHospital);
        incubatorRepository.save(incubator);
        return "redirect:/admin/hospital/" +session.getAttribute("idHospital") ;
    }

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

    @GetMapping("/{id}")
    @ResponseBody
    public String getIncubator(Model model, @PathVariable Long id){
        Incubator incubator = incubatorRepository.findById(id).get();
        //model.addAttribute("incubator", incubator);
        return incubator.toString();

    }



}
