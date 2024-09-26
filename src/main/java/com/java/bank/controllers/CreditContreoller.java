package com.java.bank.controllers;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Credit;
import com.java.bank.services.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/credit-service")
@Controller
@RequiredArgsConstructor
public class CreditContreoller {

    private final CreditService creditService;

    @GetMapping()
    public List<Credit> getAllCredits(BankAccount idBankAccount) {
        return creditService.getCreditsByBankAccount(idBankAccount);
        //TODO Добавить ссылку на страницу
    }
}
