package com.java.bank.models;

import com.java.bank.models.enums.CardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "card")
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
    private LocalDateTime openDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account", referencedColumnName = "id")
    private BankAccount bankAccount;


    public Card(String cardNumber, CardStatus status, float balance) {
        this.cardNumber = cardNumber;
        this.status = status;
        this.balance = balance;
    }

}
