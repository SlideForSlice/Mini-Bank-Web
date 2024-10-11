package com.java.bank.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.java.bank.models.enums.CardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "card")
@RequiredArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Column(name = "card_num")
    private String cardNumber;

    @Column(name = "balance")
    private float balance;

    @Column(name = "open_date")
    private LocalDate openDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account", referencedColumnName = "id")
    @JsonBackReference
    private BankAccount bankAccount;



}
