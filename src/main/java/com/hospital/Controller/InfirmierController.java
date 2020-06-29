package com.hospital.Controller;

import com.hospital.entities.BloodBank;
import com.hospital.entities.Compte;
import com.hospital.entities.Hospital;
import com.hospital.entities.Incubator;
import com.hospital.helpers.BloodBankHelper;
import com.hospital.helpers.IncubatorHelper;
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


    @GetMapping("/hospital/blood/lists/{id}")
    public String findAllByHospital(Model model, HttpServletRequest request, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hospital);
        model.addAttribute("compte",compte);
        model.addAttribute("lists", bloodBankRepository.findAllByHospital(hospital));
        return "dashboard/pages/admin/infirmier/hospital/blood/lists";
    }

    @GetMapping("/bloods/add/{id}")
    public String saveBlood(Model model, @PathVariable Long id, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("compte",compte);
        model.addAttribute("blood",new BloodBankHelper());
        return  "dashboard/pages/admin/infirmier/hospital/blood/add";
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
        return "redirect:/infirmier/hospital/blood/lists/"+hospital.getId();
    }

    @GetMapping("/bloods/update/{id}")
    public String updateBlood(@PathVariable("id") Long id,Model model){

            BloodBank blood = bloodBankRepository.getOne(id);
            model.addAttribute("blood",blood);
            return "dashboard/pages/admin/infirmier/hospital/blood/update";
    }

    @PostMapping("/bloods/update/{id}")
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

    @GetMapping("/blood/active/{id}")
    public String active(@PathVariable Long id, HttpSession session){
        BloodBank blood = bloodBankRepository.getOne(id);
        if (blood.getStatus()== true){
            blood.setStatus(false);
        }else {
            blood.setStatus(true);
        }

        bloodBankRepository.save(blood);
        return "redirect:/infirmier/bloods/lists/"+blood.getId() ;
    }


    @GetMapping("/hospital/incubator/lists/{id}")
    public String findAllIncubatorByHospital(Model model, HttpServletRequest request, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hospital);
        model.addAttribute("compte",compte);
        model.addAttribute("lists", incubatorRepository.findAllByHospital(hospital));
        return "dashboard/pages/admin/infirmier/hospital/incubator/lists";
    }

    @GetMapping("/incubator/add/{id}")
    public String saveIncubator(Model model, @PathVariable Long id, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("compte",compte);
        model.addAttribute("incubator",new IncubatorHelper());
        return  "dashboard/pages/admin/infirmier/hospital/incubator/add";
    }

    @PostMapping("/incubator/add/{id}")
    public String addIncubator(RedirectAttributes redirectAttributes, @PathVariable Long id, @Valid IncubatorHelper incubatorHelper, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hospital hospital = hospitalRepository.getOne(id);
//        Incubator incubator = incubatorRepository.findByNumberLike();
        Incubator incubator = new Incubator();
        if (incubator != null){
            incubator.setQuantity(incubator.getQuantity() + incubatorHelper.getQuantity());
            incubatorRepository.save(incubator);
        }else {
            incubator.setHospital(hospital);
           incubator.setDateObtained(incubatorHelper.getDateObtained());
            incubator.setNumber(incubatorHelper.getNumber());
            incubator.setState(true);
            incubator.setStatus(incubatorHelper.getStatus());
            incubator.setType(incubatorHelper.getType());
            incubatorRepository.save(incubator);
        }
        redirectAttributes.addFlashAttribute("success", "Your Incubator item saved successfully");
        return "redirect:/infirmier/hospital/incubator/lists/"+hospital.getId();
    }

    @GetMapping("/incubator/update/{id}")
    public String updateIncubator(@PathVariable("id") Long id,Model model){

        Incubator incubator = incubatorRepository.getOne(id);
        model.addAttribute("incubator",incubator);
        return "dashboard/pages/admin/infirmier/hospital/incubator/update";
    }

    @PostMapping("/incubator/update/{id}")
    public String updateIncubator( Incubator incubator, @PathVariable Long id, RedirectAttributes redirectAttributes, Long hospitalId){
        Optional<Incubator> incubator1 = incubatorRepository.findById(id);
        Hospital hospital = hospitalRepository.getOne(hospitalId);

        if (incubator1.isPresent()) {
            Incubator incubator2 = incubator1.get();
            incubator2.setType(incubator.getType());
            incubator2.setStatus(incubator.getStatus());
            incubator2.setNumber(incubator.getNumber());
            incubator2.setQuantity(incubator.getQuantity());
            incubator2.setDateObtained(incubator.getDateObtained());
            incubatorRepository.save(incubator2);
            redirectAttributes.addFlashAttribute("success", "The incubator has been updated successfully");
            return "redirect:/infirmier/incubator/lists/"+hospital.getId();
        }
        else {
            redirectAttributes.addFlashAttribute("error","There are no incubator with Id :" +id);
            return "redirect:/infirmier/incubator/lists/"+hospital.getId();
        }
    }

    @GetMapping("/incubator/delete/{id}")
    public String deleteIncubator(@PathVariable Long id, Model model, HttpSession session) {
        Incubator incubator = incubatorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid incubatorRepository id:" +id));
        System.out.println("incubatorRepository id: " + incubator.getId());
        incubatorRepository.delete(incubator);
        return "redirect:/infirmier/incubator/lists/"+incubator.getHospital().getId();
    }

    @GetMapping("/incubator/active/{id}")
    public String activeIncubator(@PathVariable Long id, HttpSession session){
        Incubator incubator = incubatorRepository.getOne(id);
        if (incubator.getState()== true){
            incubator.setState(false);
        }else {
            incubator.setState(true);
        }

        incubatorRepository.save(incubator);
        return "redirect:/infirmier/incubator/lists/"+incubator.getId() ;
    }
}
