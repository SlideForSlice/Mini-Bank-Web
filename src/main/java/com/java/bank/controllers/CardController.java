package com.java.bank.controllers;

import com.java.bank.controllers.DTO.BankAccountIdDTO;
import com.java.bank.controllers.DTO.CardTransDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import com.java.bank.services.CardService;
import com.java.bank.utils.MapperForDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RequestMapping("/card-service")
@RestController
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    private final MapperForDTO mapperForDTO;

    @GetMapping()
    public String getAllCards(BankAccount idBankAccount) {
        cardService.getAllCardsByBankAccount(idBankAccount);
        return "/card-service/index";
    }

    @PostMapping("/create")
    public Map<String, String> createCard(@RequestBody BankAccountIdDTO idBankAccount) {

        int id = idBankAccount.getId();

        cardService.createCard(id);

        return Map.of("status", "success");
    }

    @DeleteMapping("/{cardId}/delete")
    public void deleteCurrentCard(@PathVariable int cardId) {
        cardService.deleteCard(cardId);
    }

    @PatchMapping("/{cardId}/cash-in")
    public Map<String, Float> cashIn(@PathVariable int cardId,
                                     @RequestParam float amount) {
        cardService.cashIn(cardId, amount);

        Optional<Card> card = cardService.getById(cardId);

        return Map.of("balance", card.get().getBalance());
    }

    @PatchMapping("/{cardId}/cash-out")
    public Map<String, Float> cashOut(@PathVariable int cardId,
                        @RequestParam float amount) {

        cardService.cashOut(cardId, amount);

        Optional<Card> card = cardService.getById(cardId);

        return Map.of("balance", card.get().getBalance());
    }


    @PatchMapping("/{cardId}/send")
    public Map<String, Float> send(@PathVariable int cardId,
                     @RequestParam float amount,
                     @RequestBody CardTransDTO cardTransDTO) {

        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();

        cardService.sendMoneyOnOtherCard(cardId, amount, cardNumber);

        Optional<Card> card = cardService.getById(cardId);

        return Map.of("balance", card.get().getBalance());
    }


}
