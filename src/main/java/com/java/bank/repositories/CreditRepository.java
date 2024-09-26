package com.java.bank.repositories;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import com.java.bank.models.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditRepository extends JpaRepository<Credit, Integer> {
    List<Credit> findByBankAccount(BankAccount bankAccount);

}
