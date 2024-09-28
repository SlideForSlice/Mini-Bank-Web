package com.java.bank.repositories;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DepositRepository extends JpaRepository<Deposit, Integer> {

    List<Deposit> findByBankAccount(BankAccount bankAccount);
}
