package com.java.bank.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.java.bank.models.enums.CreditStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="credit")
@RequiredArgsConstructor
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account", referencedColumnName = "id")
    @JsonBackReference
    private BankAccount bankAccount;

    @Column(name="credit_num")
    private String creditNumber;

    @Column(name="loan_debt")
    private float loanDebt;

    @Column(name="interest")
    private float interestRate;

    @Column(name="open_date")
    private LocalDate openDate;

    @Column(name="end_date")
    private LocalDate endDate;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus;

}
