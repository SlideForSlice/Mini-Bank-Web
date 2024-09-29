package com.java.bank.models;

import com.java.bank.models.enums.DepositStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "deposit")
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
    private LocalDateTime openDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DepositStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account", referencedColumnName = "id")
    private BankAccount bankAccount;

    public Deposit(String depositNum, float balance, float interest, LocalDateTime endDate, DepositStatus status) {
        this.depositNum = depositNum;
        this.balance = balance;
        this.interest = interest;
        this.endDate = endDate;
        this.status = status;
    }
}
