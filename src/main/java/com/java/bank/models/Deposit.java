package com.java.bank.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.java.bank.models.enums.DepositStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "deposit")
@RequiredArgsConstructor
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "deposit_num")
    private String depositNum;

    @Column(name = "balance")
    private float balance;

    @Column(name = "interest")
    private float interest;

    @Column(name = "open_date")
    private LocalDate openDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DepositStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account", referencedColumnName = "id")
    @JsonBackReference
    private BankAccount bankAccount;

}
