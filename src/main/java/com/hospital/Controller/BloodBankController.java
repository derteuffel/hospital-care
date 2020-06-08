package com.hospital.Controller;


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

    public ModelAndView getIncubators(Model model, @RequestParam(name = "page") Optional<Integer> page) {

        int currentPage = page.orElse(1);

        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/blood-list");

        PageRequest pageable = PageRequest.of(currentPage - 1, 10);

        Page<BloodBank> bloodbanks = bloodBankRepository.findAll(pageable);

        modelAndView.addObject("currentEntries",bloodbanks.getContent().size());

        int totalPages = bloodbanks.getTotalPages();

        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
            modelAndView.addObject("entries",pageNumbers.size());
        }

        BloodBank bloodbank = new BloodBank();
        modelAndView.addObject("bloodbank",bloodbank);
        //modelAndView.addObject("activeArticleList", true);
        modelAndView.addObject("bloods", bloodbanks.getContent());
        modelAndView.addObject("currentPage",currentPage);

        return modelAndView;
    }


    @GetMapping("/add")
    public String form(Model model, Long id){
        List<Hospital> hospitals=hospitalRepository.findAll();
        model.addAttribute("blood", new BloodBank());
        model.addAttribute("hospitals", hospitals);
        return "dashboard/pages/admin/add-blood";
    }

    @PostMapping("/create")
    public String saveIncubator(@ModelAttribute @Valid BloodBankHelper bloodBankHelper, Long idHospital,
                                HttpSession session){

        Hospital hospital = hospitalRepository.findById(idHospital).get();
        BloodBank bloodBank = new BloodBank();
        bloodBank.setDate(bloodBankHelper.getDate());
        bloodBank.setGroupeSanguin(bloodBankHelper.getGroupeSanguin());
        bloodBank.setRhesus(bloodBankHelper.getRhesus());
        bloodBank.setStatus(bloodBankHelper.getStatus());
        bloodBank.setHospital(hospital);
        session.setAttribute("idHospital",idHospital);
        bloodBankRepository.save(bloodBank);
        return "redirect:/admin/hospital/" +session.getAttribute("idHospital") ;
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

    @GetMapping("/{id}")
    @ResponseBody
    public String getIncubator(Model model, @PathVariable Long id){
        BloodBank bloodBank = bloodBankRepository.findById(id).get();
        //model.addAttribute("bloodBank", bloodBank);
        return bloodBank.toString();

    }


}
