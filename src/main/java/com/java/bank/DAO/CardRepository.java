package com.java.bank.DAO;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

    Optional<Card> findByCardNumber(String cardNumber);

    Optional<Card> findByBankAccount(BankAccount bankAccount);

    List<Card> findAllByBankAccount(BankAccount bankAccount);


}
