package com.java.bank.controllers;

import com.java.bank.controllers.DTO.PasswordDTO;
import com.java.bank.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/{id}/update-password")
    public ResponseEntity<HttpStatus> updatePassword(@RequestBody PasswordDTO passwordDTO,
                                                     @PathVariable int id) {
        userService.updatePassword(id, passwordDTO.getPassword());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable int id) {
        userService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
