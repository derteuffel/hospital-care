package com.hospital.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "bloodbank")
@Data
public class BloodBank implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupeSanguin;

    @NotNull
    @Size(min = 1, max = 1)
    private String rhesus;

    private Date date;

    private Boolean status;


    @ManyToOne
    @JsonIgnoreProperties("bloodbanks")
    private Hospital hospital;
}
