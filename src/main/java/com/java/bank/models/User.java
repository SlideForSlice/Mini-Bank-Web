package com.java.bank.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
@Table(name="user_cred")
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="username")
    @Size(min = 8, max=24, message = "Username should be between 8 to 24 symbols long")
    @NotNull(message = "username couldn't be empty")
    private String username;

    @Column(name="password")
    @Size(min = 8, max=24, message = "Password should be between 8 to 24 symbols long")
    @NotNull(message = "password couldn't be empty")
    private String password;

    @Column(name = "role")
    private String role;

}
