package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Table(name = "conversation")
public class Conversation implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String sender;
    private String receiver;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "conversations_comptes",
            joinColumns = @JoinColumn(
                    name = "compte_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "conversation_id", referencedColumnName = "id"))
    private List<Compte> comptes;

    @JsonIgnoreProperties("conversation")
    @OneToMany(mappedBy = "conversation")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Message> messages;


    public Conversation() {
    }

    public Conversation(String sender) {
        this.sender = sender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<Compte> getComptes() {
        return comptes;
    }

    public void setComptes(List<Compte> comptes) {
        this.comptes = comptes;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
