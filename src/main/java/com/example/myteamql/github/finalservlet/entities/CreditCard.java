package com.example.myteamql.github.finalservlet.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
public class CreditCard {

    @Id
    private Long crNumber;

}
