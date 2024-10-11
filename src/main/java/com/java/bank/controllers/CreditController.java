package com.java.bank.controllers;

import com.java.bank.controllers.DTO.CardTransDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.repositories.CardRepository;
import com.java.bank.security.JWTUtil;
import com.java.bank.services.CardService;
import com.java.bank.services.CreditService;
import com.java.bank.utils.CreditErrorResponse;
import com.java.bank.utils.CreditPaidException;
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


import java.util.Map;

@RequestMapping("/credit-service")
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Tag(name = "F - Credit Service API", description = "Credit Service")
@SecurityRequirement(name = "JWT")
public class CreditController {

    private final CreditService creditService;
    private final MapperForDTO mapperForDTO;
    private final CardService cardService;
    private final CardRepository cardRepository;
    private final JWTUtil jwtUtil;

    @GetMapping()
    @Operation(summary = "Get all credits for a bank account", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved credits"),
            @ApiResponse(responseCode = "404", description = "Bank account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public String getAllCredits(
            @Parameter(description = "Bank account to retrieve credits for", required = true)
            BankAccount idBankAccount) {
        creditService.getCreditsByBankAccount(idBankAccount);
        return "";
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new credit for a bank account", responses = {
            @ApiResponse(responseCode = "201", description = "Credit created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<HttpStatus> createCredit(
            @Parameter(description = "Bank account ID to create credit for", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Credit term in months", required = true)
            @RequestParam int creditTerm) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token is not provided");
        }
        creditService.createCredit(jwtUtil.extractBankAccountId(token.replace("Bearer ", "")), creditTerm);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete a credit by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Credit deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Credit not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<HttpStatus> deleteCredit(
            @Parameter(description = "ID of the credit to delete", required = true)
            @PathVariable int id) {
        creditService.deleteCredit(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/cash-in")
    @Operation(summary = "Cash in to a credit from a card", responses = {
            @ApiResponse(responseCode = "200", description = "Cash in successful"),
            @ApiResponse(responseCode = "404", description = "Credit or card not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Map<String, Float> cashIn(
            @Parameter(description = "ID of the credit to cash in", required = true)
            @PathVariable int id,
            @Parameter(description = "Amount to cash in", required = true)
            @RequestParam float amount,
            @Parameter(description = "Card details to cash in from", required = true)
            @RequestBody CardTransDTO cardTransDTO) {
        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        float cardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
        float loanDebt = creditService.getCreditById(id).get().getLoanDebt();
        if ((loanDebt > 0 && cardBalance >= amount && amount <= loanDebt) || amount == loanDebt) {
            creditService.cashInToCreditFromCard(id, amount, cardNumber);
            float newCardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
            float newLoanDebt = creditService.getCreditById(id).get().getLoanDebt();
            return Map.of("Credit balance", newLoanDebt,
                    "Card balance", newCardBalance);
        } else {
            throw new CreditPaidException("Credit paid error");
        }
    }

    @PatchMapping("/{id}/cash-out")
    @Operation(summary = "Cash out from a credit to a card", responses = {
            @ApiResponse(responseCode = "200", description = "Cash out successful"),
            @ApiResponse(responseCode = "404", description = "Credit or card not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Map<String, Float> cashOut(
            @Parameter(description = "ID of the credit to cash out", required = true)
            @PathVariable int id,
            @Parameter(description = "Amount to cash out", required = true)
            @RequestParam float amount,
            @Parameter(description = "Card details to cash out to", required = true)
            @RequestBody CardTransDTO cardTransDTO) {
        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        creditService.cashOutFromCreditToCard(id, amount, cardNumber);
        float cardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
        float loanDebt = creditService.getCreditById(id).get().getLoanDebt();
        return Map.of("Credit balance", loanDebt,
                "Card balance", cardBalance);
    }

    @ExceptionHandler
    @Operation(summary = "Handle CreditPaidException", responses = {
            @ApiResponse(responseCode = "400", description = "Credit already paid")
    })
    public ResponseEntity<CreditErrorResponse> handleException(CreditPaidException e) {
        CreditErrorResponse response = new CreditErrorResponse(
                "Credit already paid!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}