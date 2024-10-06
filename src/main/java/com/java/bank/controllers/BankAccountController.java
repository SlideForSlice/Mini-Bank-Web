package com.java.bank.controllers;

import com.java.bank.controllers.DTO.BankAccountDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import com.java.bank.models.Credit;
import com.java.bank.models.Deposit;
import com.java.bank.services.BankAccountService;
import com.java.bank.utils.MapperForDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

@RequestMapping("/bank-account-service")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Api(value = "Bank Account Service API", tags = {"Bank Account Service"})
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final MapperForDTO mapper;

    @GetMapping("/{id}")
    @ApiOperation(value = "Get bank account by ID", response = BankAccountDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved bank account"),
            @ApiResponse(code = 404, message = "Bank account not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public BankAccountDTO getBankAccount(
            @ApiParam(value = "ID of the bank account to retrieve", required = true)
            @PathVariable int id) {
        return mapper.convertToBankAccountDTO(bankAccountService.getBankAccountById(id).orElse(null));
    }

    @PatchMapping("/{id}/update")
    @ApiOperation(value = "Update bank account by ID", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Bank account updated successfully"),
            @ApiResponse(code = 404, message = "Bank account not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<HttpStatus> updateBankAccount(
            @ApiParam(value = "ID of the bank account to update", required = true)
            @PathVariable int id,
            @ApiParam(value = "Updated bank account details", required = true)
            @RequestBody BankAccount bankAccountUpdated) {
        bankAccountService.updateBankAccount(id, bankAccountUpdated);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    @ApiOperation(value = "Delete bank account by ID", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Bank account deleted successfully"),
            @ApiResponse(code = 404, message = "Bank account not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<HttpStatus> deleteBankAccount(
            @ApiParam(value = "ID of the bank account to delete", required = true)
            @PathVariable int id) {
        bankAccountService.deleteBankAccount(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}/get-cards")
    @ApiOperation(value = "Get all cards for a bank account", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved cards"),
            @ApiResponse(code = 404, message = "Bank account not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public List<Card> getAllCards(
            @ApiParam(value = "ID of the bank account to retrieve cards for", required = true)
            @PathVariable int id) {
        return bankAccountService.getAllCards(id);
    }

    @GetMapping("/{id}/get-deposits")
    @ApiOperation(value = "Get all deposits for a bank account", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved deposits"),
            @ApiResponse(code = 404, message = "Bank account not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public List<Deposit> getAllDeposits(
            @ApiParam(value = "ID of the bank account to retrieve deposits for", required = true)
            @PathVariable int id) {
        return bankAccountService.getAllDeposits(id);
    }

    @GetMapping("/{id}/get-credits")
    @ApiOperation(value = "Get all credits for a bank account", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved credits"),
            @ApiResponse(code = 404, message = "Bank account not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public List<Credit> getAllCredits(
            @ApiParam(value = "ID of the bank account to retrieve credits for", required = true)
            @PathVariable int id) {
        return bankAccountService.getAllCredits(id);
    }
}