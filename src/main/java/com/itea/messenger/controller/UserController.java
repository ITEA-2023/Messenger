package com.itea.messenger.controller;

import com.itea.messenger.dto.requests.UserAuthenticationAndDeletionRequest;
import com.itea.messenger.dto.requests.UserModificationRequest;
import com.itea.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService authenticationService;

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long userId, @RequestBody UserAuthenticationAndDeletionRequest userAuthenticationAndDeletionRequest) {
        try {
            return ResponseEntity.ok(authenticationService.delete(userId, userAuthenticationAndDeletionRequest));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PatchMapping("/user/modify/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long userId, @RequestBody UserModificationRequest userModificationRequest) {
        try {
            return ResponseEntity.ok(authenticationService.update(userId, userModificationRequest));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

}