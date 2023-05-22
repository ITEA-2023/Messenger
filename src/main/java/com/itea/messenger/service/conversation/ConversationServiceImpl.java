package com.itea.messenger.service.conversation;

import com.itea.messenger.dto.conversation.ConversationDto;
import com.itea.messenger.dto.conversation.GroupConversationCreationStatus;
import com.itea.messenger.dto.conversation.MessageDto;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public MessageToUserResponse createUserToUserConversation(MessageToUserRequest messageToNewUserRequest) {
        long userId = messageToNewUserRequest.getUserId();
        String messageBody = messageToNewUserRequest.getMessageBody();

        UserEntity messageReceiver = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("Unable to find user. User with id: " + userId + " doesn't exist")
        );

        UserEntity messageSender = getCurrentUserEntity();

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
                    .firstUserEntity(messageReceiver)
                    .secondUserEntity(messageSender)
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
    public MessageToGroupResponse sendMessageToConversation(MessageToGroupRequest messageSendingRequest) {
        long conversationId = messageSendingRequest.getConversationId();
        String messageBody = messageSendingRequest.getMessageBody();

        ConversationEntity conversationEntity = conversationRepository.findById(conversationId).orElseThrow(
                () -> new RuntimeException("Conversation with id: " + conversationId + " doesn't exist")
        );

        UserEntity messageSender = getCurrentUserEntity();

        checkIfUserIsInConversation(messageSender, conversationEntity);

        MessageEntity savedMessageEntity = sendMessageToUsersInConversation(conversationEntity, messageSender, messageBody);

        return MessageToGroupResponse.builder()
                .conversationId(savedMessageEntity.getConversationEntity().getId())
                .messageSendingStatus(MessageSendingStatus.SENT)
                .build();
    }

    @Override
    public List<ConversationDto> getAllConversations() {
        UserEntity userEntity = getCurrentUserEntity();
        List<ConversationEntity> userConversations = userEntity.getUserConversations();
        return getConversationsToReturn(userEntity, userConversations);
    }



    @Override
    public List<ConversationDto> getAllConversationsWithNewMessages() {
        UserEntity userEntity = getCurrentUserEntity();
        List<MessageStatusEntity> messageStatusEntities = messageStatusRepository.findAllByUserEntityAndMessageStatus(userEntity, MessageStatus.UNREAD);
        List<ConversationEntity> userConversations = new ArrayList<>();
        for (MessageStatusEntity messageStatusEntity : messageStatusEntities) {
            if (!messageStatusEntity.getMessageEntity().getUserEntity().equals(userEntity)) {
                ConversationEntity conversationEntity = messageStatusEntity.getMessageEntity().getConversationEntity();
                if (!userConversations.contains(conversationEntity)) {
                    userConversations.add(conversationEntity);
                }
            }
        }
        return getConversationsToReturn(userEntity, userConversations);
    }

    @Override
    public List<MessageDto> getAllMessagesFromConversation(long conversationId) {
        UserEntity userEntity = getCurrentUserEntity();

        ConversationEntity conversationEntity = conversationRepository.findById(conversationId).orElseThrow(
                () -> new RuntimeException("Unable to find conversation. Conversation with id: " + conversationId + " doesn't exist")
        );

        checkIfUserIsInConversation(userEntity, conversationEntity);

        List<MessageEntity> messageEntities = messageRepository.findAllByConversationEntity(conversationEntity);

        List<MessageDto> messageDtoList = new ArrayList<>();
        for (MessageEntity messageEntity : messageEntities) {
            UserEntity messageSender = messageEntity.getUserEntity();

            MessageStatusEntity messageStatusEntityForCurrentUser = messageStatusRepository.findByMessageEntityAndUserEntity(
                    messageEntity,
                    userEntity
            );

            MessageStatusEntity messageStatusEntityForMessageSender = messageStatusRepository.findByMessageEntityAndUserEntity(
                    messageEntity,
                    messageSender
            );

            messageDtoList.add(
                    MessageDto.builder()
                            .senderId(messageEntity.getUserEntity().getId())
                            .senderUsername(messageEntity.getUserEntity().getUsername())
                            .messageBody(messageEntity.getMessageBody())
                            .createdAt(messageEntity.getCreatedAt())
                            .messageStatus(messageStatusEntityForCurrentUser.getMessageStatus())
                            .build()
            );

            if (!messageStatusEntityForCurrentUser.equals(messageStatusEntityForMessageSender) &&
                    messageStatusEntityForCurrentUser.getMessageStatus().equals(MessageStatus.UNREAD)) {
                messageStatusEntityForCurrentUser.setMessageStatus(MessageStatus.READ);
                messageStatusEntityForMessageSender.setMessageStatus(MessageStatus.READ);
            }
        }

        return messageDtoList.stream().sorted(Comparator.comparing(MessageDto::getCreatedAt)).collect(Collectors.toList());
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

    private MessageToUserResponse sendMessageToUserToUserConversation(
            UserEntity messageReceiver,
            UserEntity messageSender,
            ConversationEntity conversation,
            String messageBody) {

        sendMessageToUsersInConversation(conversation, messageSender, messageBody);

        return MessageToUserResponse.builder()
                .userId(messageReceiver.getId())
                .messageSendingStatus(MessageSendingStatus.SENT)
                .build();
    }

    private MessageEntity sendMessageToUsersInConversation(
            ConversationEntity conversation,
            UserEntity sender,
            String messageBody) {

        List<UserEntity> userEntities = userRepository.findAllUserEntitiesByConversationEntityId(conversation.getId());
        MessageEntity messageEntity = MessageEntity.builder()
                .userEntity(sender)
                .conversationEntity(conversation)
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
        return messageRepository.save(messageEntity);
    }

    private List<ConversationDto> getConversationsToReturn(UserEntity userEntity, List<ConversationEntity> conversationEntities) {
        List<ConversationDto> conversations = new ArrayList<>();

        for (ConversationEntity conversationEntity : conversationEntities) {
            switch (conversationEntity.getConversationType()) {

                case GROUP -> {
                    GroupConversationEntity groupConversationEntity =
                            groupConversationRepository.findByConversationEntity(conversationEntity);
                    conversations.add(ConversationDto.builder()
                            .id(conversationEntity.getId())
                            .name(groupConversationEntity.getGroupName())
                            .build());
                }

                case PERSON_TO_PERSON -> {
                    UserToUserConversationEntity userToUserConversationEntity =
                            userToUserConversationRepository.findByConversationEntity(conversationEntity);

                    final String firstUsername = userToUserConversationEntity.getFirstUserEntity().getUsername();
                    final String secondUsername = userToUserConversationEntity.getSecondUserEntity().getUsername();

                    String conversationName = null;
                    if (userEntity.getUsername().equals(firstUsername)) {
                        conversationName = secondUsername;
                    } else if (userEntity.getUsername().equals(secondUsername)) {
                        conversationName = firstUsername;
                    }

                    conversations.add(ConversationDto.builder()
                            .id(conversationEntity.getId())
                            .name(conversationName)
                            .build());
                }
            }
        }
        return conversations;
    }

    private void checkIfUserIsInConversation(UserEntity userEntity, ConversationEntity conversationEntity) {
        if (!userEntity.getUserConversations().contains(conversationEntity)) {
            throw new RuntimeException("Permission denied. User " + userEntity.getUsername() +
                    " is not a member of conversation with id: " + conversationEntity.getId());
        }
    }

}