package com.java.bank.services;

import com.java.bank.repositories.CardRepository;
import com.java.bank.repositories.UserRepository;
import com.java.bank.models.BankAccount;
import com.java.bank.repositories.BankAccountRepository;
import com.java.bank.models.Card;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, UserRepository userRepository, CardRepository cardRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }


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


}
