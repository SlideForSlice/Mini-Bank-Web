package com.java.bank.controllers;

import com.java.bank.controllers.DTO.BankAccountIdDTO;
import com.java.bank.controllers.DTO.CardTransDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import com.java.bank.services.CardService;
import com.java.bank.utils.MapperForDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RequestMapping("/card-service")
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CardController {
    private final CardService cardService;
    private final MapperForDTO mapperForDTO;

    @GetMapping()
    public String getAllCards(BankAccount idBankAccount) {
        cardService.getAllCardsByBankAccount(idBankAccount);
        return "/card-service/index";
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createCard(@RequestBody BankAccountIdDTO idBankAccount) {

        int id = idBankAccount.getId();

        cardService.createCard(id);

        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<HttpStatus> deleteCurrentCard(@PathVariable int id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/cash-in")
    public Map<String, Float> cashIn(@PathVariable int id,
                                     @RequestParam float amount) {
        cardService.cashIn(id, amount);

        Optional<Card> card = cardService.getById(id);

        return Map.of("balance", card.get().getBalance());
    }

    @PatchMapping("/{id}/cash-out")
    public Map<String, Float> cashOut(@PathVariable int id,
                        @RequestParam float amount) {

        cardService.cashOut(id, amount);

        Optional<Card> card = cardService.getById(id);

        return Map.of("balance", card.get().getBalance());
    }


    @PatchMapping("/{id}/send")
    public Map<String, Float> send(@PathVariable int id,
                     @RequestParam float amount,
                     @RequestBody CardTransDTO cardTransDTO) {

        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();

        cardService.sendMoneyOnOtherCard(id, amount, cardNumber);

        Optional<Card> card = cardService.getById(id);

        return Map.of("balance", card.get().getBalance());
    }

}
