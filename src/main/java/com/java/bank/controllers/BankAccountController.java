package com.java.bank.controllers;

import com.java.bank.controllers.DTO.BankAccountDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import com.java.bank.models.Credit;
import com.java.bank.models.Deposit;
import com.java.bank.security.JWTUtil;
import com.java.bank.services.BankAccountService;
import com.java.bank.utils.MapperForDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "JWT")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final MapperForDTO mapper;
    private final JWTUtil jwtUtil;
    private final MapperForDTO mapperForDTO;

    @GetMapping()
    @Operation(summary = "Get bank account")
    public BankAccountDTO getBankAccount(
            @Parameter(description = "Enter token to retrieve bank account id", required = true)
            @RequestHeader("Authorization") String token) {
        int id = jwtUtil.extractBankAccountId(token.replace("Bearer ", ""));
        return mapper.convertToBankAccountDTO(bankAccountService.getBankAccountById(id).orElse(null));
    }

    @PatchMapping("/update")
    @Operation(summary = "Update bank account by ID")
    public ResponseEntity<BankAccountDTO> updateBankAccount(
            @Parameter(description = "ID of the bank account to update", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Updated bank account details", required = true)
            @RequestBody BankAccountDTO bankAccountDTOUpdated) {
        int id = jwtUtil.extractBankAccountId(token.replace("Bearer ", ""));
        BankAccount updatedBankAccount = mapperForDTO.convertToBankAccount(bankAccountDTOUpdated);
        bankAccountService.updateBankAccount(id, updatedBankAccount);
        BankAccount bankAccount = bankAccountService.getBankAccountById(id).orElse(null);
        BankAccountDTO bankAccountDTO = mapper.convertToBankAccountDTO(bankAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(bankAccountDTO);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete bank account by ID")
    public ResponseEntity<HttpStatus> deleteBankAccount(
            @Parameter(description = "ID of the bank account to delete", required = true)
            @RequestHeader("Authorization") String token) {
        bankAccountService.deleteBankAccount(jwtUtil.extractBankAccountId(token.replace("Bearer ", "")));
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-cards")
    @Operation(summary = "Get all cards for a bank account")
    public List<Card> getAllCards(
            @Parameter(description = "ID of the bank account to retrieve cards for", required = true)
            @RequestHeader("Authorization") String token) {
        return bankAccountService.getAllCards(jwtUtil.extractBankAccountId(token.replace("Bearer ", "")));
    }

    @GetMapping("/get-deposits")
    @Operation(summary = "Get all deposits for a bank account")
    public List<Deposit> getAllDeposits(
            @Parameter(description = "ID of the bank account to retrieve deposits for", required = true)
            @RequestHeader("Authorization") String token) {
        return bankAccountService.getAllDeposits(jwtUtil.extractBankAccountId(token.replace("Bearer ", "")));
    }

    @GetMapping("/get-credits")
    @Operation(summary = "Get all credits for a bank account")
    public List<Credit> getAllCredits(
            @Parameter(description = "ID of the bank account to retrieve credits for", required = true)
            @RequestHeader("Authorization") String token) {
        return bankAccountService.getAllCredits(jwtUtil.extractBankAccountId(token.replace("Bearer ", "")));
    }
}