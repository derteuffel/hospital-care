package com.hospital.controller;


import com.hospital.entities.Hospital;
import com.hospital.entities.Personnel;
import com.hospital.helpers.PersonnelHelper;
import com.hospital.repository.HospitalRepository;
import com.hospital.repository.PersonnelRepository;
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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/staff")
public class PersonnelController {


    @Value("${file.upload-dir}")
    private  String fileStorage;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private HospitalRepository hospitalRepository;


    /** Get all personnel in an application */
    @GetMapping("/lists")
    public String findAll(Model model){
        System.out.println(fileStorage);
        model.addAttribute("personnelForm", new Personnel());
        model.addAttribute("staffs", personnelRepository.findAll());

        return "dashboard/pages/admin/doctor/doctors";
    }

/*    @GetMapping("/create")
    public String form(Model model,@RequestParam("idHospital") int  idHospital, Long id){
        List<Hospital> hospitals = hospitalRepository.findAll();
        model.addAttribute("idHospital",idHospital);
        model.addAttribute("hospital", hospitalRepository.getOne(id));
        model.addAttribute("hospitalList",hospitals);
        model.addAttribute(new PersonnelHelper());
        return "dashboard/pages/admin/form-personnel";
    }*/

    @GetMapping("/create")
    public String form(Model model){
        model.addAttribute("hospitals", hospitalRepository.findAll());
        model.addAttribute("personnel",new Personnel());
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
