package com.java.bank.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="bank_account")
@RequiredArgsConstructor
public class BankAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="full_name")
    private String fullName;

    @Column(name="passport_number")
    private String passportNumber;

    @Column(name="date_of_birth")
    @DateTimeFormat
    private LocalDate dateOfBirth;

    @Column(name="address")
    private String address;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="email")
    @Email
    private String email;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Credit> creditList;

    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Card> cardList;

    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Deposit> depositList;
}
