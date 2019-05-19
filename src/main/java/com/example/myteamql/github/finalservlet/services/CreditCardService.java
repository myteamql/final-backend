package com.example.myteamql.github.finalservlet.services;

import com.example.myteamql.github.finalservlet.entities.CreditCard;
import com.example.myteamql.github.finalservlet.repositories.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditCardService {
    @Autowired
    private CreditCardRepository creditCardRepository;

    public List<CreditCard> getAllCreditCards() {
        return creditCardRepository.findAll();
    }

    public void insert(CreditCard card) {
        creditCardRepository.save(card);
    }
}
