package com.java.bank.repositories;

import com.java.bank.models.BankAccount;
import com.java.bank.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {

    Optional<BankAccount> findByPassportNumber(String passportNumber);
    Optional<BankAccount> findByPhoneNumber(String phoneNumber);
    Optional<BankAccount> findByEmail(String email);
    BankAccount findByUserId(User user);
}
