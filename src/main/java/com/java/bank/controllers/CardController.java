package com.java.bank.controllers;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import com.java.bank.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/card-service")
@Controller
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @GetMapping()
    public Optional<Card> getAllCards(BankAccount idBankAccount) {
        return cardService.getAllCardsByBankAccount(idBankAccount);
        //TODO Добавить ссылку на страницу
    }

    @PostMapping("/create-card/{idBankAccount}")
    public void createCard(@PathVariable BankAccount idBankAccount) {
        cardService.createCard(idBankAccount);
    }

    @DeleteMapping("/delete-card/{cardId}")
    public void deleteCurrentCard(@PathVariable int cardId) {
        cardService.deleteCard(cardId);
    }

    @PutMapping("/cash-in/{cardId}")
    public void cashIn(@PathVariable int cardId, float amount) {
        cardService.cashIn(cardId, amount);
    }

    @PutMapping("/cash-out/{cardId}")
    public void cashOut(@PathVariable int cardId, float amount) {
        cardService.cashOut(cardId, amount);
    }

    @PutMapping("/send/{cardId}")
    public void send(@PathVariable int cardId, float amount, String recieverCardNum) {
        cardService.sendMoneyOnOtherCard(cardId, amount, recieverCardNum);
    }

}
