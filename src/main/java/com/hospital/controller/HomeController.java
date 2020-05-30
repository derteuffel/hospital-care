package com.hospital.controller;

import com.hospital.repository.RoleRepository;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @Autowired
    private CompteService compteService;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/")
    public String home(){

        return "index";
    }
}
