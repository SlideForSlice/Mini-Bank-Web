package com.java.bank.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.java.bank.DTO.BankAccountDTO;
import com.java.bank.DTO.RegDTO;
import com.java.bank.DTO.UserDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.User;
import com.java.bank.security.JWTUtil;
import com.java.bank.services.BankAccountService;
import com.java.bank.services.RegistrationService;
import com.java.bank.utils.BankAccountValidator;
import com.java.bank.utils.UserErrorResponse;
import com.java.bank.utils.UserValidator;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserValidator userValidator;
    private final BankAccountValidator bankAccountValidator;
    private final ModelMapper modelMapper;
    private final BankAccountService bankAccountService;

    @Autowired
    public AuthController(RegistrationService registrationService, JWTUtil jwtUtil,
                          AuthenticationManager authenticationManager, UserValidator userValidator,
                          BankAccountValidator bankAccountValidator, ModelMapper modelMapper,
                          BankAccountService bankAccountService) {
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userValidator = userValidator;
        this.bankAccountValidator = bankAccountValidator;
        this.modelMapper = modelMapper;
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") User user,
                               @ModelAttribute("user") BankAccount userAccount) {
        return ("auth/registration");
    }

    @PostMapping("/registration")
    public String performRegistration(@RequestBody RegDTO regDTO,
                                          @Valid UserDTO userDTO,
                                              @Valid BankAccountDTO userAccountDTO,
                                              BindingResult bindingResult) {
        User user = convertToUser(userDTO);
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.append(fieldError.getField()).append(" : ").append(fieldError.getDefaultMessage()).append("\n");
            }
            throw new ValidationException(errors.toString());
        }
        BankAccount bankAccount = convertToBankAccount(userAccountDTO);
        bankAccountValidator.validate(bankAccount, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.append(fieldError.getField()).append(" : ").append(fieldError.getDefaultMessage()).append("\n");
            }
            throw new ValidationException(errors.toString());
        }
        registrationService.register(user);
        bankAccountService.saveBankAccount(bankAccount);
        jwtUtil.generateToken(user.getUsername());
        return HttpStatus.CREATED.toString();
    }

    @PostMapping("/login")
    public String performLogin(@RequestBody @Valid UserDTO userDTO){
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword());
        try {
            authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            return HttpStatus.UNAUTHORIZED.toString();
        }
        jwtUtil.generateToken(userDTO.getUsername());
        return HttpStatus.OK.toString();
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(JWTVerificationException e) {
        UserErrorResponse response = new UserErrorResponse(
                "Token is not generated!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public User convertToUser(UserDTO userDTO) {
        return this.modelMapper.map(userDTO, User.class);
    }
    public BankAccount convertToBankAccount(BankAccountDTO bankAccountDTO) {
        return this.modelMapper.map(bankAccountDTO, BankAccount.class);
    }

}
