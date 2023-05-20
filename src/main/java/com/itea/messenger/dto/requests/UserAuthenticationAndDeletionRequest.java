package com.itea.messenger.dto.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAuthenticationAndDeletionRequest {
    private String username;
    private String password;
}