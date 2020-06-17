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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        if (compte.getPersonnel() != null){
            Hospital hospital = compte.getPersonnel().getHospital();
            for (Personnel personnel : hospital.getPersonnels()){
                comptes.add(compteRepository.findByEmail(personnel.getEmail()));
            }
        }
        List<Conversation> lists = conversationRepository.findAllBySenderIdOrReceiverId(compte.getId(),compte.getId(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists", lists);
        model.addAttribute("compte",compte);
        model.addAttribute("comptes",comptes);
        return "dashboard/pages/admin/chat/chats";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Conversation conversation = conversationRepository.findBySenderIdAndReceiverId(compte.getId(),id);
        Conversation conversation2 = conversationRepository.findBySenderIdAndReceiverId(id,compte.getId());
        List<Conversation> lists = conversationRepository.findAllBySenderIdOrReceiverId(compte.getId(),compte.getId(),Sort.by(Sort.Direction.DESC,"id"));
        List<Message> messages = new ArrayList<>();
        if (conversation != null){
            model.addAttribute("conversation",conversation);
            messages.addAll(conversation.getMessages());
            System.out.println("c'est moi");

        }else if (conversation2 != null){
            model.addAttribute("conversation",conversation2);
            messages.addAll(conversation2.getMessages());
            System.out.println("c'est moi");
        }else  {
            Conversation conversation1 = new Conversation();
            conversation1.setReceiver(compteRepository.getOne(id).getUsername());
            conversation1.setReceiverId(id);
            conversation1.setSenderId(compte.getId());
            conversation1.setSender(compte.getUsername());
            conversationRepository.save(conversation1);
            model.addAttribute("conversation", conversation1);
        }

        model.addAttribute("compte",compte);
        model.addAttribute("message",new Message());
        model.addAttribute("messages", messages);
        model.addAttribute("lists",lists);
        return "dashboard/pages/admin/chat/chat";
    }

    @PostMapping("/new/message/{id}")
    public String newMessage(Message message, @PathVariable Long id, HttpServletRequest request){
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("hh:mm");
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Conversation conversation = conversationRepository.getOne(id);
        message.setSender(compte.getUsername());
        message.setStatus(false);
        message.setTime(format.format(date));
        message.setConversation(conversation);
        messageRepository.save(message);
        return "redirect:/admin/conversation/detail/"+conversation.getReceiverId();
    }
}
