package com.itea.messenger.dto.conversation.responses;

import com.itea.messenger.dto.conversation.GroupConversationCreationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupConversationCreationResponse {
    private String groupName;
    private GroupConversationCreationStatus groupConversationCreationStatus;
}