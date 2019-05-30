package com.example.myteamql.github.finalservlet.services;

import com.example.myteamql.github.finalservlet.entities.Reservation;
import com.example.myteamql.github.finalservlet.entities.Room;
import com.example.myteamql.github.finalservlet.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private RoomService roomService;

    public Reservation findReservationByCode(int code) {
        return reservationRepository.findByCode(code);
    }

    public boolean insert(Reservation reservation) {
        // check if max occupants is good
        // check if room is already reserved for those dates
        int roomNum = reservation.getRoom();
        Room room = roomService.getRoomByRoomNumber(roomNum);
        List<Room> availables = roomService.getAllRoomsByAvailability(reservation.getCheckIn(), reservation.getCheckOut());
        if (room.getMaxOccupants() >= reservation.getAdults() + reservation.getKids() && availables.contains(room)) {
            reservationRepository.save(reservation);
            return true;
        } else {
            System.out.println("unable to reserve room");
            return false;
        }
    }

    public void cancel(int code) {
        Reservation reservation = findReservationByCode(code);
        reservation.setCanceled(true);
        reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void changeNextAvailable(int roomNumber) {
        List<Reservation> reservations = getCheckInsAndCheckOuts(roomNumber);
        boolean flag = false;
        int i = 0;
        // check if room is available today
        Calendar calendar = Calendar.getInstance();
        java.util.Date currentDate = calendar.getTime();
        java.sql.Date date = new java.sql.Date(currentDate.getTime());
        System.out.println(reservations);
        if (!isRoomAvailableOn(date, roomNumber)) {
            System.out.println("changing next available date for room #" + roomNumber);
            while (!flag) {
                if (reservations.get(i).getCheckOut().equals(reservations.get(i + 1).getCheckIn())) {
                    i++;
                } else {
                    flag = true;
                    Room room = roomService.getRoomByRoomNumber(roomNumber);
                    room.setNextAvailable(reservations.get(i).getCheckOut());
                    roomService.insert(room);
                    return;
                }
            }
        }
    }

    private Boolean isRoomAvailableOn(Date date, int roomNumber) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean available = false;
        try{
            Connection conn = DriverManager.getConnection("jdbc:mysql://csc365.toshikuboi.net:3306/sec03group01",
                    "sec03group01", "group01@sec03");
            preparedStatement = conn.prepareStatement(
                    "SELECT * FROM room JOIN reservation ON room_number = room AND room = (?) WHERE (?) BETWEEN check_in AND check_out"
            );
            preparedStatement.setInt(1, roomNumber);
            preparedStatement.setDate(2, date);

            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next())
                available =  true;
            else
                available =  false;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally{
            try {
                resultSet.close();
                preparedStatement.close();
            }catch(SQLException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return available;
    }

    private List<Reservation> getCheckInsAndCheckOuts(int roomNumber) {
        PreparedStatement preparedStatement = null;
        List<Reservation> reservations = null;
        ResultSet resultSet = null;
        try{
            Connection conn = DriverManager.getConnection("jdbc:mysql://csc365.toshikuboi.net:3306/sec03group01",
                    "sec03group01", "group01@sec03");
            preparedStatement = conn.prepareStatement(
                    "SELECT check_in, check_out FROM room JOIN reservation ON room = room_number " +
                            "WHERE room_number = (?) ORDER BY check_in"
            );
            preparedStatement.setInt(1, roomNumber);

            resultSet = preparedStatement.executeQuery();
            reservations = unpackResultSet(resultSet);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally{
            try {
                resultSet.close();
                preparedStatement.close();
            }catch(SQLException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return reservations;
    }

    private List<Reservation> unpackResultSet(ResultSet rs) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        while(rs.next()) {
            Reservation reservation = new Reservation(
                    rs.getDate("check_in"),
                    rs.getDate("check_out"));
            reservations.add(reservation);
        }
        return reservations;
    }
}
