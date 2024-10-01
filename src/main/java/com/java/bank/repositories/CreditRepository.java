package com.java.bank.repositories;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface CreditRepository extends JpaRepository<Credit, Integer> {
    List<Credit> findByBankAccount(BankAccount bankAccount);
    Optional<Credit> findByCreditNumber(String creditNumber);
    Optional<Credit> findAllByEndDateAfter(LocalDate endDate);
    List<Credit> findByEndDateAfter(LocalDate date);
}
