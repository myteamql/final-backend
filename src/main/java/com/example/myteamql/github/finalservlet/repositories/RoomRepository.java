package com.example.myteamql.github.finalservlet.repositories;

import com.example.myteamql.github.finalservlet.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findAllByTypeEquals(String type);
    List<Room> findAllByDecorEquals(String decor);
    List<Room> findAllByMaxOccupantsGreaterThanEqual(int occupants);
}
