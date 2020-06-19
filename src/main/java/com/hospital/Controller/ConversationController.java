package com.hospital.Controller;

import com.hospital.entities.*;
import com.hospital.repository.CompteRepository;
import com.hospital.repository.ConversationRepository;
import com.hospital.repository.MessageRepository;
import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/conversation")
public class ConversationController {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CompteService compteService;

    @Autowired
    private CompteRepository compteRepository;


    @GetMapping("/lists")
    public String getAllsByCompte(Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        List<Compte> comptes = new ArrayList<>();
        List<Conversation> lists = conversationRepository.findAllByComptes_Id(compte.getId(),Sort.by(Sort.Direction.DESC,"id"));
        if (compte.getPersonnel() != null){
            Hospital hospital = compte.getPersonnel().getHospital();
            for (Personnel personnel : hospital.getPersonnels()){
                comptes.add(compteRepository.findByEmail(personnel.getEmail()));
            }
        }

        model.addAttribute("lists", lists);
        model.addAttribute("compte",compte);
        model.addAttribute("comptes",comptes);
        return "dashboard/pages/admin/chat/chats";
    }

    @GetMapping("/detail/{receiver}")
    public String detail(@PathVariable String receiver, HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Compte compte1 = compteRepository.getOne(compteService.findByUsername(receiver).getId());
        Conversation conversation = conversationRepository.findBySenderOrReceiver(compte.getUsername(),compte1.getUsername());
        List<Conversation> lists = conversationRepository.findAllByComptes_Id(compte.getId(),Sort.by(Sort.Direction.DESC,"id"));
        List<Message> messages = new ArrayList<>();
        if (conversation != null){
            model.addAttribute("conversation",conversation);
            messages.addAll(conversation.getMessages());
        }else {
            Conversation conversation1 = new Conversation();
            conversation1.setSender(compte.getUsername());
            conversation1.setReceiver(receiver);
            conversation1.setComptes(Arrays.asList(compte,compte1));
            conversationRepository.save(conversation1);
            model.addAttribute("conversation",conversation1);
        }
        model.addAttribute("conversation",conversation);
        System.out.println("c'est moi");
        model.addAttribute("compte",compte);
        model.addAttribute("message",new Message());
        model.addAttribute("messages", messages);
        model.addAttribute("lists",lists);
        return "dashboard/pages/admin/chat/chat";
    }

    @PostMapping("/new/message/{id}")
    public String newMessage(Message message, @PathVariable Long id, HttpServletRequest request, @RequestParam("file") MultipartFile file){
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("hh:mm");
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Conversation conversation = conversationRepository.getOne(id);
        if (!(file.isEmpty())){

            try {

                if (!(file.isEmpty())) {
                    // Get the file and save it somewhere
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get( file + file.getOriginalFilename());
                    Files.write(path, bytes);
                    message.setFichier("/downloadFile/"+file.getOriginalFilename());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {


        message.setSender(compte.getUsername());
        }
        message.setStatus(false);
        message.setTime(format.format(date));
        message.setConversation(conversation);
        messageRepository.save(message);
        return "redirect:/admin/conversation/detail/"+conversation.getReceiver();
    }
}
