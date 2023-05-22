package com.itea.messenger.controller;

import com.itea.messenger.dto.conversation.requests.GroupConversationCreationRequest;
import com.itea.messenger.dto.conversation.requests.MessageToGroupRequest;
import com.itea.messenger.dto.conversation.requests.MessageToUserRequest;
import com.itea.messenger.service.conversation.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conversation")
public class ConversationController {
    private final ConversationService conversationService;
    @PostMapping("/group/create")
    public ResponseEntity<?> createGroupConversation(@RequestBody GroupConversationCreationRequest groupConversationCreationRequest) {
        try {
            return ResponseEntity.ok(conversationService.createGroupConversation(groupConversationCreationRequest));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PostMapping("/user/create")
    public ResponseEntity<?> createNewUserToUserConversation(@RequestBody MessageToUserRequest messageToUserRequest) {
        try {
            return ResponseEntity.ok(conversationService.createUserToUserConversation(messageToUserRequest));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PostMapping("/message/send")
    public ResponseEntity<?> sendMessageToExistingConversation(@RequestBody MessageToGroupRequest messageToGroupRequest) {
        try {
            return ResponseEntity.ok(conversationService.sendMessageToConversation(messageToGroupRequest));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllConversations() {
        return ResponseEntity.ok(conversationService.getAllConversations());
    }

    @GetMapping("/all/new")
    public ResponseEntity<?> getAllConversationsWithNewMessages() {
        return ResponseEntity.ok(conversationService.getAllConversationsWithNewMessages());
    }

    @GetMapping("/{conversationId}/messages/all")
    public ResponseEntity<?> getAllNewMessagesFromConversation(@PathVariable("conversationId") long conversationId) {
        return ResponseEntity.ok(conversationService.getAllMessagesFromConversation(conversationId));
    }
}