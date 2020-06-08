package com.hospital.entities;
import java.util.Date;

public class RdvJointure {
    private Long id;

    private String username;


    private Long compte_id;

    private Long personnel_id;

    private Date date;
    private String motif;
    private String personnel;

    public RdvJointure(Long id, String username, Long compte_id, Long personnel_id, Date date, String motif, String personnel) {
        this.id = id;
        this.username = username;
        this.compte_id = compte_id;
        this.personnel_id = personnel_id;
        this.date = date;
        this.motif = motif;
        this.personnel = personnel;
    }
}
