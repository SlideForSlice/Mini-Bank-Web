package com.java.bank.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegDTO {
    private UserDTO user;
    private BankAccountDTO bankAccount;

}
