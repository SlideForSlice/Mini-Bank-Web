package com.java.bank.controllers;

import com.java.bank.controllers.DTO.CardTransDTO;
import com.java.bank.models.Card;
import com.java.bank.security.JWTUtil;
import com.java.bank.services.CardService;
import com.java.bank.utils.MapperForDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/card-service")
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Tag(name = "Card Service API", description = "Card Service")
@SecurityRequirement(name = "JWT")
public class CardController {
    private final CardService cardService;
    private final MapperForDTO mapperForDTO;
    private final JWTUtil jwtUtil;

    @GetMapping()
    @Operation(summary = "Get all cards by bank account", security = @SecurityRequirement(name = "JWT"), responses = {@ApiResponse(responseCode = "200", description = "Successfully retrieved cards"), @ApiResponse(responseCode = "404", description = "Bank account not found"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public Optional<List<Card>> getAllCards(
            @RequestHeader("Authorization") String token) {
        List<Card> cards = cardService.getAllCardsByBankAccount(jwtUtil.extractBankAccountId(token.replace("Bearer ", "")));
        return cards.isEmpty() ? Optional.empty() : Optional.of(cards);
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new card for a bank account", responses = {@ApiResponse(responseCode = "201", description = "Card created successfully"), @ApiResponse(responseCode = "400", description = "Invalid input data"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public ResponseEntity<Card> createCard(
            @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token is not provided");
        }
        Card card = cardService.createCard(jwtUtil.extractBankAccountId(token.replace("Bearer ", "")));
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete a card by ID", responses = {@ApiResponse(responseCode = "200", description = "Card deleted successfully"), @ApiResponse(responseCode = "404", description = "Card not found"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public ResponseEntity<HttpStatus> deleteCurrentCard(
            @Parameter(description = "ID of the card to delete", required = true) @PathVariable int id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/cash-in")
    @Operation(summary = "Cash in to a card by ID", responses = {@ApiResponse(responseCode = "200", description = "Cash in successful"), @ApiResponse(responseCode = "404", description = "Card not found"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public Map<String, Float> cashIn(
            @Parameter(description = "ID of the card to cash in", required = true) @PathVariable int id,
            @Parameter(description = "Amount to cash in", required = true) @RequestParam float amount) {
        cardService.cashIn(id, amount);
        Optional<Card> card = cardService.getById(id);
        return Map.of("balance", card.get().getBalance());
    }

    @PatchMapping("/{id}/cash-out")
    @Operation(summary = "Cash out from a card by ID", responses = {@ApiResponse(responseCode = "200", description = "Cash out successful"), @ApiResponse(responseCode = "404", description = "Card not found"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public Map<String, Float> cashOut(
            @Parameter(description = "ID of the card to cash out", required = true) @PathVariable int id,
            @Parameter(description = "Amount to cash out", required = true) @RequestParam float amount) {
        cardService.cashOut(id, amount);
        Optional<Card> card = cardService.getById(id);
        return Map.of("balance", card.get().getBalance());
    }

    @PatchMapping("/{id}/send")
    @Operation(summary = "Send money from one card to another", responses = {@ApiResponse(responseCode = "200", description = "Money sent successfully"), @ApiResponse(responseCode = "404", description = "Card not found"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public Map<String, Float> send(
            @Parameter(description = "ID of the card to send money from", required = true) @PathVariable int id,
            @Parameter(description = "Amount to send", required = true) @RequestParam float amount,
            @Parameter(description = "Card details to send money to", required = true) @RequestBody CardTransDTO cardTransDTO) {
        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        cardService.sendMoneyOnOtherCard(id, amount, cardNumber);
        Optional<Card> card = cardService.getById(id);
        return Map.of("balance", card.get().getBalance());
    }
}