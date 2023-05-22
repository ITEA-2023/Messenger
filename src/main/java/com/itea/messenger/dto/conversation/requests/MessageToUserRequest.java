package com.itea.messenger.dto.conversation.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageToUserRequest {
    private long userId;
    private String messageBody;
}