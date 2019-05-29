package com.example.myteamql.github.finalservlet.repositories;

import com.example.myteamql.github.finalservlet.entities.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    Boolean existsByCrNumberAndAndFirstAndLast(Long crNumber, String first, String last);
}
