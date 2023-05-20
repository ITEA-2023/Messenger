package com.itea.messenger.controller;

import com.itea.messenger.dto.requests.UserAuthenticationAndDeletionRequest;
import com.itea.messenger.dto.requests.UserRegistrationRequest;
import com.itea.messenger.service.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        try {
            return ResponseEntity.ok(authenticationService.register(userRegistrationRequest));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody UserAuthenticationAndDeletionRequest userAuthenticationAndDeletionRequest) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(userAuthenticationAndDeletionRequest));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}