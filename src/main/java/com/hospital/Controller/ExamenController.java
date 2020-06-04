package com.hospital.Controller;

import com.hospital.entities.*;
import com.hospital.helpers.ExamenHelper;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.ExamenRepository;
import com.hospital.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private CompteRepository compteRepository;



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
        model.addAttribute("idConsultation",id);
        return "dashboard/pages/admin/exam";
    }

    /** form for adding an exam */
    @GetMapping(value = "/create")
    public String addExam(@RequestParam("idConsultation") int  idConsultation, Model model){
        List<Hospital> hospitals = hospitalRepository.findAll();
        model.addAttribute("idConsultation",idConsultation);
        model.addAttribute("hospitalList",hospitals);
        model.addAttribute(new ExamenHelper());
        return "dashboard/pages/admin/addExam";
    }

    /** Add an exam */
    @PostMapping("/create")
    public String saveExam(@ModelAttribute @Valid ExamenHelper examenHelper,Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute("hospitalList",hospitalRepository.findAll());
            model.addAttribute("idConsultation",examenHelper.getIdConsultation());
            return "dashboard/pages/admin/addExam";
        }else{
            Consultation consultation = consultationRepository.getOne(examenHelper.getIdConsultation());
            Hospital hospital = hospitalRepository.findByName(examenHelper.getHospitalName());
            examenRepository.save(examenHelper.getExamInstance(hospital,consultation));
        }
        return  "redirect:/admin/exam/consultation/"+examenHelper.getIdConsultation();
    }

    /** cancel an exam */
    @PostMapping(value = "/cancel")
    public String cancelExam(HttpServletRequest request, Model model){

        Long id = Long.parseLong(request.getParameter("id"));
        String password = request.getParameter("password");
        String idConsultation = request.getParameter("idConsultation");
        Compte compte = compteRepository.findByPassword(password);

        if(compte != null){
            if(compte.getStatus()){
                examenRepository.deleteById(id);
            }
        }
         return "redirect:/admin/exam/consultation/"+idConsultation;
    }


}
