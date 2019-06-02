package com.example.myteamql.github.finalservlet.additional;

public class RoomRevenue {

    private int roomNumber;
    private int year;
    private String month;
    private double revenue;

    public RoomRevenue(int roomNumber, int year, String month, double revenue) {
        this.roomNumber = roomNumber;
        this.year = year;
        this.month = month;
        this.revenue = revenue;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        return "RoomRevenue{" +
                "roomNumber=" + roomNumber +
                ", year=" + year +
                ", month=" + month +
                ", revenue=" + revenue +
                '}';
    }

}
