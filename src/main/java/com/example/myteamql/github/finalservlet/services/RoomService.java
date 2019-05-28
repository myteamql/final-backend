package com.example.myteamql.github.finalservlet.services;

import com.example.myteamql.github.finalservlet.entities.Room;
import com.example.myteamql.github.finalservlet.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.*;
import java.util.ArrayList;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getAllRooms(){
        return roomRepository.findAll();
    }

    public List<Room> getAllRoomsByType(String type) {
        return roomRepository.findAllByTypeEquals(type);
    }

    public List<Room> getAllRoomsByDecor(String decor) {
        return roomRepository.findAllByDecorEquals(decor);
    }

    public List<Room> getAllRoomsByMaxOccupants(int occupants) {
        return roomRepository.findAllByMaxOccupantsGreaterThanEqual(occupants);
    }

    public void insert(Room room) {
        roomRepository.save(room);
    }

    public List<Room> getAllRoomsByAvailability(Date checkin, Date checkout) {

        PreparedStatement preparedStatement = null;
        List<Room> rooms = null;
        ResultSet resultSet = null;
        try{
            Connection conn = DriverManager.getConnection("jdbc:mysql://csc365.toshikuboi.net:3306/sec03group01",
                    "sec03group01", "group01@sec03");
            boolean reachable = conn.isValid(5);
            System.out.println(reachable);
            preparedStatement = conn.prepareStatement("SELECT * FROM room r WHERE r.room_number NOT IN " +
                    "(SELECT distinct r.room_number FROM reservation re JOIN room r ON room_number=room " +
                    "WHERE re.check_in between (?)  AND (?) OR re.check_out between (?)  AND (?))");
            preparedStatement.setDate(1, checkin);
            preparedStatement.setDate(2, checkout);
            preparedStatement.setDate(3, checkin);
            preparedStatement.setDate(4, checkout);
            //preparedStatement = conn.prepareStatement("describe room");
            resultSet = preparedStatement.executeQuery();
            /*while(resultSet.next()){
                System.out.println(resultSet.getString(1));
            }*/

            rooms = unpackResultSet(resultSet);
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

        return rooms;
    }

    /*public List<Room> getRooms(Date checkin, Date checkout, int occupants, String type,
                               String decor, float price_floor, float price_ceiling) {

        PreparedStatement preparedStatement = null;
        List<Room> rooms = null;
        ResultSet resultSet = null;
        try{
            Connection conn = DriverManager.getConnection("jdbc:mysql://csc365.toshikuboi.net:3306/sec03group01",
                    "sec03group01", "group01@sec03");
            boolean reachable = conn.isValid(5);
            System.out.println(reachable);
            preparedStatement = conn.prepareStatement("SELECT * FROM " + getAllRoomsByAvailability(checkin, checkout)
                    +" r JOIN " + getAllRoomsByDecor(decor) + " r1 ON r.room_number=r1.room_number "
            + "JOIN " + getAllRoomsByMaxOccupants(occupants) + " r2 ON r.room_number=r2.room_number JOIN (?) r3 ON r.room_number=r3.room_number "
            + "JOIN (?) r4 ON r.room_number=r4.roomnumber");
            //preparedStatement = conn.prepareStatement("describe room");
            resultSet = preparedStatement.executeQuery();


            rooms = unpackResultSet(resultSet);
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

        return rooms;
    }

    public String getAllRoomsByAvailabilityQuery(Date checkin, Date checkout) {
        return ""
    }*/

    private List<Room> unpackResultSet(ResultSet rs) throws SQLException {
        List<Room> rooms = new ArrayList<Room>();

        while(rs.next()) {
            Room room = new Room(
                    rs.getInt("room_number"),
                    rs.getInt("max_occupants"),
                    rs.getString("type"),
                    rs.getString("decor"),
                    rs.getFloat("price"),
                    rs.getInt("beds"),
                    rs.getFloat("length"),
                    rs.getFloat("popularity"));

            rooms.add(room);
        }
        return rooms;
    }
}
