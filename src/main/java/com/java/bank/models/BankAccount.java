package com.java.bank.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
public class BankAccount{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="full_name")
    @NotNull(message = "name couldn't be empty")
    @Size(min = 20, max = 150, message = "name should be from 20 to 150 symbols long")
    @Pattern(regexp = "^[A-Z][a-z]+ [A-Z][a-z]+ [A-Z][a-z]+$", message = "use pattern 'Ivanov Ivan Ivanovich'")
    private String fullName;

    @Column(name="passport_number")
    @NotNull(message = "passport number couldn't be empty")
    @Size(min = 6, max = 6, message = "passport number should contain only 6 numbers")
    @Pattern(regexp = "^\\d{6}$", message = "passport number should contain only 6 numbers")
    private String passportNumber;

    @Column(name="date_of_birth")
    @NotNull(message = "date of birth couldn't be empty")
//    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "enter date in YYYY-MM-DD format")
    @Past(message = "date of birth couldn't be in future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Column(name="address")
    @NotNull(message = "address couldn't be empty")
    @Size(min = 10, max = 200, message = "Enter correct address")
    private String address;

    @Column(name="phone_number")
    @NotNull(message = "phone number couldn't be empty")
    @Pattern(regexp = "^9\\d{9}$", message = "Wrong number format. Enter your phone number without +7 or 8")
    private String phoneNumber;

    @Column(name="email")
    @Email(message = "Email is not valid")
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
