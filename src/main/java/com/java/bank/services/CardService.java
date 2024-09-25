package com.java.bank.services;


import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import com.java.bank.models.enums.CardStatus;
import com.java.bank.repositories.BankAccountRepository;
import com.java.bank.repositories.CardRepository;
import com.java.bank.utils.CardNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Optional<Card> getById(int id) {
        log.info("Get Card by id: " + id);
        return cardRepository.findById(id);
    }

    public List<Card> getAll() {
        log.info("Get All Cards");
        return cardRepository.findAll();
    }

    public Optional<Card> getAllCardsByBankAccount(BankAccount bankAccount) {
        log.info("Get Card by bank account: " + bankAccount);
        return cardRepository.findByBankAccount(bankAccount);
    }

    @Transactional
    public void createCard(BankAccount bankAccount) {
        log.info("Create Card");

        Card card = new Card();

        String cardNumber;
        do {
            cardNumber = CardNumberGenerator.generateCardNumber();
        } while (cardRepository.findByCardNumber(cardNumber).isPresent());

        cardRepository.findById(card.getId()).get().setCardNumber(cardNumber);
        cardRepository.findById(card.getId()).get().setBankAccount(bankAccount);
        cardRepository.findById(card.getId()).get().setBalance(0);
        cardRepository.findById(card.getId()).get().setStatus(CardStatus.ACTIVE);

        cardRepository.save(card);

    }

    @Transactional
    public void updateCard(Card card) {
        log.info("Update Card");
        cardRepository.save(card);
    }

    @Transactional
    public void deleteCard(int id) {
        log.info("Delete Card: " + id);
        cardRepository.deleteById(id);
    }

    @Transactional
    public void cashIn(int id, float amount) {
        log.info("Cash In card (" + id + ") on amount " + amount);
        float currentBalance = cardRepository.findById(id).get().getBalance();
        float newBalance = currentBalance + amount;
        cardRepository.findById(id).get().setBalance(newBalance);
        cardRepository.save(cardRepository.findById(id).get());
        log.info("New balance: " + newBalance);
    }

    @Transactional
    public void cashOut(int id, float amount) {
        log.info("Cash Out card (" + id + ") on amount " + amount);
        float currentBalance = cardRepository.findById(id).get().getBalance();
        float newBalance = currentBalance - amount;
        cardRepository.findById(id).get().setBalance(newBalance);
        cardRepository.save(cardRepository.findById(id).get());
        log.info("New balance: " + newBalance);
    }

    @Transactional
    public void sendMoneyOnOtherCard(int currentId, float amount, String recieverCardNum) {
        log.info("Send Money from ("+  currentId + ") on other card (" + recieverCardNum + ") an amount " + amount);
        float currentBalance = cardRepository.findById(currentId).get().getBalance();
        float newBalance = currentBalance - amount;
        cardRepository.findById(currentId).get().setBalance(newBalance);
        cardRepository.save(cardRepository.findById(currentId).get());

        float recieverBalance = cardRepository.findByCardNumber(recieverCardNum).get().getBalance();
        float newRecieverBalance = recieverBalance + amount;
        cardRepository.findByCardNumber(recieverCardNum).get().setBalance(newRecieverBalance);
        cardRepository.save(cardRepository.findByCardNumber(recieverCardNum).get());
        log.info("New current balance: " + newBalance);
        log.info("New reciever balance: " + newRecieverBalance);
    }










}
