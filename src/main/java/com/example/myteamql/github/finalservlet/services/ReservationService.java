package com.example.myteamql.github.finalservlet.services;

import com.example.myteamql.github.finalservlet.additional.RoomReservation;
import com.example.myteamql.github.finalservlet.additional.RoomRevenue;
import com.example.myteamql.github.finalservlet.entities.Payment;
import com.example.myteamql.github.finalservlet.entities.Reservation;
import com.example.myteamql.github.finalservlet.entities.Room;
import com.example.myteamql.github.finalservlet.repositories.ReservationRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

@Service
@Log4j2
public class ReservationService {

    private ReservationRepository reservationRepository;
    private RoomService roomService;
    @Autowired
    private PaymentService paymentService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, RoomService roomService) {
        this.reservationRepository = reservationRepository;
        this.roomService = roomService;
    }

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

    public boolean replace(Reservation newReservation, Reservation oldReservation) {
        // For changing a reservation
        reservationRepository.delete(oldReservation);
        int roomNum = newReservation.getRoom();
        Room room = roomService.getRoomByRoomNumber(roomNum);
        List<Room> availables = roomService.getAllRoomsByAvailability(newReservation.getCheckIn(), newReservation.getCheckOut());
        if (room.getMaxOccupants() >= newReservation.getAdults() + newReservation.getKids() && availables.contains(room)) {
            reservationRepository.save(newReservation);
            return true;
        } else {
            System.out.println("unable to reserve room");
            // If you can't add newReservation, put oldReservation back into repository
            reservationRepository.save(oldReservation);
            return false;
        }
    }

    public Reservation calculatePayment(Reservation reservation, Room room) {
        Payment newPayment = new Payment();
        Long checkIn = reservation.getCheckIn().getTime();
        Long checkOut = reservation.getCheckOut().getTime();
        newPayment.setCharged(((checkOut - checkIn) / 86400000.0 * room.getPrice()));
        newPayment.setReservationCode(reservation.getCode());
        newPayment.setCrNumber(reservation.getCrNumber());
        paymentService.insert(newPayment);
//        changeNextAvailable(reservation.getRoom());
        return reservation;
    }

    public Reservation cancel(int code) {
        // For canceling reservation (returns cancelled reservation to display details)
        Reservation reservation = findReservationByCode(code);
        reservation.setCanceled(true);
        reservationRepository.save(reservation);
        return reservation;
    }

    public Reservation changeReservation(int code, int room, Date checkin, Date checkout) {
        // For changing a reservation
        Reservation reservation = findReservationByCode(code);
        reservation.setCheckIn(checkin);
        reservation.setCheckOut(checkout);
        reservation.setRoom(room);
        System.out.println(reservation.getCheckIn());
        if(insert(reservation)) {
            reservationRepository.save(reservation);
            return reservation;
        }
        else{
            System.out.println("unable to change reservation");
            return null;
        }
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void changeNextAvailable(int roomNumber) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Calendar calendar = Calendar.getInstance();
        java.util.Date currentDate = calendar.getTime();
        java.sql.Date date = new java.sql.Date(currentDate.getTime());
        boolean check = !isRoomAvailableOn(date, roomNumber);
        if (check) {
            List<Reservation> reservations = getCheckInsAndCheckOuts(roomNumber);
            boolean flag = false;
            int i = 0;
            log.info("changing next available date for room #" + roomNumber);
            while (!flag) {
                if (reservations.size() > 2 && reservations.get(i).getCheckOut().equals(reservations.get(i + 1).getCheckIn())) {
                    log.info(reservations.get(i + 1).getCheckIn());
                    i++;
                } else {
                    flag = true;
                    Room room = roomService.getRoomByRoomNumber(roomNumber);
                    log.info("checkout: "+reservations.get(i).getCheckOut());
                    room.setNextAvailable(reservations.get(i).getCheckOut());
                    roomService.insert(room);
                    System.out.println(roomService.getRoomByRoomNumber(roomNumber));
                    return;
                }
            }
        } else {
            log.info("AVAILABLE TODAY: " + date + " " + roomNumber);
            Room room = roomService.getRoomByRoomNumber(roomNumber);
            room.setNextAvailable(date);
            roomService.insert(room);
        }
    }

    private Boolean isRoomAvailableOn(Date date, int roomNumber) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean available = false;
        Connection conn = null;
        try{
             conn = DriverManager.getConnection("jdbc:mysql://csc365.toshikuboi.net:3306/sec03group01",
                    "sec03group01", "group01@sec03");
            preparedStatement = conn.prepareStatement(
                    "SELECT * FROM room JOIN reservation ON room_number = room AND room = (?) AND canceled = false " +
                            "WHERE (?) >= check_in AND (?) < check_out"
            );
            preparedStatement.setInt(1, roomNumber);
            preparedStatement.setDate(2, date);
            preparedStatement.setDate(3, date);

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
                conn.close();
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
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        PreparedStatement preparedStatement = null;
        List<Reservation> reservations = null;
        ResultSet resultSet = null;
        Connection conn = null;
        try{
            conn = DriverManager.getConnection("jdbc:mysql://csc365.toshikuboi.net:3306/sec03group01",
                    "sec03group01", "group01@sec03");
            preparedStatement = conn.prepareStatement(
                    "SELECT check_in, check_out FROM room JOIN reservation ON room = room_number AND canceled = false " +
                            "WHERE room_number = (?) AND check_out >= CURDATE() ORDER BY check_in"
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
                conn.close();
                resultSet.close();
                preparedStatement.close();
            }catch(SQLException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return reservations;
    }

    public List<RoomReservation> getAllUserReservations(String firstname, String lastname) {
        // For displaying a particular users entire list of reservations
        PreparedStatement preparedStatement = null;
        List<RoomReservation> reservations = null;
        ResultSet resultSet = null;
        Connection conn = null;
        try{
            conn = DriverManager.getConnection("jdbc:mysql://csc365.toshikuboi.net:3306/sec03group01",
                    "sec03group01", "group01@sec03");
            preparedStatement = conn.prepareStatement(
                    "SELECT * FROM room JOIN reservation ON room = room_number " +
                            "WHERE first_name = (?) AND last_name = (?) ORDER BY check_in"
            );
            preparedStatement.setString(1, firstname);
            preparedStatement.setString(2, lastname);

            resultSet = preparedStatement.executeQuery();
            reservations = unpackResultSetRes(resultSet);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally{
            try {
                conn.close();
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
                    rs.getDate("check_out")
                    );
            reservations.add(reservation);
        }
        return reservations;
    }

    private List<RoomReservation> unpackResultSetRes(ResultSet rs) throws SQLException {
        List<RoomReservation> roomReservations = new ArrayList<>();
        while(rs.next()) {
            RoomReservation roomReservation = new RoomReservation(
                    rs.getInt("code"),
                    rs.getInt("room"),
                    rs.getInt("adults"),
                    rs.getInt("kids"),
                    rs.getDate("check_in"),
                    rs.getDate("check_out"),
                    rs.getBoolean("canceled"),
                    rs.getString("last_name"),
                    rs.getString("first_name"),
                    rs.getLong("cr_number"),
                    rs.getString("pictureurl")
            );
            roomReservations.add(roomReservation);
        }
        return roomReservations;
    }

    public List<RoomRevenue> getAllRoomsYearMonthRevenue() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<RoomRevenue> roomRevenues = null;
        Connection conn = null;
        try{
            conn = DriverManager.getConnection("jdbc:mysql://csc365.toshikuboi.net:3306/sec03group01",
                    "sec03group01", "group01@sec03");
            preparedStatement = conn.prepareStatement(
                    "SELECT room_number, YEAR(check_out) AS y, MONTHNAME(check_out) AS m, SUM(DATEDIFF(check_out, check_in) * price) AS revenue " +
                            "FROM reservation " +
                            "JOIN room ON room_number=room " +
                            "WHERE canceled<>1 " +
                            "GROUP BY room_number, YEAR(check_out), MONTHNAME(check_out)" +
                            "ORDER BY room_number"
            );
            resultSet = preparedStatement.executeQuery();
            roomRevenues = unpackRoomRevenueResultSet(resultSet);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            try {
                conn.close();
                resultSet.close();
                preparedStatement.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return roomRevenues;
    }

    private List<RoomRevenue> unpackRoomRevenueResultSet(ResultSet rs) throws SQLException {
        List<RoomRevenue> roomRevenues = new ArrayList<>();
        while(rs.next()) {
            RoomRevenue roomRevenue = new RoomRevenue(
                    rs.getInt("room_number"),
                    rs.getInt("y"),
                    rs.getString("m"),
                    rs.getDouble("revenue")
            );
            roomRevenues.add(roomRevenue);
        }
        return roomRevenues;
    }
}
