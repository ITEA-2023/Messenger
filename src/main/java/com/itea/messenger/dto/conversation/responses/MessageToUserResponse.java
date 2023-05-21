package com.itea.messenger.dto.conversation.responses;

import com.itea.messenger.dto.conversation.MessageSendingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageToUserResponse {
    private long userId;
    private MessageSendingStatus messageSendingStatus;
}