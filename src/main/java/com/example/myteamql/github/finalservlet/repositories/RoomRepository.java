package com.example.myteamql.github.finalservlet.repositories;

import com.example.myteamql.github.finalservlet.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
}
