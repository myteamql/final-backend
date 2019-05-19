package com.example.myteamql.github.finalservlet.controllers;

import com.example.myteamql.github.finalservlet.entities.CreditCard;
import com.example.myteamql.github.finalservlet.services.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    @GetMapping(value = "/test")
    @CrossOrigin
    public String index() {
        return "this is from the backend!";
    }

    @GetMapping(value = "/creditcards")
    @CrossOrigin
    public List<CreditCard> cards() {
        List<CreditCard> cards = creditCardService.getAllCreditCards();
        return cards;
    }

    @PostMapping(value = "/creditcard")
    @CrossOrigin
    public CreditCard saveCard(@RequestBody CreditCard card) {
        creditCardService.insert(card);
        return card;
    }
}
