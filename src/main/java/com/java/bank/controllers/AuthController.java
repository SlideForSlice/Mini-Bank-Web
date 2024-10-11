package com.java.bank.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.java.bank.controllers.DTO.BankAccountDTO;
import com.java.bank.controllers.DTO.AuthDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.User;
import com.java.bank.repositories.UserRepository;
import com.java.bank.security.JWTUtil;
import com.java.bank.services.BankAccountService;
import com.java.bank.services.RegistrationService;
import com.java.bank.utils.BankAccountValidator;
import com.java.bank.utils.MapperForDTO;
import com.java.bank.utils.UserErrorResponse;
import com.java.bank.utils.UserValidator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "User Registration and authorisation API", description = "User Registration and authentication")
public class AuthController {

    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserValidator userValidator;
    private final BankAccountValidator bankAccountValidator;
    private final MapperForDTO mapper;
    private final BankAccountService bankAccountService;
    private final UserRepository userRepository;

    @PostMapping("/registration")
    @Operation(summary = "Регистрация юзера через логин и пароль")
    public Map<String, String> performRegistrationUserCreds(
            @Parameter(description = "User data for registration (username + password)", required = true)
            @RequestBody @Valid AuthDTO authDTO, BindingResult bindingResult
    ) {
        User user = mapper.convertToUser(authDTO);
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.append(fieldError.getField()).append(" : ").append(fieldError.getDefaultMessage()).append("\n");
            }
            throw new JWTVerificationException(errors.toString());
        }
        registrationService.register(user);
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        return Map.of("yourToken", token);
    }

    @PostMapping("/registration/details")
    @Operation(summary = "Добавление банковских деталей пользователя")
    public ResponseEntity<HttpStatus> performRegistrationBankAccDetails(
            @Parameter(description = "Bank account details", required = true)
            @RequestBody @Valid BankAccountDTO bankAccountDTO, BindingResult bindingResult,
            @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token is not provided");
        }

        String jwtToken = token.replace("Bearer ", "");
        System.out.println("Received Token: " + jwtToken);
        int id = jwtUtil.extractUserId(jwtToken);
        System.out.println("Extracted User ID: " + id);

        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        BankAccount bankAccount = mapper.convertToBankAccount(bankAccountDTO);
        bankAccount.setUserId(user);
        bankAccountValidator.validate(bankAccount, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.append(fieldError.getField()).append(" : ").append(fieldError.getDefaultMessage()).append("\n");
            }
            throw new JWTVerificationException(errors.toString());
        }
        bankAccountService.createBankAccount(bankAccount);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and generate JWT token")
    public Map<String, String> performLogin(
            @Parameter(description = "User credentials (username + password)", required = true)
            @RequestBody AuthDTO authDTO) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword());
        try {
            authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            return Map.of("error", "incorrect username or password");
        }
        int id = userRepository.findByUsername(authDTO.getUsername()).get().getId();
        String token = jwtUtil.generateToken(authDTO.getUsername(), id);
        return Map.of("yourToken", token);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(JWTVerificationException e) {
        UserErrorResponse response = new UserErrorResponse(
                "Token is not generated!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
