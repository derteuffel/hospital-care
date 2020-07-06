package com.hospital.Controller;

import com.hospital.entities.Compte;
import com.hospital.entities.Examen;
import com.hospital.entities.Hospital;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.ExamenRepository;
import com.hospital.repository.HospitalRepository;
import com.hospital.repository.PersonnelRepository;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/laboratoire")
public class LaboController {

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private CompteService compteService;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private HospitalRepository hospitalRepository;


    @GetMapping("/hospital/examen/lists/{id}")
    public String examensInHospital(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("compte",compte);
        Hospital hospital = hospitalRepository.getOne(id);
        List<Examen> examens = examenRepository.findByHospital(hospital);

        model.addAttribute("lists",examens);
        return "dashboard/pages/admin/laboratoire/examens/lists";

    }

    @GetMapping("/examen/delivery-date/{id}")
    public String examDeliveryDate(@PathVariable Long id, String time){
        Examen examen = examenRepository.getOne(id);
        Hospital hospital = examen.getHospital();
        examen.setTime(time);
        examenRepository.save(examen);
        return "redirect:/laboratoire/hospital/examen/lists/"+hospital.getId();
    }

    @GetMapping("/examen/detail/{id}")
    public String examDetail(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Examen examen = examenRepository.getOne(id);
        Hospital hospital = examen.getHospital();
        model.addAttribute("compte", compte);
        model.addAttribute("examen", examen);
        model.addAttribute("hospital",hospital);
        return "dashboard/pages/admin/laboratoire/examens/detail";
    }

    @GetMapping("/examen/result/{id}")
    public String examResult(@PathVariable Long id, String observation){
        Examen examen = examenRepository.getOne(id);
        Hospital hospital = examen.getHospital();
        examen.setDescription(observation);
        examenRepository.save(examen);
        return "redirect:/laboratoire/hospital/examen/lists/"+hospital.getId();
    }
}
