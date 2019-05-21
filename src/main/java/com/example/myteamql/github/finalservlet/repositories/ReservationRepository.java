package com.example.myteamql.github.finalservlet.repositories;

import com.example.myteamql.github.finalservlet.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    Reservation findByCode(int code);
}
