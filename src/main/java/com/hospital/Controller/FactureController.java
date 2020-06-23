package com.hospital.Controller;

import com.hospital.repository.FactureRepository;
import com.hospital.repository.PharmacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/facture")
public class FactureController {

    @Autowired
    private FactureRepository factureRepository;

    private PharmacyRepository pharmacyRepository;
}
