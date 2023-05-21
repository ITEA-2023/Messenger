package com.itea.messenger.service.conversation;

import com.itea.messenger.dto.conversation.GroupConversationCreationStatus;
import com.itea.messenger.dto.conversation.MessageSendingStatus;
import com.itea.messenger.dto.conversation.requests.GroupConversationCreationRequest;
import com.itea.messenger.dto.conversation.requests.MessageToGroupRequest;
import com.itea.messenger.dto.conversation.requests.MessageToUserRequest;
import com.itea.messenger.dto.conversation.responses.GroupConversationCreationResponse;
import com.itea.messenger.dto.conversation.responses.MessageToGroupResponse;
import com.itea.messenger.dto.conversation.responses.MessageToUserResponse;
import com.itea.messenger.entities.conversation.ConversationEntity;
import com.itea.messenger.entities.conversation.ConversationType;
import com.itea.messenger.entities.conversation.GroupConversationEntity;
import com.itea.messenger.entities.conversation.UserToUserConversationEntity;
import com.itea.messenger.entities.message.MessageEntity;
import com.itea.messenger.entities.message.MessageStatus;
import com.itea.messenger.entities.message.MessageStatusEntity;
import com.itea.messenger.entities.user.UserEntity;
import com.itea.messenger.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final GroupConversationRepository groupConversationRepository;
    private final UserToUserConversationRepository userToUserConversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageStatusRepository messageStatusRepository;

    @Override
    public GroupConversationCreationResponse createGroupConversation(GroupConversationCreationRequest groupConversationCreationRequest) {
        String groupName = groupConversationCreationRequest.getGroupName();
        List<String> groupMembersUsernames = groupConversationCreationRequest.getGroupMembersUsernames();

        UserEntity groupCreator = getCurrentUserEntity();

        List<UserEntity> groupMembers = new ArrayList<>();
        groupMembers.add(groupCreator);

        for (String groupMemberUsername : groupMembersUsernames) {
            if (!groupMemberUsername.equals(groupCreator.getUsername())) {
                UserEntity groupMember = userRepository.findByUsername(groupMemberUsername).orElseThrow(
                        () -> new UsernameNotFoundException("Unable to find user. User with username: " +
                                groupMemberUsername + " doesn't exist")
                );
                groupMembers.add(groupMember);
            }
        }

        ConversationEntity conversationEntity = ConversationEntity.builder()
                .createdAt(LocalDate.now())
                .conversationType(ConversationType.GROUP)
                .conversationMembers(groupMembers)
                .build();

        GroupConversationEntity groupConversationEntity = GroupConversationEntity.builder()
                .groupName(groupName)
                .creator(groupCreator)
                .conversationEntity(conversationEntity)
                .build();

        groupMembers.forEach(groupMember -> groupMember.getUserConversations().add(conversationEntity));
        userRepository.saveAll(groupMembers);
        conversationRepository.save(conversationEntity);
        GroupConversationEntity savedGroupConversationEntity = groupConversationRepository.save(groupConversationEntity);

        return GroupConversationCreationResponse.builder()
                .groupName(savedGroupConversationEntity.getGroupName())
                .groupConversationCreationStatus(GroupConversationCreationStatus.CREATED)
                .build();
    }

    @Override
    public MessageToUserResponse createNewUserToUserConversation(MessageToUserRequest messageToNewUserRequest) {
        long userId = messageToNewUserRequest.getUserId();
        String messageBody = messageToNewUserRequest.getMessageBody();

        UserEntity messageReceiver = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("Unable to find user. User with id: " + userId + " doesn't exist")
        );

        String messageSenderUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity messageSender = userRepository.findByUsername(messageSenderUsername).orElseThrow(
                () -> new UsernameNotFoundException("Unable to find user. User with username: " +
                        messageSenderUsername + " doesn't exist")
        );
        List<UserEntity> conversationMembers = List.of(
                messageReceiver,
                messageSender
        );

        ConversationEntity conversationEntity = findUserToUserConversation(messageReceiver, messageSender);

        if (conversationEntity == null) {
            conversationEntity = ConversationEntity.builder()
                    .createdAt(LocalDate.now())
                    .conversationType(ConversationType.PERSON_TO_PERSON)
                    .conversationMembers(conversationMembers)
                    .build();
            ConversationEntity savedConversationEntity = conversationRepository.save(conversationEntity);

            UserToUserConversationEntity userToUserConversationEntity = UserToUserConversationEntity.builder()
                    .firstUserConversationDisplayName(messageReceiver.getFirstName())
                    .secondUserConversationDisplayName(messageSender.getFirstName())
                    .conversationEntity(savedConversationEntity)
                    .build();
            userToUserConversationRepository.save(userToUserConversationEntity);
        }
        return sendMessageToUserToUserConversation(
                messageReceiver,
                messageSender,
                conversationEntity,
                messageBody
        );

    }

    @Override
    public MessageToGroupResponse sendMessageToExistingConversation(MessageToGroupRequest messageSendingRequest) {
        long conversationId = messageSendingRequest.getConversationId();
        String messageBody = messageSendingRequest.getMessageBody();

        ConversationEntity conversationEntity = conversationRepository.findById(conversationId).orElseThrow(
                () -> new RuntimeException("Conversation with id: " + conversationId + " doesn't exist")
        );

        String messageSenderUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity messageSenderEntity = userRepository.findByUsername(messageSenderUsername).orElseThrow(
                () -> new UsernameNotFoundException("Unable to find user. User with username: " +
                        messageSenderUsername + " doesn't exist")
        );

        List<UserEntity> userEntities = userRepository.findAllUserEntitiesByConversationEntityId(conversationEntity.getId());

        MessageEntity messageEntity = MessageEntity.builder()
                .userEntity(messageSenderEntity)
                .conversationEntity(conversationEntity)
                .messageBody(messageBody)
                .createdAt(LocalDateTime.now())
                .build();

        for (UserEntity userEntity : userEntities) {
            MessageStatusEntity messageStatusEntity = MessageStatusEntity.builder()
                    .messageEntity(messageEntity)
                    .userEntity(userEntity)
                    .messageStatus(MessageStatus.UNREAD)
                    .build();

            messageStatusRepository.save(messageStatusEntity);
        }

        MessageEntity savedMessageEntity = messageRepository.save(messageEntity);



        return MessageToGroupResponse.builder()
                .conversationId(savedMessageEntity.getConversationEntity().getId())
                .messageSendingStatus(MessageSendingStatus.SENT)
                .build();
    }


    private UserEntity getCurrentUserEntity() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Unable to find user. User with username: " +
                        username + " doesn't exist")
        );
    }

    private ConversationEntity findUserToUserConversation(UserEntity messageReceiver, UserEntity messageSender) {
        List<ConversationEntity> conversationEntities = messageReceiver.getUserConversations();
        List<ConversationEntity> messageSenderConversationEntities = messageSender.getUserConversations();
        ConversationEntity conversationEntityToFind = null;

        for (ConversationEntity conversationEntity : conversationEntities) {
            if (messageSenderConversationEntities.contains(conversationEntity) && conversationEntity.getConversationType().equals(ConversationType.PERSON_TO_PERSON)) {
                conversationEntityToFind = conversationEntity;
            }
        }

        return conversationEntityToFind;
    }

    private MessageToUserResponse sendMessageToUserToUserConversation(UserEntity messageReceiver, UserEntity messageSender, ConversationEntity existingConversation, String messageBody) {
        List<UserEntity> userEntities = userRepository.findAllUserEntitiesByConversationEntityId(existingConversation.getId());

        MessageEntity messageEntity = MessageEntity.builder()
                .userEntity(messageSender)
                .conversationEntity(existingConversation)
                .messageBody(messageBody)
                .createdAt(LocalDateTime.now())
                .build();

        for (UserEntity userEntity : userEntities) {
            MessageStatusEntity messageStatusEntity = MessageStatusEntity.builder()
                    .messageEntity(messageEntity)
                    .userEntity(userEntity)
                    .messageStatus(MessageStatus.UNREAD)
                    .build();

            messageStatusRepository.save(messageStatusEntity);
        }
        messageRepository.save(messageEntity);

        return MessageToUserResponse.builder()
                .userId(messageReceiver.getId())
                .messageSendingStatus(MessageSendingStatus.SENT)
                .build();
    }
}