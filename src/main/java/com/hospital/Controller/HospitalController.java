package com.hospital.Controller;

import com.hospital.entities.Hospital;
import com.hospital.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

        PageRequest pageable = PageRequest.of(currentPage - 1, 4);

        Page<Hospital> hospitals = hospitalRepository.findAll(pageable);

        int totalPages = hospitals.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        modelAndView.addObject("activeArticleList", true);
        modelAndView.addObject("articleList", hospitals.getContent());
        modelAndView.addObject("currentPage",currentPage);

        return modelAndView;
    }
    
}
