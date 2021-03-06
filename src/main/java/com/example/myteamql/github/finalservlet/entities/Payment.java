package com.example.myteamql.github.finalservlet.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
public class Payment {

    @Id
    private int reservationCode;

    private String first;
    private String last;
    private Long crNumber;
    private Double charged;
}
