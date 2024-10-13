package com.java.bank.controllers.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthDTO {

    @Size(min = 8, max=24, message = "Username should be between 8 to 24 symbols long")
    @NotNull(message = "username couldn't be empty")
    private String username;

    @Size(min = 8, message = "Password should be more then 8 symbols long")
    @NotNull(message = "password couldn't be empty")
    private String password;
}
