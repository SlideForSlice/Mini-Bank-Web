package com.java.bank.services;

import com.java.bank.controllers.DTO.BankAccountDTO;
import com.java.bank.models.Credit;
import com.java.bank.models.Deposit;
import com.java.bank.repositories.*;
import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final DepositRepository depositRepository;
    private final CreditService creditService;
    private final CreditRepository creditRepository;


    public Optional<BankAccount> getBankAccountById(int id) {

        return bankAccountRepository.findById(id);
    }

    @Transactional
    public void createBankAccount(BankAccount bankAccount) {

        bankAccountRepository.save(bankAccount);
    }

    @Transactional
    public void updateBankAccount(int id, BankAccount bankAccountUpdated) {
        BankAccount bankAccount = bankAccountRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("User not found"));

        if (bankAccountUpdated.getFullName() != null) {
            bankAccount.setFullName(bankAccountUpdated.getFullName());
        };

        if (bankAccountUpdated.getPassportNumber() != null) {
            bankAccount.setPassportNumber(bankAccountUpdated.getPassportNumber());
        }

        if (bankAccountUpdated.getDateOfBirth() != null) {
            bankAccount.setDateOfBirth(bankAccountUpdated.getDateOfBirth());
        }

        if (bankAccountUpdated.getAddress() != null) {
            bankAccount.setAddress(bankAccountUpdated.getAddress());
        }

        if (bankAccountUpdated.getPhoneNumber() != null) {
            bankAccount.setPhoneNumber(bankAccountUpdated.getPhoneNumber());
        }

        if (bankAccountUpdated.getEmail() != null) {
            bankAccount.setEmail(bankAccountUpdated.getEmail());
        }


        bankAccountRepository.save(bankAccount);
    }

    @Transactional
    public void deleteBankAccount(int  id) {

        bankAccountRepository.deleteById(id);
    }

    public List<Card> getAllCards(int id) {
        return cardRepository.findAllByBankAccount(bankAccountRepository.findById(id).get());
    }

    public List<Deposit> getAllDeposits(int id) {
        return depositRepository.findAllByBankAccount(bankAccountRepository.findById(id).get());
    }

    public List<Credit> getAllCredits(int id) {
        return creditRepository.findAllByBankAccount(bankAccountRepository.findById(id).get());
    }

    public int findBankAccountIdByUserId(int userId) {
        // Предположим, что у вас есть метод в репозитории, который возвращает банковский аккаунт по userId
        BankAccount bankAccount = bankAccountRepository.findByUserId(userId);
        if (bankAccount == null) {
            throw new RuntimeException("Bank account not found for user ID: " + userId);
        }
        return bankAccount.getId();
    }


}
