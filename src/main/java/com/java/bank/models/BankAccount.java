package com.java.bank.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name="bank_account")
public class BankAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="full_name")
    private String fullName;

    @Column(name="passport_number")
    private int passportNumber;

    @Column(name="date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name="address")
    private String address;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="email")
    private String email;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;




}
