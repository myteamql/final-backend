package com.example.myteamql.github.finalservlet.services;

import com.example.myteamql.github.finalservlet.entities.Reservation;
import com.example.myteamql.github.finalservlet.entities.Room;
import com.example.myteamql.github.finalservlet.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        System.out.println("room : "+roomNum);
        Room room = roomService.getRoomByRoomNumber(roomNum);
        System.out.println("room: " + room);
        List<Room> availables = roomService.getAllRoomsByAvailability(reservation.getCheckIn(), reservation.getCheckOut());
        if (room.getMaxOccupants() >= reservation.getAdults() + reservation.getKids() &&
        availables.contains(room)) {
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
}
