package com.java.bank.controllers.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private int id;

//    @NotEmpty(message = "Username should not be empty!")
//    @Size(min = 2, max = 50, message = "Username should be from 2 to 50 symbols")
    private String username;
    private String password;
    private String role;
}
