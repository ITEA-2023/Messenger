package com.itea.messenger.service.conversation;

import com.itea.messenger.dto.conversation.ConversationDto;
import com.itea.messenger.dto.conversation.MessageDto;
import com.itea.messenger.dto.conversation.requests.GroupConversationCreationRequest;
import com.itea.messenger.dto.conversation.requests.MessageToGroupRequest;
import com.itea.messenger.dto.conversation.requests.MessageToUserRequest;
import com.itea.messenger.dto.conversation.responses.GroupConversationCreationResponse;
import com.itea.messenger.dto.conversation.responses.MessageToGroupResponse;
import com.itea.messenger.dto.conversation.responses.MessageToUserResponse;

import java.util.List;

public interface ConversationService {
    GroupConversationCreationResponse createGroupConversation(GroupConversationCreationRequest groupConversationCreationRequest);
    MessageToGroupResponse sendMessageToConversation(MessageToGroupRequest messageSendingRequest);
    MessageToUserResponse createUserToUserConversation(MessageToUserRequest messageToNewUserRequest);
    List<ConversationDto> getAllConversations();
    List<ConversationDto> getAllConversationsWithNewMessages();
    List<MessageDto> getAllMessagesFromConversation(long conversationId);
}