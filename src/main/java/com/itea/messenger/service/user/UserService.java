package com.itea.messenger.service.user;

import com.itea.messenger.dto.user.UserDto;
import com.itea.messenger.dto.user.requests.UserAuthenticationAndDeletionRequest;
import com.itea.messenger.dto.user.requests.UserModificationRequest;
import com.itea.messenger.dto.user.responses.UserDeletionResponse;
import com.itea.messenger.dto.user.responses.UserModificationResponse;

import java.util.List;

public interface UserService {
    UserDeletionResponse delete(long userId, UserAuthenticationAndDeletionRequest userAuthenticationAndDeletionRequest);
    UserModificationResponse update(long userId, UserModificationRequest userModificationRequest);
    List<UserDto> getAllUsers();
}