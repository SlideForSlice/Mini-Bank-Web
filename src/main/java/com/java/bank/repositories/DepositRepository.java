package com.java.bank.repositories;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepositRepository extends JpaRepository<Deposit, Integer> {

    List<Deposit> findByBankAccount(BankAccount bankAccount);
}
