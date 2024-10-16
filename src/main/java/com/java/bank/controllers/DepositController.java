package com.java.bank.controllers;

import com.java.bank.controllers.DTO.CardTransDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.Deposit;
import com.java.bank.repositories.CardRepository;
import com.java.bank.security.JWTUtil;
import com.java.bank.services.DepositService;
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
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;


import java.util.Map;


@RequestMapping("/deposit-service")
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Tag(name = "Deposit controller", description = "CRUD for deposit controller")
@SecurityRequirement(name = "JWT")
public class DepositController {
    private final DepositService depositService;
    private final MapperForDTO mapperForDTO;
    private final CardRepository cardRepository;
    private final JWTUtil jwtUtil;

//    @GetMapping
//    @Operation(summary = "Get all deposits for a bank account")
//    public String getAllDeposits(
//            @Parameter(description = "Bank account to retrieve deposits for", required = true)
//            BankAccount bankAccount) {
//        depositService.getDepositsByBankAccount(bankAccount);
//        return "";
//    }

    @PostMapping("/create")
    @Operation(summary = "Create a new deposit for a bank account")
    public ResponseEntity<Deposit> createDeposit(
            @Parameter(description = "Enter JWT Token", required = true) @RequestHeader("Authorization") String token,
            @Parameter(description = "Deposit term in months", required = true) @RequestParam int depositTerm) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token is not provided");
        }
        Deposit deposit = depositService.createDeposit(jwtUtil.extractBankAccountId(token.replace("Bearer ", "")), depositTerm);
        return ResponseEntity.status(HttpStatus.CREATED).body(deposit);
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete a deposit by ID")
    public ResponseEntity<HttpStatus> deleteDeposit(
            @Parameter(description = "ID of the deposit to delete", required = true)
            @PathVariable int id) {
        depositService.deleteDeposit(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/cash-in")
    @Operation(summary = "Cash in to a deposit from a card")
    public Map<String, Float> cashIn(
            @Parameter(description = "ID of the deposit to cash in", required = true) @PathVariable int id,
            @Parameter(description = "Amount to cash in", required = true) @RequestParam float amount,
            @Parameter(description = "Card details to cash in from", required = true) @RequestBody CardTransDTO cardTransDTO)
    {
        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        float cardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
        if (cardBalance >= amount) {
            depositService.cashInToDepositFromCard(id, amount, cardNumber);
            float newCardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
            float newDepositBalance = depositService.getDepositById(id).get().getBalance();
            return Map.of("depositBalance", newDepositBalance,
                    "cardBalance", newCardBalance);
        } else {
            throw new RuntimeException("Not enough balance");
        }
    }

    @PatchMapping("/{id}/cash-out")
    @Operation(summary = "Cash out from a deposit to a card")
    public Map<String, Float> cashOut(
            @Parameter(description = "ID of the deposit to cash out", required = true) @PathVariable int id,
            @Parameter(description = "Amount to cash out", required = true) @RequestParam float amount,
            @Parameter(description = "Card details to cash out to", required = true) @RequestBody CardTransDTO cardTransDTO) {
        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        float depositBalance = depositService.getDepositById(id).get().getBalance();
        if (depositBalance >= amount) {
            depositService.cashOutToCardFromDeposit(id, amount, cardNumber);
            float newCardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
            float newDepositBalance = depositService.getDepositById(id).get().getBalance();
            return Map.of("depositBalance", newDepositBalance,
                    "cardBalance", newCardBalance);
        } else {
            throw new RuntimeException("Not enough deposit balance");
        }
    }
}