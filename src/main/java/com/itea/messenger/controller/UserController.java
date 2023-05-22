package com.itea.messenger.controller;

import com.itea.messenger.dto.user.requests.UserAuthenticationAndDeletionRequest;
import com.itea.messenger.dto.user.requests.UserModificationRequest;
import com.itea.messenger.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long userId, @RequestBody UserAuthenticationAndDeletionRequest userAuthenticationAndDeletionRequest) {
        try {
            return ResponseEntity.ok(userService.delete(userId, userAuthenticationAndDeletionRequest));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PatchMapping("/modify/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long userId, @RequestBody UserModificationRequest userModificationRequest) {
        try {
            return ResponseEntity.ok(userService.update(userId, userModificationRequest));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}