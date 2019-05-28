package com.example.myteamql.github.finalservlet.services;

import com.example.myteamql.github.finalservlet.entities.Reservation;
import com.example.myteamql.github.finalservlet.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation findReservationByCode(int code) {
        return reservationRepository.findByCode(code);
    }

    public void insert(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public void cancel(int code) {
        Reservation reservation = findReservationByCode(code);
        reservation.setCanceled(true);
        reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
}
