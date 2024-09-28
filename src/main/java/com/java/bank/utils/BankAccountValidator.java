package com.java.bank.utils;

import com.java.bank.models.BankAccount;
import com.java.bank.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class BankAccountValidator implements Validator {

    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountValidator(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return BankAccount.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        BankAccount bankAccount = (BankAccount) target;
        if (bankAccountRepository.findByPassportNumber(String.valueOf(bankAccount.getPassportNumber())).isPresent()) {
            errors.rejectValue("passportNumber", "", "Passport number already exists");
        }
        if (bankAccountRepository.findByPhoneNumber(String.valueOf(bankAccount.getPhoneNumber())).isPresent()) {
            errors.rejectValue("phoneNumber", "", "Phone number already exists");
        }
        if (bankAccountRepository.findByEmail(bankAccount.getEmail()).isPresent()) {
            errors.rejectValue("email", "", "Email already exists");
        }

    }


}
