package com.java.bank.controllers;

import com.java.bank.controllers.DTO.PasswordDTO;
import com.java.bank.security.JWTUtil;
import com.java.bank.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
@Tag(name = "C - User Details Service API", description = "User Service")
public class UserController {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    @PatchMapping("/update-password")
    @Operation(summary = "Update user password", responses = {@ApiResponse(responseCode = "200", description = "Password updated successfully"), @ApiResponse(responseCode = "404", description = "User not found"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public ResponseEntity<HttpStatus> updatePassword(
            @Parameter(description = "New password", required = true) @RequestBody PasswordDTO passwordDTO,
            @Parameter(description = "Token to extract user id from", required = true) @RequestHeader("Authorization") String token) {

        userService.updatePassword(jwtUtil.extractUserId(token.replace("Bearer ", "")), passwordDTO.getPassword());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete user by ID", responses = {@ApiResponse(responseCode = "200", description = "User deleted successfully"), @ApiResponse(responseCode = "404", description = "User not found"), @ApiResponse(responseCode = "500", description = "Internal server error")})
    public ResponseEntity<HttpStatus> deleteUser(
            @Parameter(description = "Token to extract user id from", required = true) @RequestHeader("Authorization") String token) {
        userService.delete(jwtUtil.extractUserId(token.replace("Bearer ", "")));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}