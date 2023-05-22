package com.itea.messenger.dto.user.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAuthenticationAndDeletionRequest {
    private String username;
    private String password;
}