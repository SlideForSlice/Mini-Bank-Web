package com.java.bank.utils;

import com.java.bank.models.BankAccount;
import com.java.bank.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

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

//    @Override
//    public void validate(Object target, Errors errors) {
//
//        BankAccount bankAccount = (BankAccount) target;
//        if (bankAccountRepository.findByPassportNumber(String.valueOf(bankAccount.getPassportNumber())).isPresent()) {
//            errors.rejectValue("passportNumber", "", "Passport number already exists");
//        }
//        if (bankAccountRepository.findByPhoneNumber(String.valueOf(bankAccount.getPhoneNumber())).isPresent()) {
//            errors.rejectValue("phoneNumber", "", "Phone number already exists");
//        }
//        if (bankAccountRepository.findByEmail(bankAccount.getEmail()).isPresent()) {
//            errors.rejectValue("email", "", "Email already exists");
//        }
//
//    }

    @Override
    public void validate(Object target, Errors errors) {
        BankAccount bankAccount = (BankAccount) target;

        // Проверка на уникальность passportNumber
        if (bankAccountRepository.findByPassportNumber(bankAccount.getPassportNumber()).isPresent()) {
            errors.rejectValue("passportNumber", "", "Passport number already exists");
        }

        // Проверка на уникальность phoneNumber
        if (bankAccountRepository.findByPhoneNumber(bankAccount.getPhoneNumber()).isPresent()) {
            errors.rejectValue("phoneNumber", "", "Phone number already exists");
        }

        // Проверка на уникальность email
        if (bankAccountRepository.findByEmail(bankAccount.getEmail()).isPresent()) {
            errors.rejectValue("email", "", "Email already exists");
        }

        // Проверка fullName
        if (bankAccount.getFullName() == null || bankAccount.getFullName().isEmpty()) {
            errors.rejectValue("fullName", "", "Full name couldn't be empty");
        } else if (bankAccount.getFullName().length() < 20 || bankAccount.getFullName().length() > 150) {
            errors.rejectValue("fullName", "", "Full name should be from 20 to 150 symbols long");
        } else if (!bankAccount.getFullName().matches("^[A-Z][a-z]+ [A-Z][a-z]+ [A-Z][a-z]+$")) {
            errors.rejectValue("fullName", "", "Use pattern 'Ivanov Ivan Ivanovich'");
        }

        // Проверка passportNumber
        if (bankAccount.getPassportNumber() == null || bankAccount.getPassportNumber().isEmpty()) {
            errors.rejectValue("passportNumber", "", "Passport number couldn't be empty");
        } else if (bankAccount.getPassportNumber().length() != 6 || !bankAccount.getPassportNumber().matches("^\\d{6}$")) {
            errors.rejectValue("passportNumber", "", "Passport number should contain only 6 numbers");
        }

        // Проверка dateOfBirth
        if (bankAccount.getDateOfBirth() == null) {
            errors.rejectValue("dateOfBirth", "", "Date of birth couldn't be empty");
        } else if (bankAccount.getDateOfBirth().isAfter(LocalDate.now())) {
            errors.rejectValue("dateOfBirth", "", "Date of birth couldn't be in future");
        }

        // Проверка address
        if (bankAccount.getAddress() == null || bankAccount.getAddress().isEmpty()) {
            errors.rejectValue("address", "", "Address couldn't be empty");
        } else if (bankAccount.getAddress().length() < 10 || bankAccount.getAddress().length() > 300) {
            errors.rejectValue("address", "", "Enter correct address");
        }

        // Проверка phoneNumber
        if (bankAccount.getPhoneNumber() == null || bankAccount.getPhoneNumber().isEmpty()) {
            errors.rejectValue("phoneNumber", "", "Phone number couldn't be empty");
        } else if (!bankAccount.getPhoneNumber().matches("^9\\d{9}$")) {
            errors.rejectValue("phoneNumber", "", "Wrong number format. Enter your phone number without +7 or 8");
        }
    }

}
