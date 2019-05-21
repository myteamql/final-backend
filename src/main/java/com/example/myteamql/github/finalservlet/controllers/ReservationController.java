package com.example.myteamql.github.finalservlet.controllers;

import com.example.myteamql.github.finalservlet.entities.Payment;
import com.example.myteamql.github.finalservlet.entities.Reservation;
import com.example.myteamql.github.finalservlet.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private CreditCardController creditCardController;
    @Autowired
    private PaymentController paymentController;

    @GetMapping(value = "/reservation/{code}")
    @CrossOrigin
    public Reservation getReservation(@PathVariable("code") int code) {
        return reservationService.findReservationByCode(code);
    }

    @PostMapping(value = "/reservation")
    @CrossOrigin
    public Reservation createReservation(@RequestBody Reservation reservation) {
        if (creditCardController.validateCard(reservation.getCrNumber())) {
            reservationService.insert(reservation);
            Payment newPayment = new Payment();
            Long checkIn = reservation.getCheckIn().getTime();
            Long checkOut = reservation.getCheckOut().getTime();
            newPayment.setCharged(((checkOut - checkIn) / 86400000.0 * reservation.getRate()));
            newPayment.setReservationCode(reservation.getCode());
            newPayment.setCrNumber(reservation.getCrNumber());

            paymentController.pay(newPayment);
        } else {
            /* not a valid credit card */
        }
        return reservation;
    }

    @PutMapping(value = "/reservation/{code}")
    @CrossOrigin
    public void cancelReservation(@PathVariable("code") int code) {
        reservationService.cancel(code);
    }

}
