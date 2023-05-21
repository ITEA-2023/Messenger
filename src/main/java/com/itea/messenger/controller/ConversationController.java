package com.itea.messenger.controller;

import com.itea.messenger.dto.conversation.requests.GroupConversationCreationRequest;
import com.itea.messenger.dto.conversation.requests.MessageToGroupRequest;
import com.itea.messenger.dto.conversation.requests.MessageToUserRequest;
import com.itea.messenger.service.conversation.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping("/conversation/group/create")
    public ResponseEntity<?> createGroupConversation(@RequestBody GroupConversationCreationRequest groupConversationCreationRequest) {
        try {
            return ResponseEntity.ok(conversationService.createGroupConversation(groupConversationCreationRequest));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PostMapping("/conversation/new/create")
    public ResponseEntity<?> createNewUserToUserConversation(@RequestBody MessageToUserRequest messageToUserRequest) {
        try {
            return ResponseEntity.ok(conversationService.createNewUserToUserConversation(messageToUserRequest));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PostMapping("/conversation/message/send")
    public ResponseEntity<?> sendMessageToExistingConversation(@RequestBody MessageToGroupRequest messageToGroupRequest) {
        try {
            return ResponseEntity.ok(conversationService.sendMessageToExistingConversation(messageToGroupRequest));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}