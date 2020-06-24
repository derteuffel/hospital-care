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
import java.util.*;

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

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Timer timer = new Timer();
        int begin = 0;
        int timeInterval = 2000;

        Conversation conversation = conversationRepository.getOne(id);
        List<Conversation> lists = conversationRepository.findAllByComptes_Id(compte.getId(),Sort.by(Sort.Direction.DESC,"id"));
        List<Message> messages = messageRepository.findAllByConversation_Id(conversation.getId(), Sort.by(Sort.Direction.DESC,"id"));
            model.addAttribute("conversation",conversation);
            /*timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    detail(conversation.getId(),request,model);
                }
            }, begin,timeInterval);*/
        System.out.println("je fonctionne bien");
        for (Message message : messages){
            if (compte.getUsername().equals(message.getSender())){
                message.setStatus(true);
                messageRepository.save(message);
            }
        }
        model.addAttribute("conversation",conversation);

        model.addAttribute("messages", messages);
        model.addAttribute("compte",compte);
        model.addAttribute("message",new Message());
        model.addAttribute("lists",lists);

        return "dashboard/pages/admin/chat/chat";

    }

    @GetMapping("/add/{id}")
    public String newConversation(@PathVariable Long id, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Compte compte1 = compteRepository.getOne(id);
        Conversation senderTest = conversationRepository.findBySenderAndReceiver(compte.getUsername(),compte1.getUsername());
        Conversation receiverTest = conversationRepository.findBySenderAndReceiver(compte1.getUsername(),compte.getUsername());
        if (senderTest != null){
            return "redirect:/admin/conversation/detail/"+senderTest.getId();
        }else if (receiverTest != null){
            return "redirect:/admin/conversation/detail/"+receiverTest.getId();
        }else {
            Conversation conversation = new Conversation();
            conversation.setSender(compte.getUsername());
            conversation.setReceiver(compte1.getUsername());
            conversation.setComptes(Arrays.asList(compte, compte1));
            conversationRepository.save(conversation);
            return "redirect:/admin/conversation/detail/" + conversation.getId();
        }
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
        return "redirect:/admin/conversation/detail/"+conversation.getId();
    }
}
