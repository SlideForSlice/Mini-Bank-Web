package com.java.bank.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.java.bank.controllers.DTO.BankAccountIdDTO;
import com.java.bank.controllers.DTO.CardTransDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.Credit;
import com.java.bank.repositories.CardRepository;
import com.java.bank.services.CardService;
import com.java.bank.services.CreditService;
import com.java.bank.utils.CreditErrorResponse;
import com.java.bank.utils.CreditPaidException;
import com.java.bank.utils.MapperForDTO;
import com.java.bank.utils.UserErrorResponse;
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

@RequestMapping("/credit-service")
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Api(value = "Credit Service API", tags = {"Credit Service"})
public class CreditController {

    private final CreditService creditService;
    private final MapperForDTO mapperForDTO;
    private final CardService cardService;
    private final CardRepository cardRepository;

    @GetMapping()
    @ApiOperation(value = "Get all credits for a bank account", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved credits"),
            @ApiResponse(code = 404, message = "Bank account not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public String getAllCredits(
            @ApiParam(value = "Bank account to retrieve credits for", required = true)
            BankAccount idBankAccount) {
        creditService.getCreditsByBankAccount(idBankAccount);
        return "";
    }

    @PostMapping("/create")
    @ApiOperation(value = "Create a new credit for a bank account", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Credit created successfully"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<HttpStatus> createCredit(
            @ApiParam(value = "Bank account ID to create credit for", required = true)
            @RequestBody BankAccountIdDTO idBankAccount,
            @ApiParam(value = "Credit term in months", required = true)
            @RequestParam int creditTerm) {
        int id = idBankAccount.getId();
        creditService.createCredit(id, creditTerm);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    @ApiOperation(value = "Delete a credit by ID", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Credit deleted successfully"),
            @ApiResponse(code = 404, message = "Credit not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<HttpStatus> deleteCredit(
            @ApiParam(value = "ID of the credit to delete", required = true)
            @PathVariable int id) {
        creditService.deleteCredit(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/cash-in")
    @ApiOperation(value = "Cash in to a credit from a card", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cash in successful"),
            @ApiResponse(code = 404, message = "Credit or card not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Map<String, Float> cashIn(
            @ApiParam(value = "ID of the credit to cash in", required = true)
            @PathVariable int id,
            @ApiParam(value = "Amount to cash in", required = true)
            @RequestParam float amount,
            @ApiParam(value = "Card details to cash in from", required = true)
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
    @ApiOperation(value = "Cash out from a credit to a card", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cash out successful"),
            @ApiResponse(code = 404, message = "Credit or card not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Map<String, Float> cashOut(
            @ApiParam(value = "ID of the credit to cash out", required = true)
            @PathVariable int id,
            @ApiParam(value = "Amount to cash out", required = true)
            @RequestParam float amount,
            @ApiParam(value = "Card details to cash out to", required = true)
            @RequestBody CardTransDTO cardTransDTO) {
        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        creditService.cashOutFromCreditToCard(id, amount, cardNumber);
        float cardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
        float loanDebt = creditService.getCreditById(id).get().getLoanDebt();
        return Map.of("Credit balance", loanDebt,
                "Card balance", cardBalance);
    }

    @ExceptionHandler
    @ApiOperation(value = "Handle CreditPaidException", response = CreditErrorResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Credit already paid")
    })
    public ResponseEntity<CreditErrorResponse> handleException(CreditPaidException e) {
        CreditErrorResponse response = new CreditErrorResponse(
                "Credit already paid!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}