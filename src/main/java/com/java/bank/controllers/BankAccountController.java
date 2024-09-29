package com.java.bank.controllers;

import com.java.bank.controllers.DTO.BankAccountDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.User;
import com.java.bank.services.BankAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

@RequestMapping("/bank-account-service")
@RestController
@Slf4j
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final ModelMapper modelMapper;

    @GetMapping("/{idBankAccount}")
    public BankAccountDTO getBankAccount(@PathVariable int idBankAccount) {
        return convertToBankAccountDTO(bankAccountService.getBankAccountById(idBankAccount).orElse(null));
    }

    @PatchMapping("/{id}/update")
    public BankAccountDTO updateBankAccount(@PathVariable int id,
                                  @RequestBody BankAccount bankAccountUpdated) {
        bankAccountService.updateBankAccount(id, bankAccountUpdated);
        return convertToBankAccountDTO(bankAccountService.getBankAccountById(id).orElse(null));
    }

    @DeleteMapping("/{id}/delete")
    public void deleteBankAccount(@PathVariable int id) {

        bankAccountService.deleteBankAccount(id);
    }

    private BankAccountDTO convertToBankAccountDTO(BankAccount bankAccount) {
        return this.modelMapper.map(bankAccount, BankAccountDTO.class);
    }

    private BankAccount convertToBankAccount(BankAccountDTO bankAccountDTO) {
        return this.modelMapper.map(bankAccountDTO, BankAccount.class);
    }
}
