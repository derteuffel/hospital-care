package com.hospital.Controller;



import com.hospital.entities.Hospital;
import com.hospital.entities.Pharmacy;
import com.hospital.helpers.PharmacyHelper;
import com.hospital.repository.FactureRepository;
import com.hospital.repository.HospitalRepository;
import com.hospital.repository.PharmacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
@RequestMapping("/admin/pharmacy")
public class PharmacyController {


    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @GetMapping("/hospital/{id}")
    public String getPharmacyOfHospital(@PathVariable Long id, Model model){
        Optional<Hospital> hospital =  hospitalRepository.findById(id);
        List<Pharmacy> pharmacies = pharmacyRepository.findByHospital(hospital);
        model.addAttribute("lists", pharmacies);
        model.addAttribute("hospital",hospital.get());

        return "dashboard/pages/admin/pharmacy/list-pharmacy";
    }
    @GetMapping("/add/{id}")
    public String savePharmacy(Model model, @PathVariable Long id){
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("pharmacy",new Pharmacy());
        return  "dashboard/pages/admin/pharmacy/add-pharmacy";
    }

    @PostMapping("/add/{id}")
    public String addPharmacy(Model model, @PathVariable Long id, @Valid PharmacyHelper pharmacyHelper) {
        Hospital hospital = hospitalRepository.getOne(id);
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setHospital(hospital);
        pharmacy.setName(pharmacyHelper.getName());

        pharmacyRepository.save(pharmacy);
        return "redirect:/admin/pharmacy/lists/pharmacies/"+hospital.getId() ;
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pharmacy id:" +id));
        pharmacyRepository.delete(pharmacy);
        return "redirect:/admin/pharmacy/lists/pharmacies/"+pharmacy.getId()  ;
    }

    @GetMapping(value = "/all")

    public ModelAndView getPharmacies(Model model, @RequestParam(name = "page") Optional<Integer> page) {

        int currentPage = page.orElse(1);

        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/pharmacy/list-pharmacy");

        PageRequest pageable = PageRequest.of(currentPage - 1, 10);

        Page<Pharmacy> pharmacies = pharmacyRepository.findAll(pageable);

        modelAndView.addObject("currentEntries",pharmacies.getContent().size());

        int totalPages = pharmacies.getTotalPages();

        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
            modelAndView.addObject("entries",pageNumbers.size());
        }

        Pharmacy pharmacy = new Pharmacy();
        modelAndView.addObject("pharmacy",pharmacy);
        //modelAndView.addObject("activeArticleList", true);
        modelAndView.addObject("pharmacies", pharmacies.getContent());
        modelAndView.addObject("currentPage",currentPage);

        return modelAndView;
    }

    @GetMapping("/lists/pharmacies/{id}")
    public String findAllByHospital(Model model, @PathVariable Long id){
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hospital);
        model.addAttribute("pharmacies", pharmacyRepository.findAll());
        return "dashboard/pages/admin/pharmacy/list-pharmacy";
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs){

        try{
            Pharmacy pharmacy = pharmacyRepository.getOne(id);
            model.addAttribute("pharmacy",pharmacy);
            return "dashboard/pages/admin/pharmacy/updatePharmacy";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "This pharmacy seems to not exist");
            return "admin/pages/pharmacy/list-pharmacy";
        }
    }

    @PostMapping("/update/{id}")
    public String updateIncubator(Pharmacy pharmacy, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Optional<Pharmacy> pharmacy1 = pharmacyRepository.findById(id);

        if (pharmacy1.isPresent()) {
            Pharmacy pharmacy2 = pharmacy1.get();
            pharmacy2.setName(pharmacy.getName());
            pharmacyRepository.save(pharmacy2);
            redirectAttributes.addFlashAttribute("success", "The pharmacy has been updated successfully");
            return "redirect:/admin/pharmacy/lists/pharmacies/"+pharmacy.getId();
        }
        else {
            redirectAttributes.addFlashAttribute("error","There are no pharmacy with Id :" +id);
            return "redirect:/admin/pharmacy/lists/pharmacies/"+pharmacy.getId();
        }
    }


}
