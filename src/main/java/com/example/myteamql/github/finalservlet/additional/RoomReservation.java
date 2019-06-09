package com.example.myteamql.github.finalservlet.additional;

import lombok.Data;

import java.sql.Date;

@Data
public class RoomReservation {
    private int code;
    private int room;
    private int adults;
    private int kids;
    private Date checkIn;
    private Date checkOut;
    private boolean canceled;
    private String lastName;
    private String firstName;
    private Long crNumber;
    private String picture;

    public RoomReservation(int code, int room, int adults, int kids, Date checkIn, Date checkOut, boolean canceled, String lastName, String firstName, Long crNumber, String picture) {
        this.code = code;
        this.room = room;
        this.adults = adults;
        this.kids = kids;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.canceled = canceled;
        this.lastName = lastName;
        this.firstName = firstName;
        this.crNumber = crNumber;
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "RoomReservation{" +
                "code=" + code +
                ", room=" + room +
                ", adults=" + adults +
                ", kids=" + kids +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", canceled=" + canceled +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", crNumber=" + crNumber +
                ", picture='" + picture + '\'' +
                '}';
    }
}
