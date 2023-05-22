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
public class MessageToGroupResponse {
    private long conversationId;
    private MessageSendingStatus messageSendingStatus;
}