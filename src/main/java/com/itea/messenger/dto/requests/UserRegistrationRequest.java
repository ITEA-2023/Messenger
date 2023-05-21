package com.itea.messenger.dto.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegistrationRequest {
    private String username;
    private String password;
    private String firstName;
    private String secondName;
}