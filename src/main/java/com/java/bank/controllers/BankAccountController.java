package com.java.bank.controllers;

import com.java.bank.controllers.DTO.BankAccountDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import com.java.bank.services.BankAccountService;
import com.java.bank.utils.MapperForDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/bank-account-service")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final MapperForDTO mapper;

    @GetMapping("/{id}")
    public BankAccountDTO getBankAccount(@PathVariable int id) {
        return mapper.convertToBankAccountDTO(bankAccountService.getBankAccountById(id).orElse(null));
    }

    @PatchMapping("/{id}/update")
    public BankAccountDTO updateBankAccount(@PathVariable int id,
                                  @RequestBody BankAccount bankAccountUpdated) {
        bankAccountService.updateBankAccount(id, bankAccountUpdated);
        return mapper.convertToBankAccountDTO(bankAccountService.getBankAccountById(id).orElse(null));
    }

    // TODO сделать каскадное удаление
    @DeleteMapping("/{id}/delete")
    public void deleteBankAccount(@PathVariable int id) {

        bankAccountService.deleteBankAccount(id);
    }

    @GetMapping("/{id}/get-cards")
    public List<Card> getAllCards(@PathVariable int id){

        return bankAccountService.getAllCards(id);
    }


}
