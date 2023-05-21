package com.itea.messenger.dto.conversation.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GroupConversationCreationRequest {
    private String groupName;
    private List<String> groupMembersUsernames;
}