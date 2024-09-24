package com.java.bank.repositories;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Integer> {

    Optional<Card> findByCardNumber(String cardNumber);

    Optional<Card> findByBankAccount(BankAccount bankAccount);

}
