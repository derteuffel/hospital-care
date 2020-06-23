package com.hospital.Controller;



import com.hospital.entities.Hospital;
import com.hospital.entities.Pharmacy;
import com.hospital.helpers.PharmacyHelper;
import com.hospital.repository.FactureRepository;
import com.hospital.repository.HospitalRepository;
import com.hospital.repository.PharmacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/pharmacy")
public class PharmacyController {


    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

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
        pharmacy.setDrug(pharmacyHelper.getDrug());
        pharmacy.setStatus(true);
        pharmacy.setDrugType(pharmacyHelper.getDrugType());
        pharmacy.setGrammage(pharmacyHelper.getGrammage());
        pharmacy.setName(pharmacyHelper.getName());
        pharmacy.setQuantity(pharmacyHelper.getQuantity());
        pharmacy.setPricingUnit(pharmacyHelper.getPricingUnit());
        pharmacy.setStockQuantity(pharmacyHelper.getStockQuantity());
        pharmacyRepository.save(pharmacy);
        return "redirect:/admin/pharmacy/lists/pharmacies/"+hospital.getId() ;
    }
}
