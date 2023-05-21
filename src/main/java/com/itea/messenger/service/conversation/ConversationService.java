package com.itea.messenger.service.conversation;

import com.itea.messenger.dto.conversation.requests.GroupConversationCreationRequest;
import com.itea.messenger.dto.conversation.requests.MessageToGroupRequest;
import com.itea.messenger.dto.conversation.requests.MessageToUserRequest;
import com.itea.messenger.dto.conversation.responses.GroupConversationCreationResponse;
import com.itea.messenger.dto.conversation.responses.MessageToGroupResponse;
import com.itea.messenger.dto.conversation.responses.MessageToUserResponse;

public interface ConversationService {
    GroupConversationCreationResponse createGroupConversation(GroupConversationCreationRequest groupConversationCreationRequest);
    MessageToGroupResponse sendMessageToExistingConversation(MessageToGroupRequest messageSendingRequest);
    MessageToUserResponse createNewUserToUserConversation(MessageToUserRequest messageToNewUserRequest);
}