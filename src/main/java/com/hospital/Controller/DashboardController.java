package com.hospital.Controller;

import com.hospital.repository.DosMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @Autowired
    DosMedicalRepository dos;

    @GetMapping("/dashboard")
    public String index(){
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
