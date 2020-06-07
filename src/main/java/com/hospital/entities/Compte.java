package com.hospital.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

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



    //@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)

    @JoinTable(
            name = "comptes_roles",
            joinColumns = @JoinColumn(
                    name = "compte_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    /* @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
     @JoinTable(
             name = "comptes_personnels",
             joinColumns = @JoinColumn(
                     name = "compte_id", referencedColumnName = "id"),
             inverseJoinColumns = @JoinColumn(
                     name = "personnel_id", referencedColumnName = "id"))*/
    @OneToOne
    private Personnel personnel;


    public Compte(String username, String email, Boolean status, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.status = status;
    }

    public Compte(){

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
        return roles;
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
}
