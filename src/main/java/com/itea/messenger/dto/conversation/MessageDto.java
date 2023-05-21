package com.itea.messenger.dto.conversation;

import com.itea.messenger.entities.message.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private long senderId;
    private String senderUsername;
    private String messageBody;
    private MessageStatus messageStatus;
    private LocalDateTime createdAt;
}