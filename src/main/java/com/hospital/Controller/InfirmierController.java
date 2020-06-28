package com.hospital.Controller;

import com.hospital.entities.BloodBank;
import com.hospital.entities.Compte;
import com.hospital.entities.Hospital;
import com.hospital.helpers.BloodBankHelper;
import com.hospital.repository.*;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/infirmier")
public class InfirmierController {
    @Autowired
    private BloodBankRepository bloodBankRepository;

    @Autowired
    private IncubatorRepository incubatorRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private DosMedicalRepository dos;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private CompteService compteService;


    @GetMapping("/bloods/lists/{id}")
    public String findAllByHospital(Model model, HttpServletRequest request, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hospital);
        model.addAttribute("compte",compte);
        model.addAttribute("lists", bloodBankRepository.findAllByHospital(hospital));
        return "dashboard/pages/infirmier/hospital/blood/lists";
    }

    @GetMapping("/bloods/add/{id}")
    public String saveBlood(Model model, @PathVariable Long id, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("compte",compte);
        model.addAttribute("blood",new BloodBankHelper());
        return  "dashboard/pages/infirmier/hospital/blood/add";
    }

    @PostMapping("/bloods/add/{id}")
    public String addBlood(RedirectAttributes redirectAttributes, @PathVariable Long id, @Valid BloodBankHelper bloodBankHelper, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        BloodBank bloodBank = bloodBankRepository.findByGroupeSanguinAndRhesus(bloodBankHelper.getGroupeSanguin(),bloodBankHelper.getRhesus());
        BloodBank blood = new BloodBank();
        if (bloodBank != null){
            bloodBank.setQuantity(bloodBank.getQuantity() + bloodBankHelper.getQuantity());
            bloodBankRepository.save(bloodBank);
        }else {
            blood.setHospital(hospital);
            blood.setDate(bloodBankHelper.getDate());
            blood.setRhesus(bloodBankHelper.getRhesus());
            blood.setGroupeSanguin(bloodBankHelper.getGroupeSanguin());
            blood.setStatus(true);
            blood.setQuantity(bloodBankHelper.getQuantity());
            bloodBankRepository.save(blood);
        }
        redirectAttributes.addFlashAttribute("success", "Your Blood item saved successfully");
        return "redirect:/infirmier/bloods/lists/"+hospital.getId();
    }

    @GetMapping("/bloods/update/{id}")
    public String updateBlood(@PathVariable("id") Long id,Model model){

            BloodBank blood = bloodBankRepository.getOne(id);
            model.addAttribute("blood",blood);
            return "dashboard/pages/infirmier/hospital/blood/update";
    }

    @PostMapping("/update/{id}")
    public String updateBlood( BloodBank blood, @PathVariable Long id, RedirectAttributes redirectAttributes, Long hospitalId){
        Optional<BloodBank> blood1 = bloodBankRepository.findById(id);
        Hospital hospital = hospitalRepository.getOne(hospitalId);

        if (blood1.isPresent()) {
            BloodBank blood2 = blood1.get();
            blood2.setGroupeSanguin(blood.getGroupeSanguin());
            blood2.setRhesus(blood.getRhesus());
            blood2.setDate(blood.getDate());
            blood2.setQuantity(blood.getQuantity());
            bloodBankRepository.save(blood2);
            redirectAttributes.addFlashAttribute("success", "The blood has been updated successfully");
            return "redirect:/infirmier/blood/lists/"+hospital.getId();
        }
        else {
            redirectAttributes.addFlashAttribute("error","There are no blood with Id :" +id);
            return "redirect:/infirmier/blood/lists/"+hospital.getId();
        }
    }

    @GetMapping("/bloods/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        BloodBank bloodBank = bloodBankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bloodBankRepository id:" +id));
        System.out.println("bloodBankRepository id: " + bloodBank.getId());
        bloodBankRepository.delete(bloodBank);
        return "redirect:/infirmier/blood/lists/"+bloodBank.getHospital().getId();
    }
}
