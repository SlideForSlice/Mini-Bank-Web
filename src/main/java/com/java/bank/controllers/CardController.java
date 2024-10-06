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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RequestMapping("/card-service")
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Api(value = "Card Service API", tags = {"Card Service"})
public class CardController {
    private final CardService cardService;
    private final MapperForDTO mapperForDTO;

    @GetMapping()
    @ApiOperation(value = "Get all cards for a bank account", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved cards"),
            @ApiResponse(code = 404, message = "Bank account not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public String getAllCards(
            @ApiParam(value = "Bank account to retrieve cards for", required = true)
            BankAccount idBankAccount) {
        cardService.getAllCardsByBankAccount(idBankAccount);
        return "/card-service/index";
    }

    @PostMapping("/create")
    @ApiOperation(value = "Create a new card for a bank account", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Card created successfully"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<HttpStatus> createCard(
            @ApiParam(value = "Bank account ID to create card for", required = true)
            @RequestBody BankAccountIdDTO idBankAccount) {
        int id = idBankAccount.getId();
        cardService.createCard(id);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    @ApiOperation(value = "Delete a card by ID", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Card deleted successfully"),
            @ApiResponse(code = 404, message = "Card not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<HttpStatus> deleteCurrentCard(
            @ApiParam(value = "ID of the card to delete", required = true)
            @PathVariable int id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/cash-in")
    @ApiOperation(value = "Cash in to a card by ID", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cash in successful"),
            @ApiResponse(code = 404, message = "Card not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Map<String, Float> cashIn(
            @ApiParam(value = "ID of the card to cash in", required = true)
            @PathVariable int id,
            @ApiParam(value = "Amount to cash in", required = true)
            @RequestParam float amount) {
        cardService.cashIn(id, amount);
        Optional<Card> card = cardService.getById(id);
        return Map.of("balance", card.get().getBalance());
    }

    @PatchMapping("/{id}/cash-out")
    @ApiOperation(value = "Cash out from a card by ID", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cash out successful"),
            @ApiResponse(code = 404, message = "Card not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Map<String, Float> cashOut(
            @ApiParam(value = "ID of the card to cash out", required = true)
            @PathVariable int id,
            @ApiParam(value = "Amount to cash out", required = true)
            @RequestParam float amount) {
        cardService.cashOut(id, amount);
        Optional<Card> card = cardService.getById(id);
        return Map.of("balance", card.get().getBalance());
    }

    @PatchMapping("/{id}/send")
    @ApiOperation(value = "Send money from one card to another", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Money sent successfully"),
            @ApiResponse(code = 404, message = "Card not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Map<String, Float> send(
            @ApiParam(value = "ID of the card to send money from", required = true)
            @PathVariable int id,
            @ApiParam(value = "Amount to send", required = true)
            @RequestParam float amount,
            @ApiParam(value = "Card details to send money to", required = true)
            @RequestBody CardTransDTO cardTransDTO) {
        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        cardService.sendMoneyOnOtherCard(id, amount, cardNumber);
        Optional<Card> card = cardService.getById(id);
        return Map.of("balance", card.get().getBalance());
    }
}
