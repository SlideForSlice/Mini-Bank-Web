package com.java.bank.controllers;

import com.java.bank.DTO.BankAccountDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import com.java.bank.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/card-service")
@Controller
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @GetMapping
    public Optional<Card> getAllCards(BankAccount idBankAccount) {
        return cardService.getAllCardsByBankAccount(idBankAccount);
        //TODO Добавить ссылку на страницу
    }

    @PostMapping("{idBankAccount}")
    public void createCard(@PathVariable BankAccount idBankAccount) {
        cardService.createCard(idBankAccount);
    }


}
