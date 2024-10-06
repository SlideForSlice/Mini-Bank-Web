package com.java.bank.controllers;

import com.java.bank.controllers.DTO.PasswordDTO;
import com.java.bank.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
@Api(value = "User Service API", tags = {"User Service"})
public class UserController {
    private final UserService userService;

    @PatchMapping("/{id}/update-password")
    @ApiOperation(value = "Update user password", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Password updated successfully"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<HttpStatus> updatePassword(
            @ApiParam(value = "New password", required = true)
            @RequestBody PasswordDTO passwordDTO,
            @ApiParam(value = "User ID", required = true)
            @PathVariable int id) {
        userService.updatePassword(id, passwordDTO.getPassword());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    @ApiOperation(value = "Delete user by ID", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User deleted successfully"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<HttpStatus> deleteUser(
            @ApiParam(value = "User ID", required = true)
            @PathVariable int id) {
        userService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
