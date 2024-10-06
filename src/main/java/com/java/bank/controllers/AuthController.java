package com.java.bank.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.java.bank.controllers.DTO.BankAccountDTO;
import com.java.bank.controllers.DTO.UserDTO;
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
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor()
@Api(value = "User Registration API", tags = {"User Registration and authentication"})
public class AuthController {

    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserValidator userValidator;
    private final BankAccountValidator bankAccountValidator;
    private final MapperForDTO mapper;
    private final BankAccountService bankAccountService;
    private final UserRepository userRepository;


//    @GetMapping("/login")
//    public String login() {
//        return "auth/login";
//    }
//
//    @GetMapping("/registration")
//    public String registration() {
//        return ("auth/registration");
//    }


    @PostMapping("/registration")
    @ApiOperation(value = "Регистрация юзера через логин и пароль", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully registered user"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Map<String, String> performRegistrationUserCreds(
            @ApiParam(value = "User data for registration (username + password)", required = true)
            @RequestBody @Valid UserDTO userDTO,
            BindingResult bindingResult
    ) {
        User user = mapper.convertToUser(userDTO);
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
        return Map.of("token", token);
    }

    @PostMapping("/registration/details")
    @ApiOperation(value = "Добавление банковских деталей пользователя", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Bank account details added successfully"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<HttpStatus> performRegistrationBankAccDetails(
            @ApiParam(value = "Bank account details", required = true)
            @RequestBody @Valid BankAccountDTO bankAccountDTO,
            BindingResult bindingResult,
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
    @ApiOperation(value = "Authenticate user and generate JWT token", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully authenticated"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Map<String, String> performLogin(
            @ApiParam(value = "User credentials (username + password)", required = true)
            @RequestBody UserDTO userDTO)
    {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword());
        try {
            authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            return Map.of("error", "incorrect username or password");
        }
        String token = jwtUtil.generateToken(userDTO.getUsername(), userDTO.getId());
        return Map.of("token", token);
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
