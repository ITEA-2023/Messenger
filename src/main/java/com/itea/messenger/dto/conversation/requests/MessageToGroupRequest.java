package com.itea.messenger.dto.conversation.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageToGroupRequest {
    private long conversationId;
    private String messageBody;
}