package com.java.bank.repositories;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Card;
import com.java.bank.models.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Integer> {

    List<Deposit> findByBankAccount(BankAccount bankAccount);

    Optional<Deposit> findByDepositNum(String depositNumber);

    List<Deposit> findByEndDateAfter(LocalDate date);

    List<Deposit> findAllByBankAccount(BankAccount bankAccount);
}
