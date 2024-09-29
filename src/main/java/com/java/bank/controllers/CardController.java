package com.java.bank.controllers;

import com.java.bank.controllers.DTO.BankAccountDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import com.java.bank.services.BankAccountService;
import com.java.bank.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/card-service")
@RestController
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @GetMapping()
    public String getAllCards(BankAccount idBankAccount) {
        cardService.getAllCardsByBankAccount(idBankAccount);
        return "/card-service/index";
    }

    @PostMapping("/create-card")
    public void createCard(@RequestBody BankAccount bankAccount) {
        cardService.createCard(bankAccount);
    }

    @DeleteMapping("/delete-card/{cardId}")
    public void deleteCurrentCard(@PathVariable int cardId) {
        cardService.deleteCard(cardId);
    }

    @PutMapping("/cash-in")
    public void cashIn(@RequestBody int cardId, float amount) {
        cardService.cashIn(cardId, amount);
    }

    @PutMapping("/cash-out")
    public void cashOut(@RequestBody int cardId, float amount) {
        cardService.cashOut(cardId, amount);
    }

    @GetMapping("/send/{cardId}")
    public String showForm(@PathVariable int cardId) {
        return "/card-service/send";
    }

    @PutMapping("/send")
    public void send(@RequestBody int cardId, float amount, String recieverCardNum) {
        cardService.sendMoneyOnOtherCard(cardId, amount, recieverCardNum);
    }

}
