package com.hospital.entities;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;


@Entity
@Table(name = "consultation")
@Data
public class Consultation implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date date;


    @ManyToOne
    private DosMedical dosMedical;


    @OneToMany(mappedBy = "consultation")
    private Collection<Examen>examens;
}
