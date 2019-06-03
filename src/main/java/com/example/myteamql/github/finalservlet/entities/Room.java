package com.example.myteamql.github.finalservlet.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

@Data
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int roomNumber;

    private int maxOccupants;
    private String type;
    private String decor;
    private float price;
    private int beds;
    private float length;
    private float popularity;
    private String pictureurl;
    private Date nextAvailable;

    public Room(int roomNumber, int maxOccupants, String type, String decor, float price, int beds, float length, float popularity, String pictureurl, Date nextAvailable) {
        this.roomNumber = roomNumber;
        this.maxOccupants = maxOccupants;
        this.type = type;
        this.decor = decor;
        this.price = price;
        this.beds = beds;
        this.length = length;
        this.popularity = popularity;
        this.pictureurl = pictureurl;
        this.nextAvailable = nextAvailable;
    }

    public Room(int roomNumber, float popularity){
        this.roomNumber=roomNumber;
        this.popularity=popularity;
    }

    public Room(){

    }
}
