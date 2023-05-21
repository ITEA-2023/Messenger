package com.itea.messenger.service;

import com.itea.messenger.dto.requests.UserAuthenticationAndDeletionRequest;
import com.itea.messenger.dto.requests.UserModificationRequest;
import com.itea.messenger.dto.responses.UserDeletionResponse;
import com.itea.messenger.dto.responses.UserModificationResponse;

public interface UserService {
    UserDeletionResponse delete(long userId, UserAuthenticationAndDeletionRequest userAuthenticationAndDeletionRequest);
    UserModificationResponse update(long userId, UserModificationRequest userModificationRequest);
}