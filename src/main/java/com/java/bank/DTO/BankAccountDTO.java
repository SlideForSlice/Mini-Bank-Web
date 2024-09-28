package com.java.bank.DTO;

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
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BankAccountDTO {

    @NotEmpty(message = "Full name should not be empty!")
    private String fullName;
    @NotEmpty(message = "Passport number should not be empty!")
    private String passportNumber;

    @NotEmpty(message = "DOB should not be empty!")
    private LocalDate dateOfBirth;

    @NotEmpty(message = "Address should not be empty!")
    private String address;

    @NotEmpty(message = "Phone number should not be empty!")
    private String phoneNumber;

    @Email(message = "Email is not valid")
    @NotEmpty(message = "Email should not be empty!")
    private String email;

    private User userId;

    private List<Credit> creditList;

    private List<Card> cardList;

    private List<Deposit> depositList;
}
