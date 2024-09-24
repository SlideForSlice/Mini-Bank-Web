package com.java.bank.repositories;

import com.java.bank.models.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {

    List<BankAccount> findByFullName(String fullName);

}
