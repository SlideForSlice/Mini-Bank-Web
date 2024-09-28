package com.java.bank.models;

import com.java.bank.models.enums.CreditStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="credit")
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account", referencedColumnName = "id")
    private BankAccount bankAccount;

    @Column(name="credit_num")
    private String creditNumber;

    @Column(name="loan_debt")
    private float loanDebt;

    @Column(name="interest")
    private float interestRate;

    @Column(name="open_date")
    private LocalDateTime openDate;

    @Column(name="end_date")
    private LocalDateTime endDate;

    @Column(name="status")
    private CreditStatus credit_status;


    public Credit(String creditNumber, float loanDebt, float interestRate, CreditStatus credit_status, LocalDateTime endDate) {
        this.creditNumber = creditNumber;
        this.loanDebt = loanDebt;
        this.interestRate = interestRate;
        this.credit_status = credit_status;
        this.endDate = endDate;
    }
}
