package com.java.bank.controllers;

import com.java.bank.controllers.DTO.BankAccountDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import com.java.bank.models.Credit;
import com.java.bank.models.Deposit;
import com.java.bank.services.BankAccountService;
import com.java.bank.utils.MapperForDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RequestMapping("/bank-account-service")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Tag(name = "Bank Account Service API", description = "Bank Account Service")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final MapperForDTO mapper;

    @GetMapping("/{id}")
    @Operation(summary = "Get bank account by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved bank account"), @ApiResponse(responseCode = "404", description = "Bank account not found"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public BankAccountDTO getBankAccount(
            @Parameter(description = "ID of the bank account to retrieve", required = true) @PathVariable int id) {
        return mapper.convertToBankAccountDTO(bankAccountService.getBankAccountById(id).orElse(null));
    }

    @PatchMapping("/{id}/update")
    @Operation(summary = "Update bank account by ID", responses = {@ApiResponse(responseCode = "201", description = "Bank account updated successfully"), @ApiResponse(responseCode = "404", description = "Bank account not found"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public ResponseEntity<HttpStatus> updateBankAccount(
            @Parameter(description = "ID of the bank account to update", required = true) @PathVariable int id,
            @Parameter(description = "Updated bank account details", required = true) @RequestBody BankAccount bankAccountUpdated) {
        bankAccountService.updateBankAccount(id, bankAccountUpdated);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete bank account by ID", responses = {@ApiResponse(responseCode = "202", description = "Bank account deleted successfully"), @ApiResponse(responseCode = "404", description = "Bank account not found"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public ResponseEntity<HttpStatus> deleteBankAccount(
            @Parameter(description = "ID of the bank account to delete", required = true)
            @PathVariable int id) {
        bankAccountService.deleteBankAccount(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}/get-cards")
    @Operation(summary = "Get all cards for a bank account", responses = {@ApiResponse(responseCode = "200", description = "Successfully retrieved cards"), @ApiResponse(responseCode = "404", description = "Bank account not found"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public List<Card> getAllCards(
            @Parameter(description = "ID of the bank account to retrieve cards for", required = true)
            @PathVariable int id) {
        return bankAccountService.getAllCards(id);
    }

    @GetMapping("/{id}/get-deposits")
    @Operation(summary = "Get all deposits for a bank account", responses = {@ApiResponse(responseCode = "200", description = "Successfully retrieved deposits"), @ApiResponse(responseCode = "404", description = "Bank account not found"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public List<Deposit> getAllDeposits(
            @Parameter(description = "ID of the bank account to retrieve deposits for", required = true)
            @PathVariable int id) {
        return bankAccountService.getAllDeposits(id);
    }

    @GetMapping("/{id}/get-credits")
    @Operation(summary = "Get all credits for a bank account", responses = {@ApiResponse(responseCode = "200", description = "Successfully retrieved credits"), @ApiResponse(responseCode = "404", description = "Bank account not found"), @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Credit> getAllCredits(
            @Parameter(description = "ID of the bank account to retrieve credits for", required = true)
            @PathVariable int id) {
        return bankAccountService.getAllCredits(id);
    }
}