package com.example.myteamql.github.finalservlet.controllers;

import com.example.myteamql.github.finalservlet.entities.Payment;
import com.example.myteamql.github.finalservlet.entities.Reservation;
import com.example.myteamql.github.finalservlet.entities.Room;
import com.example.myteamql.github.finalservlet.services.ReservationService;
import com.example.myteamql.github.finalservlet.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.TimeZone;

@RestController
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private CreditCardController creditCardController;
    @Autowired
    private PaymentController paymentController;
    @Autowired
    private RoomService roomService;

    @GetMapping(value = "/reservation/{code}")
    @CrossOrigin
    public Reservation getReservation(@PathVariable("code") int code) {
        return reservationService.findReservationByCode(code);
    }

    @GetMapping(value = "/reservations")
    @CrossOrigin
    public List<Reservation> reservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return reservations;
    }

    @PostMapping(value = "/reservation")
    @CrossOrigin
    public Reservation createReservation(@RequestBody Reservation reservation) throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        reservation.setFirstName(reservation.getFirstName().toLowerCase());
        reservation.setLastName(reservation.getLastName().toLowerCase());
        if (creditCardController.validateCard(reservation.getCrNumber(), reservation.getFirstName(), reservation.getLastName())) {
            reservation.setCanceled(false);
            Room room = roomService.getRoomByRoomNumber(reservation.getRoom());
            if (reservationService.insert(reservation)) {
                Payment newPayment = new Payment();
                Long checkIn = reservation.getCheckIn().getTime();
                Long checkOut = reservation.getCheckOut().getTime();
                newPayment.setCharged(((checkOut - checkIn) / 86400000.0 * room.getPrice()));
                newPayment.setReservationCode(reservation.getCode());
                newPayment.setCrNumber(reservation.getCrNumber());
                paymentController.pay(newPayment);
                reservationService.changeNextAvailable(reservation.getRoom());
                return reservation;
            }
        } else {
            System.out.println("Not a valid credit card.");
            /* not a valid credit card */
        }
        return null;
    }

    @PutMapping(value = "/reservation/{code}")
    @CrossOrigin
    public void cancelReservation(@PathVariable("code") int code) {
        reservationService.cancel(code);
    }

}
