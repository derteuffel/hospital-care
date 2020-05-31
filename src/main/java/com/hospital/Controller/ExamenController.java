package com.hospital.Controller;

import com.hospital.entities.Consultation;
import com.hospital.entities.Examen;
import com.hospital.helpers.ExamenHelper;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.ExamenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/hospital-care/examen")
public class ExamenController {


    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private ConsultationRepository consultationRepository;


    @PostMapping("/save/{id}")
    public String saveExamen(@Valid ExamenHelper examenHelper, @PathVariable Long id,
                             RedirectAttributes redirectAttributes){

        Consultation consultation = consultationRepository.getOne(id);
        Examen examen = new Examen();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd hh:mm");
        //examen.setDateOfTesting(dateFormat.format(date));
       // examen.setDeliverDate(dateFormat.format(date));
       // examen.setName(examenHelper.getName());
       // examen.setResults(examenHelper.getResults());
       // examen.setTestType(examenHelper.getTestType());
       // examen.setDescription(examenHelper.getDescription());
       // examen.setConsultation(consultation);
        examenRepository.save(examen);

        return ""; /**** ici on va voir la vue qu'il faut mettre *****/
    }
}
