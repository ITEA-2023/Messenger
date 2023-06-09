package com.itea.messenger.dto.user.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserModificationRequest {
    private String password;
    private String firstName;
    private String secondName;
}