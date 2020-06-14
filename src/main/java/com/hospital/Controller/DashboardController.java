package com.hospital.Controller;

import com.hospital.entities.Compte;
import com.hospital.entities.Irdvjointure;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.DosMedicalRepository;
import com.hospital.repository.RdvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @Autowired
    DosMedicalRepository dos;

    @Autowired
    CompteRepository compteRepository;

    @Autowired
    RdvRepository rdvRepository;

    @GetMapping("/dashboard")
    public String index(Model model){

        List<Compte> comptes= compteRepository.findAll();
        List<Compte> patients = comptes.stream()
                .filter(d -> d.getRoles().stream().findFirst().get().getId() == 2)
                .collect(Collectors.toList());
        List<Compte> medecins = comptes.stream()
                .filter(d -> d.getRoles().stream().findFirst().get().getId() == 1)
                .collect(Collectors.toList());
        List<Irdvjointure> rdvs = rdvRepository.findAllWithJoin();
        model.addAttribute("patients",patients);
        model.addAttribute("medecins",medecins);
        model.addAttribute("rdvs",rdvs);

       // return rdvs.toString();
        return "dashboard/pages/admin/dashboard";

    }

    @GetMapping("/activities")
    public String activities(){
        return "dashboard/pages/admin/activities";
    }

    @GetMapping("/addDosMedical")
    public String addDosMedical(){
        return "dashboard/pages/admin/addDosMedical";
    }

    @GetMapping("/dosMedical")
    public String getAllDosMedical(Model model){
        model.addAttribute("dosMedicalList",dos.findAll());
        return "dashboard/pages/admin/dosMedical";
    }

}
