package com.java.bank.controllers.DTO;

import com.java.bank.models.Card;
import com.java.bank.models.Credit;
import com.java.bank.models.Deposit;
import com.java.bank.models.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BankAccountDTO {


    @NotNull(message = "name couldn't be empty")
    @Size(min = 20, max = 150, message = "name should be from 20 to 150 symbols long")
    @Pattern(regexp = "^[A-Z][a-z]+ [A-Z][a-z]+ [A-Z][a-z]+$", message = "use pattern 'Ivanov Ivan Ivanovich'")
    private String fullName;

    @NotNull(message = "passport number couldn't be empty")
    @Size(min = 6, max = 6, message = "passport number should contain only 6 numbers")
    @Pattern(regexp = "^\\d{6}$", message = "passport number should contain only 6 numbers")
    private String passportNumber;

//    @NotNull(message = "date of birth couldn't be empty")
//    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "enter date in YYYY-MM-DD format")
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "date of birth couldn't be in future")
    private LocalDate dateOfBirth;

    @NotNull(message = "address couldn't be empty")
    @Size(min = 10, max = 300, message = "Enter correct address")
    private String address;

    @NotNull(message = "phone number couldn't be empty")
    @Pattern(regexp = "^9\\d{9}$", message = "Wrong number format. Enter your phone number without +7 or 8")
    private String phoneNumber;

    @NotNull(message = "email couldn't be empty")
    @Email(message = "Email is not valid")
    private String email;

//    private User userId;
//
//
//    private List<Credit> creditList;
//
//    private List<Card> cardList;
//
//    private List<Deposit> depositList;
}
