package com.java.bank.controllers;

import com.java.bank.models.BankAccount;
import com.java.bank.services.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/deposit-service")
@RestController
@RequiredArgsConstructor
public class DepositController {
    private final DepositService depositService;

    @GetMapping
    public String getAllDeposits(BankAccount bankAccount) {
        depositService.getDepositsByBankAccount(bankAccount);
        return "";
    }

    @PostMapping("/create-deposit")
    public void createDeposit(@RequestBody BankAccount bankAccount) {
        depositService.createDeposit(bankAccount);
    }

    @DeleteMapping("/delete-deposit/{id}")
    public void deleteDeposit(@PathVariable int id) {
        depositService.deleteDeposit(id);
    }

    @PostMapping("/cash-in")
    public void cashIn(@RequestBody int id, float amount, String cardNumber) {
        depositService.cashInToDepositFromCard(id, amount, cardNumber);
    }

    @PostMapping("/cash-out")
    public void cashOut(@RequestBody int id, float amount, String cardNumber) {
        depositService.cashOutToCardFromDeposit(id, amount, cardNumber);
    }







}
