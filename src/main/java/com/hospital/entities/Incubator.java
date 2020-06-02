package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "incubator")
@Data
public class Incubator implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private String incubartorNo;

    private String dateObtained;

    private Boolean status;

    @ManyToOne
    @JsonIgnoreProperties("incubators")
    private Hospital hospital;


    public String getDateObtained() {
        return dateObtained;
    }

    public void setDateObtained(String dateObtained) {
        this.dateObtained = dateObtained;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getIncubatorNo() {
        return incubartorNo;
    }

    public void setIncubatorNo(String incubatorNo) {
        this.incubartorNo = incubatorNo;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }
}
