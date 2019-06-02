package com.example.myteamql.github.finalservlet.services;

import com.example.myteamql.github.finalservlet.entities.Room;
import com.example.myteamql.github.finalservlet.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;

@Service
public class RoomService {

  private RoomRepository roomRepository;

  @Autowired
  public RoomService(RoomRepository roomRepository) {
    this.roomRepository = roomRepository;
  }

  public List<Room> getAllRooms() {
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

  public Room getRoomByRoomNumber(int room_number) {
    return roomRepository.getRoomByRoomNumber(room_number);
  }

  public void insert(Room room) {
    roomRepository.save(room);
  }

  public List<Room> getAllRoomsByAvailability(Date checkin, Date checkout) {
    PreparedStatement preparedStatement = null;
    List<Room> rooms = null;
    ResultSet resultSet = null;
    Connection conn = null;
    try {
      conn =
          DriverManager.getConnection(
              "jdbc:mysql://csc365.toshikuboi.net:3306/sec03group01",
              "sec03group01",
              "group01@sec03");
      preparedStatement =
          conn.prepareStatement(
              "SELECT * FROM room r WHERE r.room_number NOT IN "
                  + "(SELECT distinct r.room_number FROM reservation re JOIN room r ON room_number=room AND canceled=false "
                  + "WHERE (re.check_in > (?)  AND re.check_in <= (?)) OR (re.check_out > (?)  AND re.check_out <= (?)))");
      preparedStatement.setDate(1, checkin);
      preparedStatement.setDate(2, checkout);
      preparedStatement.setDate(3, checkin);
      preparedStatement.setDate(4, checkout);

      resultSet = preparedStatement.executeQuery();
      rooms = unpackResultSet(resultSet);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      try {
        conn.close();
        resultSet.close();
        preparedStatement.close();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
    return rooms;
  }

  public List<Room> getRooms(
      Date checkin,
      Date checkout,
      int occupants,
      String type,
      String decor,
      float price_floor,
      float price_ceiling) {
    PreparedStatement preparedStatement = null;
    List<Room> rooms = null;
    ResultSet resultSet = null;
    Connection conn = null;
    try {
      conn =
          DriverManager.getConnection(
              "jdbc:mysql://csc365.toshikuboi.net:3306/sec03group01",
              "sec03group01",
              "group01@sec03");

      List<Room> rooms_pop = getPopularityScore(conn);
      for (Room r : rooms_pop) {
        Room room = getRoomByRoomNumber(r.getRoomNumber());
        room.setPopularity(r.getPopularity());
        insert(room);
      }

      String preparedString =
          "SELECT * FROM "
              + getAllRoomsByAvailabilityQuery(checkin, checkout)
              + "JOIN "
              + getAllRoomsByDecorQuery(decor)
              + " ON r.room_number=r1.room_number "
              + "JOIN "
              + getAllRoomsByMaxOccupantsQuery(occupants)
              + " ON r.room_number=r2.room_number "
              + "JOIN "
              + getAllRoomsByTypeQuery(type)
              + " ON r.room_number=r3.room_number "
              + "JOIN "
              + getAllRoomsByPriceRangeQuery(price_floor, price_ceiling)
              + " ON r.room_number=r4.room_number";
      preparedStatement = conn.prepareStatement(preparedString);
      int i = 1;
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      if (!checkin.equals(sdf.parse("0001-01-01")) && !checkout.equals(sdf.parse("0001-01-01"))) {
        preparedStatement.setDate(i, checkin);
        i++;
        preparedStatement.setDate(i, checkout);
        i++;
        preparedStatement.setDate(i, checkin);
        i++;
        preparedStatement.setDate(i, checkout);
        i++;
      }
      if (!decor.equals("null")) {
        preparedStatement.setString(i, decor);
        i++;
      }
      if (occupants != -1) {
        preparedStatement.setInt(i, occupants);
        i++;
      }
      if (!type.equals("null")) {
        preparedStatement.setString(i, type);
        i++;
      }
      if (price_floor != -1 && price_ceiling != -1) {
        preparedStatement.setFloat(i, price_floor);
        i++;
        preparedStatement.setFloat(i, price_ceiling);
        i++;
      }

      resultSet = preparedStatement.executeQuery();
      rooms = unpackResultSet(resultSet);

    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      try {
        conn.close();
        resultSet.close();
        preparedStatement.close();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
    return rooms;
  }

  private List<Room> getPopularityScore(Connection conn) {
    PreparedStatement preparedStatement = null;
    List<Room> rooms = null;
    ResultSet resultSet = null;
    try {
      String preparedString =
          "SELECT room, ROUND((SUM(DATEDIFF(check_out, check_in) "
              + "- GREATEST(DATEDIFF(DATE_SUB(CURDATE(), INTERVAL 180 DAY), check_in), 0)))/180,2) as pop "
              + "FROM room  "
              + "JOIN reservation "
              + "ON room=room_number "
              + "WHERE DATEDIFF(CURDATE(), check_out)<=180 "
              + "GROUP BY room";
      // String preparedString = "describe room";
      preparedStatement = conn.prepareStatement(preparedString);

      resultSet = preparedStatement.executeQuery();
      rooms = unpackResultSetPopularity(resultSet);

    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      try {
        resultSet.close();
        preparedStatement.close();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
    return rooms;
  }

  private String getAllRoomsByPriceRangeQuery(float price_floor, float price_ceiling) {
    if (price_floor == -1 || price_ceiling == -1) {
      return "(SELECT * FROM room) r4 ";
    } else {
      return "(SELECT * FROM room WHERE price BETWEEN (?) AND (?)) r4 ";
    }
  }

  private String getAllRoomsByMaxOccupantsQuery(int occupants) {
    if (occupants == -1) {
      return "(SELECT * FROM room) r2 ";
    } else {
      return "(SELECT * FROM room WHERE max_occupants >= ?) r2 ";
    }
  }

  private String getAllRoomsByTypeQuery(String type) {
    if (type.equals("null")) {
      return "(SELECT * FROM room) r3 ";
    } else {
      return "(SELECT * FROM room WHERE type = ?) r3 ";
    }
  }

  private String getAllRoomsByDecorQuery(String decor) {
    if (decor.equals("null")) {
      return "(SELECT * FROM room) r1 ";
    } else {
      return "(SELECT * FROM room WHERE decor = ?) r1 ";
    }
  }

  private String getAllRoomsByAvailabilityQuery(Date checkin, Date checkout) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    if (checkin.equals(sdf.parse("0001-01-01")) || checkout.equals(sdf.parse("0001-01-01"))) {
      return "(SELECT * FROM room) r ";
    } else {
      return "(SELECT * FROM room r WHERE r.room_number NOT IN "
          + "(SELECT distinct r.room_number FROM reservation re JOIN room r ON room_number=room AND canceled=false "
          + "WHERE (re.check_in > (?) AND re.check_in <= (?)) OR (re.check_out > (?) AND re.check_out <= (?)))) r ";
    }
  }

  private List<Room> unpackResultSet(ResultSet rs) throws SQLException {
    List<Room> rooms = new ArrayList<>();
    while (rs.next()) {
      Room room =
          new Room(
              rs.getInt("room_number"),
              rs.getInt("max_occupants"),
              rs.getString("type"),
              rs.getString("decor"),
              rs.getFloat("price"),
              rs.getInt("beds"),
              rs.getFloat("length"),
              rs.getFloat("popularity"),
              rs.getString("pictureurl"),
              rs.getDate("next_available"));

      rooms.add(room);
    }
    return rooms;
  }

  private List<Room> unpackResultSetPopularity(ResultSet rs) throws SQLException {
    List<Room> rooms = new ArrayList<>();
    while (rs.next()) {
      Room room = new Room(rs.getInt("room"), rs.getFloat("pop"));

      rooms.add(room);
    }
    return rooms;
  }
}
