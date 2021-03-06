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
        card.setFirst(card.getFirst().toLowerCase());
        card.setLast(card.getLast().toLowerCase());
        creditCardService.insert(card);
        return card;
    }

    @GetMapping(value = "/validate/{crNumber}/{first}/{last}")
    @CrossOrigin
    public Boolean validateCard(@PathVariable("crNumber") Long crNumber,
                                @PathVariable("first") String first,
                                @PathVariable("last") String last) {
        Boolean cardIsValid = creditCardService.validate(crNumber, first.toLowerCase(), last.toLowerCase());
        return cardIsValid;
    }
}
