package com.hospital.Controller;

import com.hospital.repository.MedicamentRepository;
import com.hospital.repository.PharmacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/medicament")
public class MedicamentController {

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

}
