package com.java.bank.controllers;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Credit;
import com.java.bank.services.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/credit-service")
@RestController
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @GetMapping()
    public String getAllCredits(BankAccount idBankAccount) {
        creditService.getCreditsByBankAccount(idBankAccount);
        //TODO Добавить ссылку на страницу
        return "";
    }

    @PostMapping("/create-credit")
    public void createCredit(@RequestBody BankAccount idBankAccount, int amount) {
        creditService.createCredit(idBankAccount, amount);
    }

    @DeleteMapping("/delete-credit/{id}")
    public void deleteCredit(@PathVariable int id) {
        creditService.deleteCredit(id);
    }

    @PostMapping("/cash-in")
    public void cashIn(@RequestBody int creditId, float amount, String cardNumber) {
        creditService.cashInToCreditFromCard(creditId, amount, cardNumber);
    }

    @PostMapping("/cash-out")
    public void cashOut(@RequestBody int creditId, float amount, String cardNumber) {
        creditService.cashOutFromCreditToCard(creditId, amount, cardNumber);
    }








}
