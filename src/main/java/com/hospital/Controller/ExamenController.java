package com.hospital.Controller;

import com.hospital.entities.Consultation;
import com.hospital.entities.DosMedical;
import com.hospital.entities.Examen;
import com.hospital.entities.Hospital;
import com.hospital.helpers.ExamenHelper;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.ExamenRepository;
import com.hospital.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/exam")
public class ExamenController {


    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private ConsultationRepository consultationRepository;


    /** Get all exams made in an hospital */
    @GetMapping(value = "/hospital/{id}")
    @ResponseBody
    public List<Examen> getAllExamsInHospital(@PathVariable Long id){
        Hospital hospital =  hospitalRepository.getOne(id);
        List<Examen> exams = examenRepository.findByHospital(hospital);
        return exams;
    }

    /** Get all exams of a consultation */
    @GetMapping(value = "/consultation/{id}")
    public String getAllExamsOfAConsultation(@PathVariable Long id, Model model){
        Consultation consultation =  consultationRepository.getOne(id);
        List<Examen> exams = examenRepository.findByConsultation(consultation);
        model.addAttribute("examList",exams);
        return "dashboard/pages/admin/exam";
    }

    /** form for adding an exam */
    @GetMapping(value = "/create")
    public String addExam(){
        return "dashboard/pages/admin/addExam";
    }

    /** Add an exam */
    @PostMapping("/create")
    public String saveExam(@Valid Examen exam, Model model){

       // Consultation consultation = consultationRepository.getOne(id);
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

    /** cancel an exam */
    @DeleteMapping(value = "/{id}")
    public String cancelExam(@PathVariable Long id, Model model){
         examenRepository.deleteById(id);
         return "exam";
    }


}
