package com.itea.messenger.service.security;

import com.itea.messenger.dto.user.requests.UserAuthenticationAndDeletionRequest;
import com.itea.messenger.dto.user.requests.UserRegistrationRequest;
import com.itea.messenger.dto.user.responses.UserRegistrationAndAuthenticationResponse;

public interface AuthenticationService {
    UserRegistrationAndAuthenticationResponse register(UserRegistrationRequest userRegistrationRequest);
    UserRegistrationAndAuthenticationResponse authenticate(UserAuthenticationAndDeletionRequest userAuthenticationAndDeletionRequest);
}