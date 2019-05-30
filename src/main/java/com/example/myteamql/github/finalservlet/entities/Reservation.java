package com.example.myteamql.github.finalservlet.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

@Data
@Entity
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int code;

    private int room;
    private Date checkIn;
    private Date checkOut;
    private String lastName;
    private String firstName;
    private int adults;
    private int kids;
    private boolean canceled;
    private Long crNumber;

    public Reservation(Date checkIn, Date checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }
}
