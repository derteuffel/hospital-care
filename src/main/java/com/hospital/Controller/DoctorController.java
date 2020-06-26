package com.hospital.Controller;

import com.hospital.entities.Compte;
import com.hospital.entities.Hospital;
import com.hospital.entities.Personnel;
import com.hospital.enums.ERole;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.HospitalRepository;
import com.hospital.repository.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/admin/doctors")
public class DoctorController {


}
