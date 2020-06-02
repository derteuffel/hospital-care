package com.hospital.Controller;

import com.hospital.entities.Hospital;
import com.hospital.entities.HospitalSearch;
import com.hospital.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    public String addHostpital(Hospital hospital, Model model){

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

    public ModelAndView getHospitals(Model model, @RequestParam(name = "page") Optional<Integer> page) {

        int currentPage = page.orElse(1);

        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/hospital-list");

        PageRequest pageable = PageRequest.of(currentPage - 1, 10);

        Page<Hospital> hospitals = hospitalRepository.findAll(pageable);

        modelAndView.addObject("currentEntries",hospitals.getContent().size());

        int totalPages = hospitals.getTotalPages();

        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
            modelAndView.addObject("entries",pageNumbers.size());
        }

        Hospital hospital = new Hospital();
        modelAndView.addObject("hospital",hospital);
        modelAndView.addObject("activeArticleList", true);
        modelAndView.addObject("articleList", hospitals.getContent());
        modelAndView.addObject("currentPage",currentPage);

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
    public String addHospital(@Valid Hospital hospital, BindingResult bindingResult,Model model){
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            //modelAndView.setViewName("dashboard/pages/admin/add-hospital");
            //model.addAttribute("hospital",hospital);
            System.out.println("erreur");
            model.addAttribute("hospital",hospital);
            System.out.println(hospital.toString()) ;
            return "dashboard/pages/admin/add-hospital";
        }
        modelAndView.setViewName("/dashboard/pages/admin/hospital-list");
        return "redirect:/admin/hospital/all";
    }

    @PostMapping("/search")
    public String searchHospital(Hospital hospital,Model model){

        List<Hospital> results = hospitalRepository.findBySearch(hospital.getName());
        model.addAttribute("results",results);
        return "dashboard/pages/admin/search-hospital";
    }
    
}
