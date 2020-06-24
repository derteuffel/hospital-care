package com.hospital.Controller;

import com.hospital.entities.Compte;
import com.hospital.entities.Medicament;
import com.hospital.entities.Pharmacy;
import com.hospital.helpers.MedicamentHelper;
import com.hospital.repository.MedicamentRepository;
import com.hospital.repository.PharmacyRepository;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/medicament")
public class MedicamentController {

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    private CompteService compteService;

    @GetMapping(value = "/all")

    public ModelAndView getMedicaments(Model model, @RequestParam(name = "page") Optional<Integer> page) {

        int currentPage = page.orElse(1);

        ModelAndView modelAndView = new ModelAndView("/dashboard/pages/admin/drug/drug-list");

        PageRequest pageable = PageRequest.of(currentPage - 1, 10);

        Page<Medicament> drugs = medicamentRepository.findAll(pageable);

        modelAndView.addObject("currentEntries",drugs.getContent().size());

        int totalPages = drugs.getTotalPages();

        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
            modelAndView.addObject("entries",pageNumbers.size());
        }

        Medicament medicament = new Medicament();
        modelAndView.addObject("medicament",medicament);
        //modelAndView.addObject("activeArticleList", true);
        modelAndView.addObject("drugs", drugs.getContent());
        modelAndView.addObject("currentPage",currentPage);

        return modelAndView;
    }

    @GetMapping("/lists/drugs/{id}")
    public String findAllByPharmacy(Model model, @PathVariable Long id){
        Pharmacy pharmacy = pharmacyRepository.getOne(id);
        model.addAttribute("pharmacy", pharmacy);
        model.addAttribute("drugs", medicamentRepository.findAll());
        return "dashboard/pages/admin/drug/drug-list";
    }

    @GetMapping("/add/{id}")
    public String saveDrug(Model model, @PathVariable Long id){
        Pharmacy pharmacy = pharmacyRepository.getOne(id);
        model.addAttribute("pharmacy",pharmacy);
        model.addAttribute("drug",new MedicamentHelper());
        return  "dashboard/pages/admin/drug/add-drug";
    }

    @PostMapping("/add/{id}")
    public String addDrug(Model model, @PathVariable Long id, @Valid MedicamentHelper medicamentHelper) {
        Pharmacy pharmacy = pharmacyRepository.getOne(id);
        Medicament medicament = new Medicament();
        medicament.setPharmacy(pharmacy);
        medicament.setStatus(true);
        medicament.setStockQuantity(medicamentHelper.getStockQuantity());
        medicament.setQuantity(medicamentHelper.getQuantity());
        medicament.setName(medicamentHelper.getName());
        medicament.setPricingUnit(medicamentHelper.getPricingUnit());
        medicament.setGrammage(medicamentHelper.getGrammage());
        medicament.setDrugType(medicamentHelper.getDrugType());
        medicamentRepository.save(medicament);
        return "redirect:/admin/medicament/lists/drugs/"+pharmacy.getId();
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs){

        try{
            Medicament medicament = medicamentRepository.getOne(id);
            model.addAttribute("drug",medicament);
            return "dashboard/pages/admin/drug/updateDrug";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "This drug seems to not exist");
            return "admin/pages/drug/drug-list";
        }
    }


    @PostMapping("/update/{id}")
    public String updateDrug(Medicament medicament, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Optional<Medicament> drug = medicamentRepository.findById(id);

        if (drug.isPresent()) {
            Medicament drug2 = drug.get();
            drug2.setDrugType(medicament.getDrugType());
            drug2.setGrammage(medicament.getGrammage());
            drug2.setPricingUnit(medicament.getPricingUnit());
            drug2.setQuantity(medicament.getQuantity());
            drug2.setStatus(medicament.getStatus());
            drug2.setName(medicament.getName());
            drug2.setStockQuantity(medicament.getStockQuantity());
            medicamentRepository.save(drug2);
            redirectAttributes.addFlashAttribute("success", "The drug has been updated successfully");
            return "redirect:/admin/medicament/lists/drugs/"+medicament.getId();
        }
        else {
            redirectAttributes.addFlashAttribute("error","There are no drug with Id :" +id);
            return "redirect:/admin/medicament/lists/drugs/"+medicament.getId();
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        Medicament medicament = medicamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid drug id:" +id));
        medicamentRepository.delete(medicament);
        return "redirect:/admin/medicament/lists/drugs/"+medicament.getId()  ;
    }

    @GetMapping("/active/{id}")
    public String active(@PathVariable Long id, HttpSession session){
        Medicament medicament = medicamentRepository.getOne(id);
        if (medicament.getStatus()== true){
            medicament.setStatus(false);
        }else {
            medicament.setStatus(true);
        }

        medicamentRepository.save(medicament);
        return "redirect:/admin/medicament/lists/drugs/"+medicament.getId() ;
    }
}
