package com.hospital.entities;

import com.hospital.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "compte")
@Data
@AllArgsConstructor //@NoArgsConstructor
public class Compte implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private Boolean status;
    private String name;
    private String token;


    @OneToOne
    private Personnel personnel;



    //@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)


    @JoinTable(
            name = "comptes_roles",
            joinColumns = @JoinColumn(
                    name = "compte_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    @ManyToMany(mappedBy = "comptes")
    private List<Conversation> conversations;
    @ManyToMany(mappedBy = "comptes")
    private List<Rdv> rdvs;
    @OneToMany(mappedBy = "compte")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Armoire> armoires;


    public Compte(String username, String password, String email, String avatar, Boolean status, String name, String token) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.avatar = avatar;
        this.status = status;
        this.name = name;
        this.token = token;
    }

    public Compte(){

    }

    public boolean checkRole(ERole roleEnum){
        boolean authorized = false;
        for (Role role : this.getRoles()){
            if(role.getName().equals(roleEnum.toString())){
                authorized = true;
            }
        }
        return authorized;
    }

    public Compte(String username, String email, String avatar) {
        this.username = username;
        this.email = email;
        this.avatar = avatar;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public Collection<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Personnel getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    public List<Rdv> getRdvs() {
        return rdvs;
    }

    public void setRdvs(List<Rdv> rdvs) {
        this.rdvs = rdvs;
    }

    public List<Armoire> getArmoires() {
        return armoires;
    }

    public void setArmoires(List<Armoire> armoires) {
        this.armoires = armoires;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
