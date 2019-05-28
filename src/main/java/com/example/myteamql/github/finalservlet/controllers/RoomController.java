package com.example.myteamql.github.finalservlet.controllers;

import com.example.myteamql.github.finalservlet.entities.Room;
import com.example.myteamql.github.finalservlet.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.sql.Date;

import java.util.List;

@RestController
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping(value = "/rooms")
    @CrossOrigin
    public List<Room> rooms() {
        List<Room> rooms = roomService.getAllRooms();
        return rooms;
    }

    @GetMapping(value = "/roomtype/{type}")
    @CrossOrigin
    public List<Room> searchRoomsByType(@PathVariable("type") String type) {
        List<Room> rooms = roomService.getAllRoomsByType(type);
        return rooms;
    }

    @GetMapping(value = "/roomdecor/{decor}")
    @CrossOrigin
    public List<Room> searchRoomsByDecor(@PathVariable("decor") String decor) {
        List<Room> rooms = roomService.getAllRoomsByDecor(decor);
        return rooms;
    }

    @GetMapping(value = "/roomoccupants/{occupants}")
    @CrossOrigin
    public List<Room> searchRoomsByOccupants(@PathVariable("occupants") int occupants) {
        List<Room> rooms = roomService.getAllRoomsByMaxOccupants(occupants);
        return rooms;
    }

    @PostMapping(value = "/room")
    @CrossOrigin
    public Room createRoom(@RequestBody Room room) {
        roomService.insert(room);
        return room;
    }

    @GetMapping(value = "/roomavailable/{checkin}/{checkout}")
    @CrossOrigin
    public List<Room> searchRoomsByAvailability(@PathVariable("checkin") Date checkin, @PathVariable("checkout") Date checkout) {
        List<Room> rooms = roomService.getAllRoomsByAvailability(checkin, checkout);
        return rooms;
    }

    /*@GetMapping(value = "/rooms/{checkin}/{checkout}/{occupants}/{type}/{decor}/{price_floor}/{price_ceiling}")
    @CrossOrigin
    public List<Room> searchRooms(@PathVariable("checkin") Date checkin, @PathVariable("checkout") Date checkout,
                                  @PathVariable("occupants") int occupants, @PathVariable("type") String type,
                                  @PathVariable("decor") String decor, @PathVariable("price_floor") float price_floor,
                                  @PathVariable("price_ceiling") float price_ceiling) {
        List<Room> rooms = roomService.getRooms(checkin, checkout, occupants, type, decor, price_floor, price_ceiling);
        return rooms;
    }*/

}
