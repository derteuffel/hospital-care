package com.hospital.entities;

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

    private Boolean status;

    @ManyToOne
    private Hospital hospital;


}
