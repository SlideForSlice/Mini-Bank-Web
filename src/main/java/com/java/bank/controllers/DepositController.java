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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/deposit-service")
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DepositController {
    private final DepositService depositService;
    private final MapperForDTO mapperForDTO;
    private final CardRepository cardRepository;

    @GetMapping
    public String getAllDeposits(BankAccount bankAccount) {
        depositService.getDepositsByBankAccount(bankAccount);
        return "";
    }

    @PostMapping("/create")
    public Map<String, String> createDeposit(@RequestBody BankAccountIdDTO idBankAccount,
                                             @RequestParam int depositTerm) {
        int id = idBankAccount.getId();
        depositService.createDeposit(id, depositTerm);
        return Map.of("status", "success");
    }

    @DeleteMapping("/{id}/delete")
    public void deleteDeposit(@PathVariable int id) {
        depositService.deleteDeposit(id);
    }

    @PatchMapping("/{id}/cash-in")
    public Map<String, Float> cashIn(@PathVariable int id,
                                     @RequestParam float amount,
                                     @RequestBody CardTransDTO cardTransDTO) {
        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        float cardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
        if(cardBalance >= amount) {
            depositService.cashInToDepositFromCard(id, amount, cardNumber);
            float newCardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
            float newDepositBalance = depositService.getDepositById(id).get().getBalance();
            return Map.of("Deposit balance", newDepositBalance,
                    "Card balance", newCardBalance);
        }
        else {
            throw new RuntimeException("Not enough balance");
        }
    }

    @PatchMapping("/{id}/cash-out")
    public Map<String, Float> cashOut(@PathVariable int id,
                        @RequestParam float amount,
                        @RequestBody CardTransDTO cardTransDTO) {
        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        float depositBalance = depositService.getDepositById(id).get().getBalance();
        if(depositBalance >= amount) {
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
