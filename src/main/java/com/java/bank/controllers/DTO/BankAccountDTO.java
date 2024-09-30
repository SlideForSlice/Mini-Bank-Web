package com.java.bank.controllers.DTO;

import com.java.bank.models.Card;
import com.java.bank.models.Credit;
import com.java.bank.models.Deposit;
import com.java.bank.models.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BankAccountDTO {


    @NotNull(message = "Full name should not be empty!")
    private String fullName;

    @NotNull(message = "Passport number should not be empty!")
    private String passportNumber;

    @NotNull(message = "DOB should not be empty!")
    private LocalDate dateOfBirth;

    @NotNull(message = "Address should not be empty!")
    private String address;

    @NotNull(message = "Phone number should not be empty!")
    private String phoneNumber;

    @Email(message = "Email is not valid")
    @NotNull(message = "Email should not be empty!")
    private String email;

    private User userId;

    private List<Credit> creditList;

    private List<Card> cardList;

    private List<Deposit> depositList;
}
