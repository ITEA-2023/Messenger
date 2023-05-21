package com.itea.messenger.service.security;

import com.itea.messenger.dto.requests.UserAuthenticationAndDeletionRequest;
import com.itea.messenger.dto.requests.UserRegistrationRequest;
import com.itea.messenger.dto.responses.UserRegistrationAndAuthenticationResponse;

public interface AuthenticationService {
    UserRegistrationAndAuthenticationResponse register(UserRegistrationRequest userRegistrationRequest);
    UserRegistrationAndAuthenticationResponse authenticate(UserAuthenticationAndDeletionRequest userAuthenticationAndDeletionRequest);
}