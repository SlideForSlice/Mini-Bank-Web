package com.java.bank.controllers.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BankAccountIdDTO {

    @NotNull(message = "id should not be empty!")
    private int id;
}
