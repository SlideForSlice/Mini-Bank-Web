package com.java.bank.repositories;

import com.java.bank.models.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {

    Optional<BankAccount> findByFullName(String fullName);
    Optional<BankAccount> findByPassportNumber(String passportNumber);
    Optional<BankAccount> findByPhoneNumber(String phoneNumber);
    Optional<BankAccount> findByEmail(String email);

}
