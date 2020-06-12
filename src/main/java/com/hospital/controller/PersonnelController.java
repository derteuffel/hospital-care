package com.hospital.controller;


import com.hospital.entities.Compte;
import com.hospital.entities.DosMedical;
import com.hospital.entities.Hospital;
import com.hospital.entities.Personnel;
import com.hospital.helpers.PersonnelHelper;
import com.hospital.repository.DosMedicalRepository;
import com.hospital.repository.HospitalRepository;
import com.hospital.repository.PersonnelRepository;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/admin/staff")
public class PersonnelController {


    @Value("${file.upload-dir}")
    private  String fileStorage;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private CompteService compteService;

    @Autowired
    private DosMedicalRepository dosMedicalRepository;


    /** Get all personnel in an application */
    @GetMapping("/lists/doctors")
    public String findAll(Model model){
        System.out.println(fileStorage);
        model.addAttribute("lists", personnelRepository.findAllByFunction("DOCTOR"));
        return "dashboard/pages/admin/doctor/doctors";
    }

    @GetMapping("/lists/doctors/{id}")
    public String findAllByHospital(Model model, @PathVariable Long id){
        System.out.println(fileStorage);
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hospital);
        model.addAttribute("lists", personnelRepository.findAllByFunctionAndHospital_Id("DOCTOR",hospital.getId()));
        return "dashboard/pages/admin/hospital/doctors";
    }

    @GetMapping("/add/doctor/{id}")
    public String addDoctor(@PathVariable Long id,Model model){
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("form",new PersonnelHelper());
        return "dashboard/pages/admin/hospital/new-doctor";
    }

    @PostMapping("/create/doctor/{id}")
    public String saveDoctor( @Valid PersonnelHelper form, Model model,
                       BindingResult bindingResult,
                       @RequestParam("file") MultipartFile file, @PathVariable Long id, HttpSession session){

        Hospital hospital = hospitalRepository.getOne(id);
        Personnel persExists = personnelRepository.findByLastNameOrEmailOrPhone(form.getLastName(),

                form.getEmail(), form.getPhone());

        Personnel personnel = new Personnel();
        DosMedical dosMedical = new DosMedical();

        if (persExists != null){
            bindingResult
                    .rejectValue("email", "error.personnel",
                            "there is already a personnel registered with an email or name or telephone provided ");
        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("hospital",hospital);
            model.addAttribute("form",new PersonnelHelper());
            return  "dashboard/pages/admin/hospital/new-doctor";
        }else {
            if (!(file.isEmpty())){
                try {
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(fileStorage + file.getOriginalFilename());
                    Files.write(path, bytes);
                }catch (IOException e){
                    e.printStackTrace();
                }
                personnel.setAvatar("/downloadFile/"+file.getOriginalFilename());
            }else {
                personnel.setAvatar("/img/default.jpeg");
            }
            Long days = TimeUnit.DAYS.convert(new Date().getTime() - form.getBirthDate().getTime(),TimeUnit.MILLISECONDS);
            personnel.setHospital(hospital);
            personnel.setAddress(form.getAddress());
            personnel.setEmail(form.getEmail());
            personnel.setLastName(form.getLastName());
            personnel.setFirstName(form.getFirstName());
            personnel.setAge(Math.round(days/365));
            personnel.setCity(form.getCity());
            personnel.setFunction("DOCTOR");
            personnel.setPhone(form.getPhone());
            personnel.setGender(form.getSex());
            //personnel.setLocalisation(form.getLocalisation());
            personnelRepository.save(personnel);
            dosMedical.setSex(form.getSex());
            dosMedical.setName(personnel.getLastName()+" "+personnel.getFirstName());
            dosMedical.setBloodType(form.getBloodType());
            dosMedical.setHeight(Integer.parseInt(form.getHeight()));
            dosMedical.setWeight(Integer.parseInt(form.getWeight()));
            dosMedical.setBirthDate(form.getBirthDate());
            dosMedical.setRhesus(form.getRhesus());
            dosMedical.setHereditaryDiseases(form.getHereditaryDiseases());
            dosMedical.setDescription(form.getDescription());
            dosMedical.setCode(form.getCode());
            dosMedicalRepository.save(dosMedical);
            compteService.saveDoctor(form,personnel.getAvatar(),dosMedical,personnel);
            System.out.println(personnel.getLastName());
        }
        return "redirect:/admin/staff/lists/doctors/"+hospital.getId();

    }


    @GetMapping("/lists/simples/{id}")
    public String findAllByHospitalS(Model model, @PathVariable Long id){
        System.out.println(fileStorage);
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital", hospital);
        model.addAttribute("lists", personnelRepository.findAllByFunctionAndHospital_Id("SIMPLE",hospital.getId()));
        return "dashboard/pages/admin/hospital/simples";
    }


    @GetMapping("/lists/simples")
    public String findAllSimples(Model model){
        System.out.println(fileStorage);
        model.addAttribute("lists", personnelRepository.findAllByFunction("SIMPLE"));
        return "dashboard/pages/admin/simple/simples";
    }

    @GetMapping("/add/simple/{id}")
    public String addSimple(@PathVariable Long id,Model model){
        Hospital hospital = hospitalRepository.getOne(id);
        model.addAttribute("hospital",hospital);
        model.addAttribute("form",new PersonnelHelper());
        return "dashboard/pages/admin/hospital/new-simple";
    }

    @PostMapping("/create/simple/{id}")
    public String saveSimple( @Valid PersonnelHelper form, Model model,
                              BindingResult bindingResult,
                              @RequestParam("file") MultipartFile file, @PathVariable Long id, HttpSession session){

        Hospital hospital = hospitalRepository.getOne(id);
        Personnel persExists = personnelRepository.findByLastNameOrEmailOrPhone(form.getLastName(),

                form.getEmail(), form.getPhone());

        Personnel personnel = new Personnel();
        DosMedical dosMedical = new DosMedical();

        if (persExists != null){
            bindingResult
                    .rejectValue("email", "error.personnel",
                            "there is already a personnel registered with an email or name or telephone provided ");
        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("hospital",hospital);
            model.addAttribute("form",new PersonnelHelper());
            return  "dashboard/pages/admin/hospital/new-simple";
        }else {
            if (!(file.isEmpty())){
                try {
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(fileStorage + file.getOriginalFilename());
                    Files.write(path, bytes);
                }catch (IOException e){
                    e.printStackTrace();
                }
                personnel.setAvatar("/downloadFile/"+file.getOriginalFilename());
            }else {
                personnel.setAvatar("/img/default.jpeg");
            }
            Long days = TimeUnit.DAYS.convert(new Date().getTime() - form.getBirthDate().getTime(),TimeUnit.MILLISECONDS);
            personnel.setHospital(hospital);
            personnel.setAddress(form.getAddress());
            personnel.setEmail(form.getEmail());
            personnel.setLastName(form.getLastName());
            personnel.setFirstName(form.getFirstName());
            personnel.setAge(Math.round(days/365));
            personnel.setCity(form.getCity());
            personnel.setFunction("SIMPLE");
            personnel.setPhone(form.getPhone());
            personnel.setGender(form.getSex());
            //personnel.setLocalisation(form.getLocalisation());
            personnelRepository.save(personnel);
            dosMedical.setSex(form.getSex());
            dosMedical.setName(personnel.getLastName()+" "+personnel.getFirstName());
            dosMedical.setBloodType(form.getBloodType());
            dosMedical.setHeight(Integer.parseInt(form.getHeight()));
            dosMedical.setWeight(Integer.parseInt(form.getWeight()));
            dosMedical.setBirthDate(form.getBirthDate());
            dosMedical.setRhesus(form.getRhesus());
            dosMedical.setHereditaryDiseases(form.getHereditaryDiseases());
            dosMedical.setDescription(form.getDescription());
            dosMedical.setCode(form.getCode());
            dosMedicalRepository.save(dosMedical);
            compteService.saveSimple(form,personnel.getAvatar(),dosMedical,personnel);
            System.out.println(personnel.getLastName());
        }
        return "redirect:/admin/staff/lists/simples/"+hospital.getId();

    }




    @GetMapping("/create")
    public String form(Model model){
        model.addAttribute("hospitals", hospitalRepository.findAll());
        model.addAttribute("form",new PersonnelHelper());
        return "dashboard/pages/admin/personnel/form-personnel";
    }


    @PostMapping("/create")
    public String save(@ModelAttribute("personnel") @Valid Personnel personnel, Model model,
                       BindingResult bindingResult,
                       @RequestParam("file") MultipartFile file, Long id, HttpSession session){

        Hospital hospital = hospitalRepository.getOne(id);
        session.setAttribute("id",hospital);
        System.out.println(personnel.getLastName());
        Personnel persExists = personnelRepository.findByLastNameOrEmailOrPhone(personnel.getLastName(),

                personnel.getEmail(), personnel.getPhone());

        if (persExists != null){
            bindingResult
                    .rejectValue("email", "error.personnel",
                            "there is already a personnel registered with an email or name or telephone provided ");
        }

        if(bindingResult.hasErrors()) {
            return  "dashboard/pages/admin/personnel/form-personnel";
        }else {
            if (!(file.isEmpty())){
                try {
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(fileStorage + file.getOriginalFilename());
                    Files.write(path, bytes);
                }catch (IOException e){
                    e.printStackTrace();
                }
                personnel.setHospital(hospital);
                personnel.setAvatar("/downloadFile/"+file.getOriginalFilename());
            }else {
                personnel.setAvatar("/img/default.jpeg");
            }
            System.out.println(personnel.getLastName());
            personnel.setHospital(hospital);
           // Hospital hospital = hospitalRepository.getOne(personnelHelper.getIdHospital());
            personnelRepository.save(personnel);
        }
        return "redirect:/admin/staff/lists";

    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable("id") Long id,Model model,RedirectAttributes redirAttrs){

        try{
            Personnel personnel = personnelRepository.getOne(id);
            model.addAttribute("personnel",personnel);
            return "dashboard/pages/admin/personnel/edit-staff";
        }catch (Exception e){
            redirAttrs.addFlashAttribute("error", "This hospital seems to not exist");
            return "redirect:/admin/staff/lists";
        }
    }



    @PostMapping("/update/{id}")
    public String updateUser(Personnel personnel, @PathVariable Long id, RedirectAttributes redirectAttributes,
                             @RequestParam("file") MultipartFile file){
        Optional<Personnel> personnel1 = personnelRepository.findById(id);


        if (personnel1.isPresent()) {
            Personnel personnel2 = personnel1.get();
            personnel2.setLastName(personnel.getLastName());
            personnel2.setFirstName(personnel.getFirstName());
            personnel2.setEmail(personnel.getEmail());
            personnel2.setCity(personnel.getCity());
            personnel2.setPhone(personnel.getPhone());
            personnel2.setAddress(personnel.getAddress());
            personnel2.setAddress(personnel.getAddress());
            personnel2.setFunction(personnel.getFunction());
            personnel2.setAge(personnel.getAge());
            if (!(file.isEmpty())){
                try{
                    // Get the file and save it somewhere
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(fileStorage + file.getOriginalFilename());
                    Files.write(path, bytes);
                }catch (IOException e){
                    e.printStackTrace();
                }
                personnel.setAvatar("/downloadFile/"+file.getOriginalFilename());
            }else {
                personnel2.setAvatar(personnel.getAvatar());
            }
            personnelRepository.save(personnel2);
            redirectAttributes.addFlashAttribute("success", "The personnel has been updated successfully");
            return "redirect:/admin/staff/get/"+personnel2.getId();
        }
        else {
            redirectAttributes.addFlashAttribute("error","There are no personnel with Id :" +id);
            return "redirect:/admin/staff/lists";
        }
    }

/*    @GetMapping("/get/{id}")
    public String findUserById(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){
        Optional<Personnel> personnel = personnelRepository.findById(id);
        if (personnel.isPresent()){
            model.addAttribute("personnel",personnel.get());
            return "dashboard/pages/admin/personnel/show-staff";
        }
        else {
            redirectAttributes.addFlashAttribute("error", "There no personnel with Id :" +id);
            return "redirect:/admin/personnel/staff/lists";
        }
    }*/



  @GetMapping("/getStaffByHospital/{hospitalId}")
  public String getPersonnelByHospital(@PathVariable Long hospitalId, Model model){

      Optional<Hospital> hospital = hospitalRepository.findById(hospitalId);
      model.addAttribute("hospital", hospital.get());
      model.addAttribute("staffs", personnelRepository.findAllByHospital_Id(hospitalId));
      return "dashboard/pages/admin/personnel/staffByHospital";
  }




    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid personnel id:" +id));
        System.out.println("personnel id: " + personnel.getId());
        Hospital hospital = hospitalRepository.getOne((Long)session.getAttribute("id"));
        personnelRepository.delete(personnel);
        model.addAttribute("staffs", personnelRepository.findAll());
        return "redirect:/admin/hospital/"+hospital.getId() ;
    }

}
