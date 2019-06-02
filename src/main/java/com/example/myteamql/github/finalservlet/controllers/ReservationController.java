package com.example.myteamql.github.finalservlet.controllers;

import com.example.myteamql.github.finalservlet.entities.Payment;
import com.example.myteamql.github.finalservlet.entities.Reservation;
import com.example.myteamql.github.finalservlet.entities.Room;
import com.example.myteamql.github.finalservlet.services.ReservationService;
import com.example.myteamql.github.finalservlet.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.TimeZone;

@RestController
public class ReservationController {

    private ReservationService reservationService;
    private CreditCardController creditCardController;
    private PaymentController paymentController;
    private RoomService roomService;

    @Autowired
    public ReservationController(ReservationService reservationService, CreditCardController creditCardController, PaymentController paymentController, RoomService roomService) {
        this.reservationService = reservationService;
        this.creditCardController = creditCardController;
        this.paymentController = paymentController;
        this.roomService = roomService;
    }

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
                return reservationService.calculatePayment(reservation, room);
            }
        } else {
            System.out.println("Not a valid credit card.");
            /* not a valid credit card */
        }
        return null;
    }

    @PutMapping(value = "/reservation")
    @CrossOrigin
    public Reservation changeReservation(@RequestBody Reservation newReservation) throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Reservation oldReservation = reservationService.findReservationByCode(newReservation.getCode());
        newReservation.setFirstName(newReservation.getFirstName().toLowerCase());
        newReservation.setLastName(newReservation.getLastName().toLowerCase());
        if (creditCardController.validateCard(newReservation.getCrNumber(), newReservation.getFirstName(), newReservation.getLastName())) {
            newReservation.setCanceled(false);
            Room room = roomService.getRoomByRoomNumber(newReservation.getRoom());
            if (reservationService.replace(newReservation, oldReservation)) {
                return reservationService.calculatePayment(newReservation, room);
            }
        } else {
            System.out.println("Not a valid credit card.");
            /* not a valid credit card */
        }
        return null;
    }

    @PutMapping(value = "/reservation/cancel/{code}")
    @CrossOrigin
    public Reservation cancelReservation(@PathVariable("code") int code) {
        return reservationService.cancel(code);
    }

    @GetMapping(value = "/revenue")
    @CrossOrigin
    public ResponseEntity getAllRoomsYearMonthRevenue() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.getAllRoomsYearMonthRevenue());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
    }
}
