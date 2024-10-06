package com.java.bank.controllers;

import com.java.bank.controllers.DTO.BankAccountIdDTO;
import com.java.bank.controllers.DTO.CardTransDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.repositories.CardRepository;
import com.java.bank.services.DepositService;
import com.java.bank.utils.MapperForDTO;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.mapper.Mapper;
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

@RequestMapping("/deposit-service")
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Api(value = "Deposit Service API", tags = {"Deposit Service"})
public class DepositController {
    private final DepositService depositService;
    private final MapperForDTO mapperForDTO;
    private final CardRepository cardRepository;

    @GetMapping
    @ApiOperation(value = "Get all deposits for a bank account", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved deposits"),
            @ApiResponse(code = 404, message = "Bank account not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public String getAllDeposits(
            @ApiParam(value = "Bank account to retrieve deposits for", required = true)
            BankAccount bankAccount) {
        depositService.getDepositsByBankAccount(bankAccount);
        return "";
    }

    @PostMapping("/create")
    @ApiOperation(value = "Create a new deposit for a bank account", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Deposit created successfully"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<HttpStatus> createDeposit(
            @ApiParam(value = "Bank account ID to create deposit for", required = true)
            @RequestBody BankAccountIdDTO idBankAccount,
            @ApiParam(value = "Deposit term in months", required = true)
            @RequestParam int depositTerm) {
        int id = idBankAccount.getId();
        depositService.createDeposit(id, depositTerm);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    @ApiOperation(value = "Delete a deposit by ID", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deposit deleted successfully"),
            @ApiResponse(code = 404, message = "Deposit not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<HttpStatus> deleteDeposit(
            @ApiParam(value = "ID of the deposit to delete", required = true)
            @PathVariable int id) {
        depositService.deleteDeposit(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/cash-in")
    @ApiOperation(value = "Cash in to a deposit from a card", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cash in successful"),
            @ApiResponse(code = 404, message = "Deposit or card not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Map<String, Float> cashIn(
            @ApiParam(value = "ID of the deposit to cash in", required = true)
            @PathVariable int id,
            @ApiParam(value = "Amount to cash in", required = true)
            @RequestParam float amount,
            @ApiParam(value = "Card details to cash in from", required = true)
            @RequestBody CardTransDTO cardTransDTO) {
        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        float cardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
        if (cardBalance >= amount) {
            depositService.cashInToDepositFromCard(id, amount, cardNumber);
            float newCardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
            float newDepositBalance = depositService.getDepositById(id).get().getBalance();
            return Map.of("Deposit balance", newDepositBalance,
                    "Card balance", newCardBalance);
        } else {
            throw new RuntimeException("Not enough balance");
        }
    }

    @PatchMapping("/{id}/cash-out")
    @ApiOperation(value = "Cash out from a deposit to a card", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cash out successful"),
            @ApiResponse(code = 404, message = "Deposit or card not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Map<String, Float> cashOut(
            @ApiParam(value = "ID of the deposit to cash out", required = true)
            @PathVariable int id,
            @ApiParam(value = "Amount to cash out", required = true)
            @RequestParam float amount,
            @ApiParam(value = "Card details to cash out to", required = true)
            @RequestBody CardTransDTO cardTransDTO) {
        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        float depositBalance = depositService.getDepositById(id).get().getBalance();
        if (depositBalance >= amount) {
            depositService.cashOutToCardFromDeposit(id, amount, cardNumber);
            float newCardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
            float newDepositBalance = depositService.getDepositById(id).get().getBalance();
            return Map.of("Deposit balance", newDepositBalance,
                    "Card balance", newCardBalance);
        } else {
            throw new RuntimeException("Not enough deposit balance");
        }
    }
}
