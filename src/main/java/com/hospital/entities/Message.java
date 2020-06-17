package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Table(name = "message")
@Entity
public class Message implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String sender;
    private String body;
    private Boolean status;
    private String time;

    @ManyToOne
    @JsonIgnoreProperties("messages")
    private Conversation conversation;

    public Message(String sender, String body, Boolean status, String time) {
        this.sender = sender;
        this.body = body;
        this.status = status;
        this.time = time;
    }

    public Message() {
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
