package com.hospital.Controller;

import com.hospital.entities.*;
import com.hospital.repository.*;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/rdv")
public class RdvController {

    @Autowired
    CompteRepository compteRepository;

    @Autowired
    RdvRepository rdvRepository;

    @Autowired
    private CompteService compteService;

    @Autowired
    private DosMedicalRepository dosMedical;

    @Autowired
    private PersonnelRepository per;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private PersonnelRepository personnelRepository;


    @GetMapping("/add/{id}")
    public ModelAndView showform(@PathVariable Long id, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Personnel personnel = per.getOne(id);
        Compte personnelAccount = compteRepository.findByPersonnel_Id(personnel.getId());
        ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/appointment/add-rdv");
        Rdv rdv = new Rdv();

        modelAndView.addObject("rdv", rdv);
        modelAndView.addObject("compte",compte);
        modelAndView.addObject("personnelAccount",personnelAccount);
//        modelAndView.addObject("appointments", rdvRepository.findAll());
        return modelAndView;
    }

    @PostMapping("/add/{compteId}/{personnelAccountId}")
    public String storeRdv(@ModelAttribute @Valid Rdv rdv, Errors errors,@PathVariable Long compteId,@PathVariable Long personnelAccountId, RedirectAttributes redirAttrs){
        if(errors.hasErrors()){
            return "dashboard/pages/admin/appointment/add-rdv";
        }
        Compte compte = compteRepository.getOne(compteId);
        Compte personnelAccount = compteRepository.getOne(personnelAccountId);

        if (compteId != personnelAccountId) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

            rdv.setComptes(Arrays.asList(compte, personnelAccount));
            rdv.setStatus(false);
            rdvRepository.save(rdv);
            Message message = new Message();
            message.setTime(sdf.format(new Date()));
            message.setStatus(false);
            message.setSender(compte.getUsername());
            message.setBody(rdv.getMotif());
            Conversation conversation = conversationRepository.findBySenderOrReceiver(compte.getUsername(), personnelAccount.getUsername());
            if (conversation != null) {
                message.setConversation(conversation);

            } else {
                Conversation conversation1 = new Conversation();
                conversation1.setComptes(Arrays.asList(compte, personnelAccount));
                conversation1.setReceiver(personnelAccount.getUsername());
                conversation1.setSender(compte.getUsername());
                conversationRepository.save(conversation1);
                message.setConversation(conversation1);
            }
            messageRepository.save(message);

            redirAttrs.addFlashAttribute("message", "Rdv added Successfully");
        }else {
            redirAttrs.addFlashAttribute("error", "Error you can not make an appointment with yourself");
        }
        return "redirect:/admin/staff/doctor/"+personnelAccount.getPersonnel().getId();
    }

/*
      @GetMapping("/add")
      public String form(Model model){
          List<Personnel> personnels = per.findAllByFunction("DOCTOR");
          List<DosMedical>dos = dosMedical.findAll();
          model.addAttribute("dos", dos);
          model.addAttribute("staffs", personnels);
          model.addAttribute("rdv", new Rdv());
          return "dashboard/pages/admin/appointment/add-rdv";
      }

        @PostMapping("/add")
        public String save(@Valid Rdv rdv, String code, Long id){

            Personnel personnel = per.getOne(id);
            DosMedical dos = dosMedical.findByCode(code);
            rdv.setStatus(false);
            rdv.setDosMedical(dos);
            rdv.setPersonnel(personnel);
            rdvRepository.save(rdv);
            return "redirect:/admin/rdv/all";
        }
*/


        @ResponseBody
        @GetMapping("/account")
        public ModelAndView getAllRdv(HttpServletRequest request){

            Principal principal =  request.getUserPrincipal();
            Compte compte = compteService.findByUsername(principal.getName());

            ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/rdv-list");
            List<Rdv> rdvs = rdvRepository.findAllByComptes_Id(compte.getId(),Sort.by(Sort.Direction.DESC, "id"));
            modelAndView.addObject("appointments",rdvs);
            return modelAndView;
        }

    @ResponseBody
    @GetMapping("/all")
    public ModelAndView getAllRdvs(HttpServletRequest request,Long id){

        Principal principal =  request.getUserPrincipal();
        Personnel personnel = per.getOne(id);
        Compte personnelAccount = compteRepository.findByPersonnel_Id(personnel.getId());
        Compte compte = compteService.findByUsername(principal.getName());
        ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/appointment/rdv-list");
        List<Rdv> rdvs = rdvRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        modelAndView.addObject("compte",compte);
        modelAndView.addObject("personnelAccount",personnelAccount);
        modelAndView.addObject("appointments",rdvs);
        return modelAndView;
    }
        @PostMapping("/delete/{id}")
        public String delete(@PathVariable("id") Long id,RedirectAttributes redirAttrs){
            try {
                rdvRepository.deleteById(id);
                redirAttrs.addFlashAttribute("message", "Successfully deleted");
                return "redirect:/admin/rdv/all";
            }catch (Exception e){
                redirAttrs.addFlashAttribute("error", "Error deleting this hospital");
                return "redirect:/admin/rdv/all";
            }
        }

        @GetMapping("/edit/{id}")
        public ModelAndView showEditForm(@PathVariable("id") Long id,RedirectAttributes redirAttrs){

            ModelAndView modelAndView = new ModelAndView("dashboard/pages/admin/appointment/edit-rdv");
            List<Compte> comptes= compteRepository.findAll();
            List<Compte> patients = comptes.stream()
                    .filter(d -> d.getRoles().stream().findFirst().get().getId() == 2)
                    .collect(Collectors.toList());
            List<Compte> medecins = comptes.stream()
                    .filter(d -> d.getRoles().stream().findFirst().get().getId() == 1)
                    .collect(Collectors.toList());
            Rdv rdv = rdvRepository.findById(id).get();
            modelAndView.addObject("rdv",rdv);
            modelAndView.addObject("patients", patients);
            modelAndView.addObject("medecins", medecins);
            return modelAndView;
        }

        @PostMapping("/edit/{id}")
        public String updateHospital(@PathVariable("id") Long id, @Valid Rdv rdv, Errors errors, RedirectAttributes redirAttrs){
            if (errors.hasErrors()) {
                return "dashboard/pages/admin/appointment/edit-rdv";
            }
            rdvRepository.save(rdv);
            redirAttrs.addFlashAttribute("message", "Successfully edited");
            return "redirect:/admin/rdv/all";
        }

        @GetMapping("/active/{id}")
        public String active(@PathVariable Long id, HttpSession session){
            Rdv rdv = rdvRepository.getOne(id);
            if (rdv.getStatus() != null) {
                if (rdv.getStatus() == true) {
                    rdv.setStatus(false);
                } else {
                    rdv.setStatus(true);
                }
                rdvRepository.save(rdv);
            }else {
                rdv.setStatus(false);
                rdvRepository.save(rdv);
                return "redirect:/admin/rdv/active/"+rdv.getId();
            }


            return "redirect:/admin/rdv/all" ;
        }

        @GetMapping("/active")
        public String findAllStatusActive(Model model,HttpServletRequest request){
            Principal principal =  request.getUserPrincipal();
            Compte compte = compteService.findByUsername(principal.getName());
            List<Rdv> lists = rdvRepository.findAllByStatusAndComptes_Id(true,compte.getId(),Sort.by(Sort.Direction.DESC,"id"));
            model.addAttribute("appointments", lists);
            return "dashboard/pages/admin/appointment/rdv-actif";
        }

        @GetMapping("/inactive")
        public String findAllStatusInacctive(Model model,HttpServletRequest request){

            Principal principal =  request.getUserPrincipal();
            Compte compte = compteService.findByUsername(principal.getName());
            List<Rdv> lists = rdvRepository.findAllByStatusAndComptes_Id(false,compte.getId(),Sort.by(Sort.Direction.DESC,"id"));
            model.addAttribute("appointments", lists);
            return "dashboard/pages/admin/appointment/rdv-inactif";
        }

}
