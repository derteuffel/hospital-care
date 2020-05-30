package com.hospital.controller;


import com.hospital.entities.Personnel;
import com.hospital.repository.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/hospital-care/personnel")
public class PersonnelController {


    @Value("${file.upload-dir}")
    private  String fileStorage;

    @Autowired
    private PersonnelRepository personnelRepository;


    @GetMapping("/lists")
    public String findAll(Model model){
        System.out.println(fileStorage);
        model.addAttribute("personnelForm", new Personnel());
        model.addAttribute("personnels", personnelRepository.findAll());

        return "personnel/all";
    }

    @GetMapping("/form")
    public String form(Model model){
        Personnel personnel = new Personnel();

        model.addAttribute("personnel", personnel);
        return "personnel/form";
    }


    @PostMapping("/save")
    public String save(Personnel personnel, Model model,
                       BindingResult bindingResult, @RequestParam("file") MultipartFile file){

        System.out.println(personnel.getLastName());
        Personnel persExists = personnelRepository.findByLastNameOrEmailOrPhone(personnel.getLastName(),
                personnel.getEmail(), personnel.getPhone());

        if (persExists != null){
            bindingResult
                    .rejectValue("email", "error.personnel",
                            "there is already a personnel registered with an email or name or telephone provided ");
        }

        if(bindingResult.hasErrors()) {
            return  "personnel/form";
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
            System.out.println(personnel.getLastName());
            personnelRepository.save(personnel);
        }
        return "redirect:/hospital-care/personnel/lists";

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
            return "redirect:/hospital-care/personnel/get/"+personnel2.getId();
        }
        else {
            redirectAttributes.addFlashAttribute("error","There are no personnel with Id :" +id);
            return "redirect:/hospital-care/personnel/lists";
        }
    }

    @GetMapping("/get/{id}")
    public String findUserById(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){
        Optional<Personnel> personnel = personnelRepository.findById(id);
        if (personnel.isPresent()){
            model.addAttribute("personnel",personnel.get());
            return "personnel/detail";
        }
        else {
            redirectAttributes.addFlashAttribute("error", "There no personnel with Id :" +id);
            return "redirect:/hospital-care/personnel/lists";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        personnelRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "You deleted the personnel with Id:" +id);
        return "redirect:/hospital/personnel/lists";
    }

}
