package com.hospital.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @GetMapping("/dashboard")
    public String index(){
        return "dashboard/pages/admin/dashboard";
    }

    @GetMapping("/activities")
    public String activities(){
        return "dashboard/pages/admin/activities";
    }
    @GetMapping("/doctors")
    public String doctors(){
        return "dashboard/pages/admin/doctors";
    }

    @GetMapping("/addDosMedical")
    public String addDosMedical(){
        return "dashboard/pages/admin/addDosMedical";
    }

    /*@GetMapping("/dashboard")
    public String index(){
        return "dashboard/pages/admin/dashboard";
    }*/
}
