package com.hospital.Controller;



import com.hospital.entities.Hospital;
import com.hospital.entities.Medicament;
import com.hospital.entities.Pharmacy;
import com.hospital.helpers.MedicamentHelper;
import com.hospital.helpers.PharmacyHelper;
import com.hospital.repository.FactureRepository;
import com.hospital.repository.HospitalRepository;
import com.hospital.repository.MedicamentRepository;
import com.hospital.repository.PharmacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/pharmacie")
public class PharmacyController {


    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private MedicamentRepository medicamentRepository;

    @GetMapping("/login")
    public String login(Model model){
        return "dashboard/pages/admin/pharmacy/login";
    }


    /*@GetMapping("/hospital/{id}")
    public String getPharmacyOfHospital(@PathVariable Long id, Model model){
        Optional<Hospital> hospital =  hospitalRepository.findById(id);
        List<Pharmacy> pharmacies = pharmacyRepository.findByHospital(hospital);
        model.addAttribute("lists", pharmacies);
        model.addAttribute("hospital",hospital.get());

        return "dashboard/pages/admin/pharmacy/list-pharmacy";
    }
*/


    @GetMapping("/detail/{id}")
    public String detailPharmacy(@PathVariable Long id, Model model){
        Pharmacy pharmacy = pharmacyRepository.getOne(id);
        Hospital hospital = pharmacy.getHospital();
        model.addAttribute("pharmacy", pharmacy);
        model.addAttribute("hospital",hospital);
        return "dashboard/pages/admin/pharmacy/detail";
    }

    @GetMapping("/medicament/lists/{id}")
    public String findAllByPharmacy(Model model, @PathVariable Long id){
        Pharmacy pharmacy = pharmacyRepository.getOne(id);

        List<Medicament> medicaments = medicamentRepository.findAllByPharmacy_Id(pharmacy.getId(), Sort.by(Sort.Direction.DESC,"id"));
        for (Medicament medicament : medicaments){
            if (medicament.getStockQuantity() == 0){
                medicament.setStatus(false);
                medicamentRepository.save(medicament);
            }
        }
        model.addAttribute("pharmacy", pharmacy);
        model.addAttribute("lists", medicaments);
        return "dashboard/pages/admin/pharmacy/medicament/lists";
    }

    @GetMapping("/medicament/add/{id}")
    public String addMedicament(@PathVariable Long id, Model model){
        Pharmacy pharmacy = pharmacyRepository.getOne(id);
        model.addAttribute("pharmacy",pharmacy);
        model.addAttribute("medicament",new MedicamentHelper());
        return "dashboard/pages/admin/pharmacy/medicament/add-medicament";
    }

    @PostMapping("/medicament/add/{id}")
    public String saveMedicament(@PathVariable Long id, @Valid MedicamentHelper medicamentHelper,RedirectAttributes redirectAttributes){
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
        redirectAttributes.addAttribute("success","You saved successfully your drugs");
        return "redirect:/pharmacy/medicament/lists/"+pharmacy.getId();
    }

    @GetMapping("/medicament/update/{id}")
    public String editMedicament(@PathVariable Long id,Model model,RedirectAttributes redirAttrs){
            Medicament medicament = medicamentRepository.getOne(id);
            Pharmacy pharmacy = medicament.getPharmacy();
            model.addAttribute("medicament",medicament);
            model.addAttribute("pharmacy",pharmacy);

            return "dashboard/pages/admin/pharmacy/medicament/update";

    }


    @PostMapping("/medicament/update/{id}")
    public String updateDrug(Medicament medicament, @PathVariable Long id, RedirectAttributes redirectAttributes, Long pharmacyId){

        Pharmacy pharmacy = pharmacyRepository.getOne(pharmacyId);
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
            drug2.setPharmacy(pharmacy);
            medicamentRepository.save(drug2);
            redirectAttributes.addFlashAttribute("success", "The drug has been updated successfully");
            return "redirect:/pharmacy/medicament/lists/"+pharmacy.getId();
        }
        else {
            redirectAttributes.addFlashAttribute("error","There are no drug with Id :" +id);
            return "redirect:/pharmacy/medicament/lists/"+pharmacy.getId();
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        Medicament medicament = medicamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid drug id:" +id));
        Pharmacy pharmacy = medicament.getPharmacy();
        medicamentRepository.delete(medicament);
        return "redirect:/pharmacy/medicament/lists/"+pharmacy.getId()  ;
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs){

            Pharmacy pharmacy = pharmacyRepository.getOne(id);
            model.addAttribute("pharmacy",pharmacy);
            return "dashboard/pages/admin/pharmacy/updatePharmacy";
    }

    @PostMapping("/update/{id}")
    public String updatePharmacy(Pharmacy pharmacy, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Optional<Pharmacy> pharmacy1 = pharmacyRepository.findById(id);
            Pharmacy pharmacy2 = pharmacy1.get();
            pharmacy2.setName(pharmacy.getName());
            pharmacyRepository.save(pharmacy2);
            redirectAttributes.addFlashAttribute("success", "The pharmacy has been updated successfully");
            return "redirect:/pharmacie/detail/"+pharmacy.getId();

    }

    @GetMapping("/medicament/active/{id}")
    public String active(@PathVariable Long id, HttpSession session){
        Medicament medicament = medicamentRepository.getOne(id);
        if (medicament.getStatus()== true){
            medicament.setStatus(false);
        }else {
            medicament.setStatus(true);
        }

        medicamentRepository.save(medicament);
        return "redirect:/pharmacie/medicament/lists/"+medicament.getId() ;
    }
}
