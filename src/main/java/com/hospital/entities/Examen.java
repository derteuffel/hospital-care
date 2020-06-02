package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
public class Examen implements Serializable {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String testType;

    private String deliverDate;

    private String dateOfTesting;

    private String results;

    private String description;

    @ManyToOne
    @JsonIgnoreProperties("examens")
    private Consultation consultation;

    @ManyToOne
    private Hospital hospital;
}
