package com.example.myteamql.github.finalservlet.services;

import com.example.myteamql.github.finalservlet.entities.Room;
import com.example.myteamql.github.finalservlet.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return roomRepository.findAllByMaxOccupantsGreaterThan(occupants);
    }

    public void insert(Room room) {
        roomRepository.save(room);
    }
}
